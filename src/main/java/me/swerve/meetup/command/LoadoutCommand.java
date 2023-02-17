package me.swerve.meetup.command;

import me.swerve.meetup.RiseMeetup;
import me.swerve.meetup.menu.board.LoadoutMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoadoutCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f[&6RiseMeetup&f] &fYou must be a player to use this command."));
            return false;
        }

        Player p = (Player) sender;
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f[&6RiseMeetup&f] &fOpening loadout menu..."));
        Bukkit.getPluginManager().registerEvents(new LoadoutMenu(p), RiseMeetup.getInstance());

        return false;
    }
}
