package me.swerve.meetup.player.logger;

import lombok.Getter;
import lombok.Setter;
import me.swerve.meetup.RiseMeetup;
import me.swerve.meetup.manager.MeetupManager;
import me.swerve.meetup.player.MeetupPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatLogger implements Listener {
    @Getter private static final Map<UUID, CombatLogger> loggers = new HashMap<>();

    private final Player loggedPlayer;
    @Getter private Villager logger;

    @Getter private final ItemStack[] contents;
    @Getter private final ItemStack[] armorContents;

    @Getter private final Location spawnLocation;

    @Getter @Setter private boolean dead = false;
    private final String playerName;

    private float health;

    public CombatLogger(Player p) {
        this.loggedPlayer = p;

        this.contents = p.getInventory().getContents();
        this.armorContents = p.getInventory().getArmorContents();

        this.playerName = p.getDisplayName();

        this.spawnLocation = p.getLocation();
        this.health = (float) p.getHealth();

        createLogger();

        loggers.put(p.getUniqueId(), this);

        Bukkit.getScheduler().scheduleSyncDelayedTask(RiseMeetup.getInstance(), () -> {
            if (dead) return;
            logger.setHealth(0);
        }, (20 * 60));
    }

    private void createLogger() {
        logger = (Villager) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.VILLAGER);

        logger.setProfession(Villager.Profession.PRIEST);

        logger.getEquipment().setHelmetDropChance(100);
        logger.getEquipment().setChestplateDropChance(100);
        logger.getEquipment().setLeggingsDropChance(100);
        logger.getEquipment().setBootsDropChance(100);

        logger.getEquipment().setArmorContents(armorContents);

        logger.setRemoveWhenFarAway(false);
        logger.setCanPickupItems(false);

        logger.setMaxHealth(health);
        logger.setHealth(health);

        logger.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10 * (20*60), 255));

        logger.setCustomName(playerName);
        logger.setCustomNameVisible(true);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Zombie)) return;

        if (e.getEntity().getUniqueId() == logger.getUniqueId()) {
            dead = true;

            e.getDrops().addAll(Arrays.asList(contents));
            e.getDrops().addAll(Arrays.asList(armorContents));

            if (e.getEntity().getKiller() != null) {
                lowerPlayerCount();
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c" + e.getEntity().getCustomName() + "&6(&fCombat Logger&6) &cwas slain by " + e.getEntity().getKiller().getDisplayName() + "."));

                MeetupPlayer dead = MeetupPlayer.getMeetupPlayers().get(loggedPlayer.getUniqueId());
                dead.getSaveManager().setLifeDeaths(dead.getSaveManager().getLifeDeaths() + 1);
                dead.getSaveManager().saveInfo();

                MeetupPlayer player = MeetupPlayer.getMeetupPlayers().get(e.getEntity().getKiller().getUniqueId());
                if (player.getPlayerObject() == null) return;
                player.getSaveManager().setSessionKills(player.getSaveManager().getSessionKills() + 1);
                return;
            }

            lowerPlayerCount();
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c" + e.getEntity().getCustomName() + "&6(&fCombat Logger&6) &chas expired. (10 Minutes)"));
            HandlerList.unregisterAll(this);
        }
    }

    @EventHandler
    public void onEntityIgnite(EntityCombustEvent e) {
        if (!(e.getEntity() instanceof Villager)) return;
        if (e.getEntity().getUniqueId() == logger.getUniqueId()) e.setCancelled(true);
    }

    private void lowerPlayerCount() {
        MeetupManager.getInstance().getGame().getGamePlayers().remove(MeetupPlayer.getMeetupPlayers().get(loggedPlayer.getUniqueId()));
    }
}
