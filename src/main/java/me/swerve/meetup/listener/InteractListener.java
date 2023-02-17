package me.swerve.meetup.listener;

import me.swerve.meetup.RiseMeetup;
import me.swerve.meetup.manager.MeetupManager;
import me.swerve.meetup.menu.board.PlayerMenu;
import me.swerve.meetup.player.MeetupPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class InteractListener implements Listener {

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        if (e.getItem().getType() == Material.GOLDEN_APPLE) if (e.getItem().isSimilar(MeetupManager.getInstance().getGoldenHead()))
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 1));

    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent e) {
        if (MeetupPlayer.getMeetupPlayers().get(e.getPlayer().getUniqueId()).getCurrentState() != MeetupPlayer.PlayerState.PLAYING) e.setCancelled(true);
        if (e.getItem() == null) return;
        if (e.getAction() == Action.LEFT_CLICK_AIR  || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.PHYSICAL) return;

        if (MeetupPlayer.getMeetupPlayers().get(e.getPlayer().getUniqueId()).getCurrentState() == MeetupPlayer.PlayerState.LOBBY) {
            MeetupPlayer player = MeetupPlayer.getMeetupPlayers().get(e.getPlayer().getUniqueId());

            if (e.getItem().getType() == Material.SKULL_ITEM) {
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cStats are disabled during the beta season."));
                return;
            }

            if (e.getItem().getType() == Material.NAME_TAG) {
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cLeaderboards are disabled during the beta season."));
                return;
            }

            return;
        }

        if (MeetupPlayer.getMeetupPlayers().get(e.getPlayer().getUniqueId()).getCurrentState() == MeetupPlayer.PlayerState.SPECTATING) if (e.getItem().getType() == Material.COMPASS) Bukkit.getPluginManager().registerEvents(new PlayerMenu(e.getPlayer(), false), RiseMeetup.getInstance());
    }


    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        if (MeetupPlayer.getMeetupPlayers().get(e.getPlayer().getUniqueId()).getCurrentState() != MeetupPlayer.PlayerState.SPECTATING) return;
        if (!e.getPlayer().hasPermission("uhc.modspectate")) return;

        if (e.getRightClicked() instanceof Player) {
            Player spec = e.getPlayer();
            Player rightClicked = (Player) e.getRightClicked();

            Inventory inv = Bukkit.createInventory(null, 45, rightClicked.getDisplayName() + "'s Inventory");
            for (int b = 0; b < 36; b++) inv.setItem(b, rightClicked.getInventory().getItem(b));

            ItemStack health = new ItemStack(Material.REDSTONE_BLOCK);
            ItemMeta meta = health.getItemMeta();

            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cHealth: " + (int) rightClicked.getHealth()));
            health.setItemMeta(meta);
            inv.setItem(36, health);

            if (rightClicked.getInventory().getHelmet()     != null) inv.setItem(38, rightClicked.getInventory().getHelmet());
            if (rightClicked.getInventory().getChestplate() != null) inv.setItem(39, rightClicked.getInventory().getChestplate());
            if (rightClicked.getInventory().getLeggings()   != null) inv.setItem(40, rightClicked.getInventory().getLeggings());
            if (rightClicked.getInventory().getBoots()      != null) inv.setItem(41, rightClicked.getInventory().getBoots());

            ItemStack potions = new ItemStack(Material.GLASS_BOTTLE);
            ItemMeta potionMeta = potions.getItemMeta();
            potionMeta.setDisplayName(ChatColor.YELLOW + "Active Potion Effects: ");

            ArrayList<String> list = new ArrayList<>();
            for (PotionEffect effect : rightClicked.getActivePotionEffects()) list.add(ChatColor.translateAlternateColorCodes('&', "&cPotion: " + effect.getType().getName().toLowerCase() + " Level: "  + effect.getAmplifier() + 1 + " Time Left: " + effect.getDuration()));

            potionMeta.setLore(list);
            potions.setItemMeta(potionMeta);
            inv.setItem(43, potions);

            spec.openInventory(inv);
        }
    }
}
