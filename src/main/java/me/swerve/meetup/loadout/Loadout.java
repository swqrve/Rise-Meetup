package me.swerve.meetup.loadout;

import lombok.Getter;
import me.swerve.meetup.manager.MeetupManager;
import me.swerve.meetup.player.MeetupPlayer;
import me.swerve.meetup.util.ItemCreator;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Loadout {
    @Getter private final ItemStack[] finalInventory = new ItemStack[36];
    @Getter private final ItemStack[] armor = new ItemStack[4];
    private final Random random = new Random();
    private ItemStack sword;
    private ItemStack bow;


    private int goldenApples = 5;
    private int goldenHeads = 2;

    protected final List<Integer> heldPiecesCounter = new ArrayList<>();
    float totalRating = 0;

    Map<Integer, String> armorMap = new HashMap<>();

    public Loadout(MeetupPlayer p) {
        armorMap.put(3, "HELMET");
        armorMap.put(2, "CHESTPLATE");
        armorMap.put(1, "LEGGINGS");
        armorMap.put(0, "BOOTS");

        createRandomLoadoutItems();
        createLoadout(p);
    }

    public void createLoadout(MeetupPlayer p) {
        ItemStack goldHead = MeetupManager.getInstance().getGoldenHead().clone();
        goldHead.setAmount(goldenHeads);

        HashMap<String, Integer> loadOutMap = p.getSaveManager().getLoadout();

        finalInventory[loadOutMap.get("SwordIndex")] = sword;
        finalInventory[loadOutMap.get("RodIndex")] = new ItemCreator(Material.FISHING_ROD, 1).getItem();
        finalInventory[loadOutMap.get("BowIndex")] = bow;
        finalInventory[loadOutMap.get("BlockOneIndex")] = new ItemCreator(Material.COBBLESTONE, 64).getItem();
        finalInventory[loadOutMap.get("LavaOneIndex")] = new ItemCreator(Material.LAVA_BUCKET, 1).getItem();
        finalInventory[loadOutMap.get("WaterOneIndex")] = new ItemCreator(Material.WATER_BUCKET, 1).getItem();
        finalInventory[loadOutMap.get("GoldenAppleIndex")] = new ItemCreator(Material.GOLDEN_APPLE, goldenApples).getItem();
        finalInventory[loadOutMap.get("GoldenHeadIndex")] = goldHead;
        finalInventory[loadOutMap.get("PickaxeIndex")] = new ItemCreator(Material.DIAMOND_PICKAXE, 1).getItem();
        finalInventory[loadOutMap.get("AxeIndex")] = new ItemCreator(Material.DIAMOND_AXE, 1).getItem();
        finalInventory[loadOutMap.get("LavaTwoIndex")] = new ItemCreator(Material.LAVA_BUCKET, 1).getItem();
        finalInventory[loadOutMap.get("WaterTwoIndex")] = new ItemCreator(Material.WATER_BUCKET, 1).getItem();
        finalInventory[loadOutMap.get("ArrowIndex")] = new ItemCreator(Material.ARROW, 64).getItem();
        finalInventory[loadOutMap.get("SteakIndex")] = new ItemCreator(Material.COOKED_BEEF, 64).getItem();
        finalInventory[loadOutMap.get("BlockTwoIndex")] = new ItemCreator(Material.WOOD, 64).getItem();
        finalInventory[loadOutMap.get("AnvilIndex")] = new ItemCreator(Material.ANVIL, 1).getItem();
        finalInventory[loadOutMap.get("EnchantIndex")] = new ItemCreator(Material.ENCHANTMENT_TABLE, 1).getItem();
        finalInventory[loadOutMap.get("XPIndex")] = new ItemCreator(Material.EXP_BOTTLE, 64).getItem();
    }

    public void createRandomLoadoutItems() {
        for (int i = 0; i < 2 + random.nextInt(3); i++) {
            int x = getUniqueRandomIntInRange(4, random);
            heldPiecesCounter.add(x);

            addDiamondArmor(x);
        }

        for (int i = 0; i < 4; i++) if (armor[i] == null) {
            armor[i] = new ItemCreator(Material.valueOf(("IRON_" + armorMap.get(i))), 1).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(3) + 1, true).getItem();
        }

        Material swordMaterial = Material.DIAMOND_SWORD;
        if ((random.nextInt(12) + 1) > totalRating) swordMaterial = Material.IRON_SWORD;
        if (25 > random.nextInt(100)) swordMaterial = Material.DIAMOND_SWORD;

        int swordEnchantLevel = random.nextInt(2) + 2;
        if (totalRating > 9.5) swordEnchantLevel = 1;
        if (totalRating < 5) swordEnchantLevel = 3;

        if (swordMaterial == Material.DIAMOND_SWORD) totalRating++;
        if (swordEnchantLevel == 3) totalRating += 2;

        sword = new ItemCreator(swordMaterial, 1).addEnchant(Enchantment.DAMAGE_ALL, swordEnchantLevel, true).getItem();
        bow = new ItemCreator(Material.BOW, 1).addEnchant(Enchantment.ARROW_DAMAGE, random.nextInt(3) + 1, true).getItem();

        if (totalRating > 10) {
            goldenApples += random.nextInt(3);
            goldenHeads += random.nextInt(2);
        } else {
            goldenApples += random.nextInt(6);
            goldenHeads += random.nextInt(4);
        }
    }

    public void addDiamondArmor(int piece) {
        int enchantLevel = random.nextInt(3) + 1;
        Material pieceMaterial = Material.valueOf(("DIAMOND_" + armorMap.get(piece)));

        armor[piece] = new ItemCreator(pieceMaterial, 1).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, enchantLevel, true).getItem();
        totalRating += Math.abs(enchantLevel - 1.5);

        switch (piece) {
            case 0:
            case 3:
                totalRating++;
                break;
            case 1:
            case 2:
                totalRating += 2;
                break;
        }
    }

    public int getUniqueRandomIntInRange(int range, Random random) {
        int randomNumber = random.nextInt(range);
        return heldPiecesCounter.contains(randomNumber) ? getUniqueRandomIntInRange(range, random) : randomNumber;
    }

}
