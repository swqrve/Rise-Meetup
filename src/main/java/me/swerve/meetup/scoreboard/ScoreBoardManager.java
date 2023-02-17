package me.swerve.meetup.scoreboard;

import assemble.AssembleAdapter;
import me.swerve.meetup.manager.MeetupManager;
import me.swerve.meetup.player.MeetupPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ScoreBoardManager implements AssembleAdapter {

    private final DecimalFormat df = new DecimalFormat("0.00");

    public String getTitle(Player player) { return ChatColor.translateAlternateColorCodes('&', "&6&lRise &7⎟ &fMeetup");
    }

    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();
        lines.add("&7&m-------------------------------");

        if (MeetupManager.getInstance().getCurrentGameState() == MeetupManager.GameState.WAITING) {
            lines.add("&6Online:");
            lines.add("&f" + Bukkit.getOnlinePlayers().size());
            lines.add("");
            lines.add("&6Players til Start:");
            lines.add("&f" + (MeetupManager.getInstance().getRequiredPlayerCount() - Bukkit.getOnlinePlayers().size()));
            lines.add("");
            lines.add("&7riseuhc.club");
        }

        if (MeetupManager.getInstance().getCurrentGameState() == MeetupManager.GameState.STARTING) {
            lines.add("&6Starting In:");
            lines.add("&f" + MeetupManager.getInstance().getGame().getGameTime());
            lines.add("");
            lines.add("&7riseuhc.club");
        }

        if (MeetupManager.getInstance().getCurrentGameState() == MeetupManager.GameState.PLAYING) {
            String result = String.format("%02d:%02d", (MeetupManager.getInstance().getGame().getGameTime() / 60) % 60, MeetupManager.getInstance().getGame().getGameTime() % 60);
            if (MeetupManager.getInstance().getGame().getGameTime() / 3600 != 0) result = String.format("%02d:%02d:%02d", MeetupManager.getInstance().getGame().getGameTime() / 3600, (MeetupManager.getInstance().getGame().getGameTime() / 60) % 60, MeetupManager.getInstance().getGame().getGameTime() % 60);
            lines.add("&6Duration: &f" + result);
            lines.add("&6Players: &7[&f" + MeetupManager.getInstance().getGame().getGamePlayers().size() + "&7]");
            lines.add("&6Kills: &f" + MeetupPlayer.getMeetupPlayers().get(player.getUniqueId()).getSaveManager().getSessionKills());
            // TODO: If you add DND, uncomment this!
            // if (MeetupPlayer.getMeetupPlayers().get(player.getUniqueId()).getCooldown() != null) lines.add("&6DND: &f" + TimeUtil.differenceInSeconds(UHCPlayer.getUhcPlayers().get(player.getUniqueId()).getCooldown(), new Date()) + "s");
            lines.add("&6Border: &7[&f" + MeetupManager.getInstance().getGame().getCurrentBorder() + "x" + MeetupManager.getInstance().getGame().getCurrentBorder() + "&7]");
            lines.add("");
            lines.add("&7riseuhc.club");
        }

        if (MeetupManager.getInstance().getCurrentGameState() == MeetupManager.GameState.ENDING) {
            lines.add("&6Winners:");
            lines.add("&f" + MeetupManager.getInstance().getGame().getWinnerName());
            lines.add("");
            lines.add("&6Kills:");
            lines.add("&f" + MeetupManager.getInstance().getGame().getWinnerKills());
            lines.add("");
            lines.add("&6Closing in:");
            lines.add("&f" + MeetupManager.getInstance().getGame().getSecondsTillRestart());
            lines.add("");
            lines.add("&7riseuhc.club");
        }

        lines.add("&7&m-------------------------------");

        List<String> toReturn = new ArrayList<>();
        lines.forEach(line -> toReturn.add(ChatColor.translateAlternateColorCodes('&', line)));
        return toReturn;
    }
}
