package me.swerve.meetup.menu.board;

import me.swerve.meetup.manager.MeetupManager;
import me.swerve.meetup.menu.Menu;

import me.swerve.meetup.menu.Page;
import me.swerve.meetup.player.MeetupPlayer;
import me.swerve.meetup.util.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerMenu extends Menu {
    private final boolean hasPermission;
    public PlayerMenu(Player p, boolean hasPermission) {
        super("Showing Players", Menu.InventoryType.INTERACTABLE, PageInformation.MULTI_PAGE, new ItemCreator(Material.STAINED_GLASS_PANE, 1).setData(1).getItem());

        this.hasPermission = hasPermission;

        List<MeetupPlayer> playersWithinLimit = new ArrayList<>(MeetupManager.getInstance().getGame().getGamePlayers());
        if (!hasPermission) {
            playersWithinLimit.clear();
            MeetupManager.getInstance().getGame().getGamePlayers().forEach(player -> {
                if (player.getPlayerObject().getLocation().distance(new Location(Bukkit.getWorld("uhc_world"), 0, 60, 0)) < 100) playersWithinLimit.add(player);
            });
        }

        if (playersWithinLimit.size() < 1) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThere is nobody to display!"));
            HandlerList.unregisterAll(this);
            return;
        }

        int neededPages = (int) Math.ceil((float) playersWithinLimit.size() / 18);

        for (int i = 0; i < neededPages; i++) {
            Page page = new Page(18);

            for (int b = 0; b < 18; b++) {
                if (playersWithinLimit.size() < 1) break;
                MeetupPlayer player = playersWithinLimit.get(0);
                if (player.getPlayerObject() == null) continue;

                page.put(b, new ItemCreator(Material.SKULL_ITEM, 1).setName("&6" + player.getPlayerObject().getDisplayName()).addLore(Collections.singletonList("&7Click to teleport to the player!")).setOwner(player.getPlayerObject().getName()).getItem());
                playersWithinLimit.remove(0);
            }

            addPage(page);
        }

        updateInventory(p);
    }

    @Override
    public void clickedItem(Inventory inventory, InventoryClickEvent e, Page currentPage) {
        if (e.getCurrentItem().getType() != Material.SKULL_ITEM) return;

        String skullName = e.getCurrentItem().getItemMeta().getDisplayName();
        MeetupPlayer foundPlayer = null;

        for (MeetupPlayer player : MeetupManager.getInstance().getGame().getGamePlayers())  if (ChatColor.stripColor(player.getPlayerObject().getDisplayName()).equalsIgnoreCase(ChatColor.stripColor(skullName))) foundPlayer = player;

        if (foundPlayer == null || foundPlayer.getPlayerObject() == null) {
            unavailablePlayer(e.getWhoClicked());
            return;
        }

        if (hasPermission) {
            ((Player) e.getWhoClicked()).sendMessage(ChatColor.translateAlternateColorCodes('&', "&f[&6RiseUHC&f] &fYou've been teleported to " + foundPlayer.getPlayerObject().getDisplayName()));
            e.getWhoClicked().closeInventory();
            e.getWhoClicked().teleport(foundPlayer.getPlayerObject().getLocation());
            return;
        }

        if (foundPlayer.getPlayerObject().getLocation().distance(new Location(Bukkit.getWorld("uhc_world"), 0, 60, 0)) > 100) {
            unavailablePlayer(e.getWhoClicked());
        }
    }

    private void unavailablePlayer(HumanEntity e) {
        ((Player) e).sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis player is no longer available."));
        e.closeInventory();
    }

    @Override public void lastChance(Inventory inventory) { }

    @Override public void onClose(InventoryCloseEvent e) {}
}
