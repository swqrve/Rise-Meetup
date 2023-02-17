package me.swerve.meetup.listener;

import me.swerve.meetup.RiseMeetup;
import me.swerve.meetup.manager.MeetupManager;
import me.swerve.meetup.player.MeetupPlayer;
import me.swerve.meetup.player.logger.CombatLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ConnectionListener implements Listener {

    @EventHandler
    public void onPlayerConnect(PlayerLoginEvent e) {
        if (MeetupManager.getInstance().getCurrentGameState() == MeetupManager.GameState.WHITELISTED) {
            e.setKickMessage(ChatColor.translateAlternateColorCodes('&', "&f[&6Rise Meetup&f] This server is currently whitelisted."));
            e.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', "&7[&a+&7] &7" + e.getPlayer().getDisplayName()));

        new MeetupPlayer(e.getPlayer());
        for (MeetupPlayer p : MeetupPlayer.getMeetupPlayers().values()) p.hideSpectators();

        if (MeetupManager.getInstance().getCurrentGameState() == MeetupManager.GameState.STARTING) MeetupManager.getInstance().getGame().addGamePlayer(MeetupPlayer.getMeetupPlayers().get(e.getPlayer().getUniqueId()));
        if (MeetupManager.getInstance().getCurrentGameState() == MeetupManager.GameState.PLAYING) MeetupPlayer.getMeetupPlayers().get(e.getPlayer().getUniqueId()).setSpectator();

        if (CombatLogger.getLoggers().get(e.getPlayer().getUniqueId()) != null) MeetupPlayer.getMeetupPlayers().get(e.getPlayer().getUniqueId()).useCombatLogger();

        Bukkit.getScheduler().scheduleSyncDelayedTask(RiseMeetup.getInstance(), () -> { // TODO: Add it on scoreboard too
            Scoreboard healthTagBoard = e.getPlayer().getScoreboard();

            Objective name = healthTagBoard.registerNewObjective("name", "health");
            name.setDisplaySlot(DisplaySlot.BELOW_NAME);
            name.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4❤"));

            healthTagBoard.registerNewTeam("color");
            healthTagBoard.getTeam("color").setPrefix(ChatColor.translateAlternateColorCodes('&', "&c"));
            healthTagBoard.getTeam("color").addPlayer(e.getPlayer());

            for (Player player : Bukkit.getOnlinePlayers()) {
                e.getPlayer().getScoreboard().getTeam("color").addPlayer(player);
                player.getScoreboard().getTeam("color").addPlayer(e.getPlayer());
            }
        }, 10);


        if (MeetupManager.getInstance().getCurrentGameState() == MeetupManager.GameState.WAITING && Bukkit.getOnlinePlayers().size() >= MeetupManager.getInstance().getRequiredPlayerCount()) MeetupManager.getInstance().startMeetup();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        MeetupPlayer player = MeetupPlayer.getMeetupPlayers().get(e.getPlayer().getUniqueId());
        e.setQuitMessage(ChatColor.translateAlternateColorCodes('&', "&7[&c-&7] &7" + e.getPlayer().getDisplayName()));

        player.getSaveManager().saveInfo();

        if (MeetupManager.getInstance().getGame() != null) {
            MeetupManager.getInstance().getGame().getGamePlayers().remove(MeetupPlayer.getMeetupPlayers().get(e.getPlayer().getUniqueId()));
            if (MeetupPlayer.getMeetupPlayers().get(e.getPlayer().getUniqueId()).getCurrentState() == MeetupPlayer.PlayerState.PLAYING) Bukkit.getPluginManager().registerEvents(new CombatLogger(e.getPlayer()), RiseMeetup.getInstance());
        }

        MeetupPlayer.getMeetupPlayers().remove(e.getPlayer().getUniqueId());

        if (MeetupManager.getInstance().getCurrentGameState() == MeetupManager.GameState.STARTING) {
            if (player.getCurrentScatterLoc() == null) return;
            MeetupManager.getInstance().getScatterManager().getUsedScatterLocs().remove(player.getCurrentScatterLoc().getId());
        }

        player.getSaveManager().saveInfo();
    }
}
