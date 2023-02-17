package me.swerve.meetup.runnable;

import me.swerve.meetup.manager.MeetupManager;
import me.swerve.meetup.player.MeetupPlayer;
import me.swerve.meetup.util.SitUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameStartRunnable extends BukkitRunnable {

    public void run() {
        if (MeetupManager.getInstance().getGame().getGameTime() <= 0) {
            unSitAllPlayers();

            for (Player player : Bukkit.getOnlinePlayers()) player.playSound(player.getLocation(), Sound.valueOf("ENDERDRAGON_GROWL"), .5f, 1);
            MeetupManager.getInstance().setCurrentGameState(MeetupManager.GameState.PLAYING);

            MeetupManager.getInstance().getGame().startGame();

            cancel();
        }

        MeetupManager.getInstance().getGame().setGameTime(MeetupManager.getInstance().getGame().getGameTime() - 1);
    }

    private void unSitAllPlayers() {
        for (MeetupPlayer player : MeetupPlayer.getMeetupPlayers().values()) if (player.getCurrentState() != MeetupPlayer.PlayerState.SPECTATING) SitUtil.unSitPlayer(player.getPlayerObject());
    }
}
