package me.swerve.meetup.listener;

import me.swerve.meetup.player.MeetupPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ItemListener implements Listener {

    @EventHandler
    public void onItemBurn(EntityCombustEvent e) {
        if (e.getEntity().getType() == EntityType.DROPPED_ITEM) e.setCancelled(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (MeetupPlayer.getMeetupPlayers().get(e.getPlayer().getUniqueId()).getCurrentState() != MeetupPlayer.PlayerState.PLAYING) e.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent e) {
        if (MeetupPlayer.getMeetupPlayers().get(e.getPlayer().getUniqueId()).getCurrentState() != MeetupPlayer.PlayerState.PLAYING) e.setCancelled(true);
    }
}
