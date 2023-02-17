package me.swerve.meetup.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        String command = e.getMessage();
        if (e.getPlayer().hasPermission("uhc.commandbypass")) return;
        if (command.startsWith("/me") || command.startsWith("/pl") || command.startsWith("/bukkit") || command.startsWith("/version")) e.setCancelled(true);
    }
}
