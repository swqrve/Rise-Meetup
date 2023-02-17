package me.swerve.meetup.listener;

import me.swerve.meetup.player.MeetupPlayer;
import me.swerve.meetup.util.BorderUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        MeetupPlayer p = MeetupPlayer.getMeetupPlayers().get(e.getPlayer().getUniqueId());
        if (p.getCurrentState() == MeetupPlayer.PlayerState.PLAYING || p.getCurrentState() == MeetupPlayer.PlayerState.SPECTATING) BorderUtil.updatePlayer(e.getPlayer());
    }
}