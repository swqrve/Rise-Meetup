package me.swerve.meetup.listener;

import me.swerve.meetup.RiseMeetup;
import me.swerve.meetup.manager.MeetupManager;
import me.swerve.meetup.player.MeetupPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        MeetupPlayer player = MeetupPlayer.getMeetupPlayers().get(e.getEntity().getUniqueId());

        if (e.getEntity().getKiller() == null) e.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + e.getDeathMessage()));
        else e.setDeathMessage(ChatColor.translateAlternateColorCodes('&', "&c" + e.getEntity().getDisplayName() + " was slain by " + e.getEntity().getKiller().getDisplayName() + "."));

        Bukkit.getScheduler().scheduleSyncDelayedTask(RiseMeetup.getInstance(), () -> {
            player.getPlayerObject().spigot().respawn();
            MeetupManager.getInstance().getGame().getGamePlayers().remove(player);
            player.setSpectator();
        }, 1);
    }
}
