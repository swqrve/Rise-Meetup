package me.swerve.meetup.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class PortalListener implements Listener {

    @EventHandler
    public void onPlayerEnterPortal(PlayerPortalEvent e) {
        e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cNether is disabled."));
        e.setCancelled(true);
    }
}
