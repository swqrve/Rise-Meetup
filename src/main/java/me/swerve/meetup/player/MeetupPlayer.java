package me.swerve.meetup.player;

import lombok.Getter;
import lombok.Setter;
import me.swerve.meetup.RiseMeetup;
import me.swerve.meetup.file.FileManager;
import me.swerve.meetup.manager.MeetupManager;
import me.swerve.meetup.manager.SaveManager;
import me.swerve.meetup.player.logger.CombatLogger;
import me.swerve.meetup.util.ScatterLocation;
import me.swerve.meetup.util.ItemCreator;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.*;

@Getter
public class MeetupPlayer {
    @Getter private static final Map<UUID, MeetupPlayer> meetupPlayers = new HashMap<>();

    public enum PlayerState { LOBBY, PLAYING, SPECTATING }

    private final UUID uuid;
    @Setter private PlayerState currentState;
    @Setter private ScatterLocation currentScatterLoc;

    private final SaveManager saveManager;

    // DND Info
/*    private MeetupPlayer assignedPlayer = null;
    private Date cooldown = null;*/

    public MeetupPlayer(Player p) {
        this.uuid = p.getUniqueId();

        meetupPlayers.put(uuid, this);
        establishState();

        Document statsDocument = FileManager.getPlayerDocument(uuid);
        saveManager = new SaveManager(statsDocument, uuid);
    }

    private void establishState() {
        resetPlayerInfo();

        if (CombatLogger.getLoggers().get(uuid) != null) {
            useCombatLogger();
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) player.showPlayer(getPlayerObject());

        if (MeetupManager.getInstance().getCurrentGameState() == MeetupManager.GameState.WAITING) {
            currentState = PlayerState.LOBBY;

            teleportWithSound(new Location(Bukkit.getWorld("Lobby"), 12, 54, -0.5, 90, -1.3f), "LEVEL_UP");
            MeetupManager.getInstance().getLobbyInventory().keySet().forEach(keyInt -> {
                getPlayerObject().getInventory().setItem(keyInt, MeetupManager.getInstance().getLobbyInventory().get(keyInt));
                getPlayerObject().getInventory().setItem(7, new ItemCreator(Material.SKULL_ITEM, 1).setOwner(getPlayerObject().getName()).setName("&6Stats &7(Right Click)").addLore(Collections.singletonList("&7Right Click to view your Stats!")).getItem());
            });


            return;
        }

        if (MeetupManager.getInstance().getCurrentGameState() == MeetupManager.GameState.STARTING) {
            currentState = PlayerState.PLAYING;

            return;
        }

        currentState = PlayerState.SPECTATING;

        teleportWithSound(new Location(RiseMeetup.getInstance().getMeetupWorld(), 0, 100, 0), "LEVEL_UP");
        MeetupManager.getInstance().getLobbyInventory().keySet().forEach(keyInt -> { getPlayerObject().getInventory().setItem(keyInt, MeetupManager.getInstance().getSpectatorInventory().get(keyInt)); });

        hideSpectators();
    }

    private void resetPlayerInfo() {
        getPlayerObject().setGameMode(GameMode.SURVIVAL);
        getPlayerObject().getInventory().clear();
        getPlayerObject().getInventory().setArmorContents(null);

        getPlayerObject().setHealth(20D);
        getPlayerObject().setFoodLevel(20);
        getPlayerObject().setExp(0);
        getPlayerObject().setLevel(0);
        getPlayerObject().setFireTicks(0);

        EntityPlayer player = ((CraftPlayer) getPlayerObject()).getHandle();
        player.setAbsorptionHearts(0);
        getPlayerObject().getActivePotionEffects().clear();
        getPlayerObject().updateInventory();
    }

    public void useCombatLogger() {
        CombatLogger logger = CombatLogger.getLoggers().get(getPlayerObject().getUniqueId());

        if (logger.isDead()) {
            setSpectator();
            HandlerList.unregisterAll(logger);
            CombatLogger.getLoggers().remove(getPlayerObject().getUniqueId());
            return;
        }

        logger.setDead(true);

        currentState = PlayerState.PLAYING;
        resetPlayerInfo();

        getPlayerObject().setHealth(logger.getLogger().getHealth());
        getPlayerObject().getInventory().setContents(logger.getContents());
        getPlayerObject().getInventory().setArmorContents(logger.getLogger().getEquipment().getArmorContents());
        getPlayerObject().teleport(logger.getLogger().getLocation());

        logger.getLogger().remove();
        HandlerList.unregisterAll(logger);

        MeetupManager.getInstance().getGame().getGamePlayers().add(this);
        CombatLogger.getLoggers().remove(getPlayerObject().getUniqueId());
    }

    public void setSpectator() {
        resetPlayerInfo();
        meetupPlayers.values().forEach(uhcPlayer -> uhcPlayer.hideSpectator(getPlayerObject()));

        MeetupManager.getInstance().getSpectatorInventory().keySet().forEach(keyInt -> getPlayerObject().getInventory().setItem(keyInt, MeetupManager.getInstance().getSpectatorInventory().get(keyInt)));

        getPlayerObject().setGameMode(GameMode.CREATIVE);
        getPlayerObject().teleport(new Location(RiseMeetup.getInstance().getMeetupWorld(), 0, 100, 0));

        currentState = PlayerState.SPECTATING;
    }
    public void hideSpectators() {
        meetupPlayers.values().forEach(p -> {
            if (p.getCurrentState() == PlayerState.SPECTATING) hideSpectator(p.getPlayerObject());
        });
    }

    public void hideSpectator(Player player) {
        if (getPlayerObject() == null) return;
        if (player == null || !player.isOnline()) return;

        if (player.getDisplayName().equalsIgnoreCase(getPlayerObject().getDisplayName())) return;
        getPlayerObject().hidePlayer(player);
    }

    public Player getPlayerObject() { // THROWS NULL
        if (Bukkit.getPlayer(uuid) == null) return null;
        return Bukkit.getPlayer(uuid);
    }

    public void teleportWithSound(Location loc, String sound) {
        getPlayerObject().teleport(loc);
        getPlayerObject().playSound(getPlayerObject().getLocation(), Sound.valueOf(sound), 1f, 1);
    }
}
