package me.swerve.meetup.manager;

import lombok.Getter;
import lombok.Setter;
import me.swerve.meetup.RiseMeetup;
import me.swerve.meetup.game.MeetupGame;
import me.swerve.meetup.player.MeetupPlayer;
import me.swerve.meetup.util.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import redis.clients.jedis.Jedis;

import java.util.*;

@Getter
public class MeetupManager {
    @Getter private static MeetupManager instance;
    public enum GameState { WAITING, STARTING, PLAYING, ENDING, WHITELISTED }

    private GameState currentGameState;
    @Setter private MeetupGame game;
    @Setter private ScatterManager scatterManager = null;

    private final Map<Integer, ItemStack> lobbyInventory = new HashMap<>();
    private final Map<Integer, ItemStack> spectatorInventory = new HashMap<>();

    private final int requiredPlayerCount = 2;

    @Setter private boolean PvPEnabled = false;

    private final ItemStack goldenHead = new ItemCreator(Material.GOLDEN_APPLE, 1).setName("&6Golden Head").getItem();

    public MeetupManager() {
        instance = this;

        setCurrentGameState(GameState.WHITELISTED);

        // Hub inventory
        lobbyInventory.put(8, new ItemCreator(Material.NAME_TAG, 1).setName("&6Leaderboards &7(Right Click)").addLore(Collections.singletonList("&7Right Click to view the Leaderboards!")).getItem());

        // Spectator Inventory (Non Staff)
        spectatorInventory.put(0, new ItemCreator(Material.COMPASS, 1).setName("&6Navigator &7(Right Click)").addLore(Collections.singletonList("&7Right Click to View all Players within 100x100")).getItem());
    }

    public void setCurrentGameState(GameState state) {
        currentGameState = state;

        try (Jedis jedis = RiseMeetup.getInstance().getPool().getResource()) {
            if (jedis.get("meetup-1") != null) jedis.del("meetup-1");
            jedis.set("meetup-1", state.toString());
        } catch (Exception ignored) {
            Bukkit.getConsoleSender().sendMessage("Failed to update Game State");
        }
    }
    public void startMeetup() { // This is starting meetup as in getting them out of the lobby, not starting the actual game yet (This starts the "Starting" Stage
        scatterManager = new ScatterManager();

        List<MeetupPlayer> gamePlayers = new ArrayList<>(MeetupPlayer.getMeetupPlayers().values());
        game = new MeetupGame(gamePlayers);
    }
}
