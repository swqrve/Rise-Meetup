package me.swerve.meetup.menu.board;

import me.swerve.meetup.manager.MeetupManager;
import me.swerve.meetup.menu.Menu;
import me.swerve.meetup.menu.Page;
import me.swerve.meetup.player.MeetupPlayer;
import me.swerve.meetup.util.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;

public class LoadoutMenu extends Menu {
    public LoadoutMenu(Player p) {
        super("Edit Loadout", InventoryType.INTERACTABLE, PageInformation.SINGLE_PAGE);

        MeetupPlayer player = MeetupPlayer.getMeetupPlayers().get(p.getUniqueId());

        ItemStack[] loadOutInventory = new ItemStack[36];
        HashMap<String, Integer> currentLoadout = player.getSaveManager().getLoadout();

        loadOutInventory[currentLoadout.get("SwordIndex")] = new ItemCreator(Material.DIAMOND_SWORD, 1).getItem();
        loadOutInventory[currentLoadout.get("RodIndex")] = new ItemCreator(Material.FISHING_ROD, 1).getItem();
        loadOutInventory[currentLoadout.get("BowIndex")] = new ItemCreator(Material.BOW, 1).getItem();
        loadOutInventory[currentLoadout.get("BlockOneIndex")] = new ItemCreator(Material.COBBLESTONE, 1).getItem();
        loadOutInventory[currentLoadout.get("LavaOneIndex")] = new ItemCreator(Material.LAVA_BUCKET, 1).getItem();
        loadOutInventory[currentLoadout.get("WaterOneIndex")] = new ItemCreator(Material.WATER_BUCKET, 1).getItem();
        loadOutInventory[currentLoadout.get("GoldenAppleIndex")] = new ItemCreator(Material.GOLDEN_APPLE, 1).getItem();
        loadOutInventory[currentLoadout.get("GoldenHeadIndex")] = MeetupManager.getInstance().getGoldenHead();
        loadOutInventory[currentLoadout.get("PickaxeIndex")] = new ItemCreator(Material.DIAMOND_PICKAXE, 1).getItem();
        loadOutInventory[currentLoadout.get("AxeIndex")] = new ItemCreator(Material.DIAMOND_AXE, 1).getItem();
        loadOutInventory[currentLoadout.get("LavaTwoIndex")] = new ItemCreator(Material.LAVA_BUCKET, 1).getItem();
        loadOutInventory[currentLoadout.get("WaterTwoIndex")] = new ItemCreator(Material.WATER_BUCKET, 1).getItem();
        loadOutInventory[currentLoadout.get("ArrowIndex")] = new ItemCreator(Material.ARROW, 1).getItem();
        loadOutInventory[currentLoadout.get("SteakIndex")] = new ItemCreator(Material.COOKED_BEEF, 1).getItem();
        loadOutInventory[currentLoadout.get("BlockTwoIndex")] = new ItemCreator(Material.WOOD, 1).getItem();
        loadOutInventory[currentLoadout.get("AnvilIndex")] = new ItemCreator(Material.ANVIL, 1).getItem();
        loadOutInventory[currentLoadout.get("EnchantIndex")] = new ItemCreator(Material.ENCHANTMENT_TABLE, 1).getItem();
        loadOutInventory[currentLoadout.get("XPIndex")] = new ItemCreator(Material.EXP_BOTTLE, 1).getItem();

        ItemStack[] organizedInventory = new ItemStack[36];
        for (int i = 0; i < 36; i++) {
            if (i < 9) {
                organizedInventory[i + 27] = loadOutInventory[i];
                continue;
            }

            organizedInventory[i - 9] = loadOutInventory[i];
        }

        Page page = new Page(36);
        for (int i = 0; i < 36; i++) {
            if (organizedInventory[i] == null) continue;
            page.put(i, organizedInventory[i]);
        }

        addPage(page);
        updateInventory(p);
    }

    @Override
    public void clickedItem(Inventory inventory, InventoryClickEvent e, Page currentPage) {
        if (e.getClickedInventory().getType() == org.bukkit.event.inventory.InventoryType.PLAYER) e.setCancelled(true);

        if (e.getAction().toString().contains("DROP") || e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY || e.getAction() == InventoryAction.UNKNOWN) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
        }

        if (e.getSlot() == -999 || e.getClickedInventory() == null) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
            Bukkit.broadcastMessage("Outside inventory!");
        }
    }

    @Override public void lastChance(Inventory inventory) { }

    @Override
    public void onClose(InventoryCloseEvent e) {
        ItemStack[] items = e.getInventory().getContents();
        HashMap<String, Integer> newLoadout = new HashMap<>();

        for (int i = 0; i < 36; i++) {
            if (items[i] == null) continue;
            if (items[i].getType() == Material.AIR) continue;

            String toAdd = "";
            switch (items[i].getType()) {
                case DIAMOND_SWORD:
                    toAdd = "SwordIndex";
                    break;
                case FISHING_ROD:
                    toAdd = "RodIndex";
                    break;
                case BOW:
                    toAdd = "BowIndex";
                    break;
                case COBBLESTONE:
                    toAdd = "BlockOneIndex";
                    break;
                case LAVA_BUCKET:
                    toAdd = "LavaOneIndex";
                    if (newLoadout.containsKey("LavaOneIndex")) toAdd = "LavaTwoIndex";
                    break;
                case WATER_BUCKET:
                    toAdd = "WaterOneIndex";
                    if (newLoadout.containsKey("WaterOneIndex")) toAdd = "WaterTwoIndex";
                    break;
                case GOLDEN_APPLE:
                    toAdd = "GoldenAppleIndex";
                    if (items[i].isSimilar(MeetupManager.getInstance().getGoldenHead())) toAdd = "GoldenHeadIndex";
                    break;
                case DIAMOND_PICKAXE:
                    toAdd = "PickaxeIndex";
                    break;
                case DIAMOND_AXE:
                    toAdd = "AxeIndex";
                    break;
                case ARROW:
                    toAdd = "ArrowIndex";
                    break;
                case COOKED_BEEF:
                    toAdd = "SteakIndex";
                    break;
                case WOOD:
                    toAdd = "BlockTwoIndex";
                    break;
                case ANVIL:
                    toAdd = "AnvilIndex";
                    break;
                case ENCHANTMENT_TABLE:
                    toAdd = "EnchantIndex";
                    break;
                case EXP_BOTTLE:
                    toAdd = "XPIndex";
                    break;
            }

            int index = i + 9;
            if (i >= 27) index -= 36;

            newLoadout.put(toAdd, index);
        }

        MeetupPlayer.getMeetupPlayers().get(e.getPlayer().getUniqueId()).getSaveManager().setLoadout(newLoadout);
    }
}
