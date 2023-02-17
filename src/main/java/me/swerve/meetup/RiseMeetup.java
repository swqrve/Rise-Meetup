package me.swerve.meetup;

import assemble.Assemble;
import lombok.Getter;
import me.swerve.meetup.command.LoadoutCommand;
import me.swerve.meetup.listener.*;
import me.swerve.meetup.manager.MeetupManager;
import me.swerve.meetup.player.MeetupPlayer;
import me.swerve.meetup.scoreboard.ScoreBoardManager;
import me.swerve.meetup.util.BorderUtil;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public final class RiseMeetup extends JavaPlugin {
    @Getter private static RiseMeetup instance;
    @Getter private JedisPool pool;
    @Getter private World meetupWorld;

    @Override
    public void onEnable() {
        instance = this;

        // This fix is not mine, it is disgusting, please delete this code from existence. Thank you
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(Jedis.class.getClassLoader());
        pool = new JedisPool("127.0.0.1", 6379);
        Thread.currentThread().setContextClassLoader(previous);

        registerListeners();
        registerCommands();

        new Assemble(this, new ScoreBoardManager());
        new MeetupManager();

        meetupWorld = Bukkit.createWorld(new WorldCreator("meetup_world").environment(World.Environment.NORMAL).type(WorldType.NORMAL));
        BorderUtil.createBedrockWall(100);

        MeetupManager.getInstance().setCurrentGameState(MeetupManager.GameState.WAITING);
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&f[&6RiseUHC&f] &fMeetup is ready.."));
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&f[&6RiseUHC&f] &fMeetup has been disabled.."));

        MeetupPlayer.getMeetupPlayers().values().forEach(meetupPlayer -> meetupPlayer.getSaveManager().saveInfo());
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new FoodListener(), this);
        Bukkit.getPluginManager().registerEvents(new ConnectionListener(), this);
        Bukkit.getPluginManager().registerEvents(new ItemListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpawnListener(), this);
        Bukkit.getPluginManager().registerEvents(new WeatherListener(), this);
        Bukkit.getPluginManager().registerEvents(new HealListener(), this);
        Bukkit.getPluginManager().registerEvents(new DamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new InteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new HealListener(), this);
        Bukkit.getPluginManager().registerEvents(new PortalListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChunkListener(), this);
        Bukkit.getPluginManager().registerEvents(new DeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new MoveListener(), this);
    }

    private void registerCommands() {
        Bukkit.getPluginCommand("loadout").setExecutor(new LoadoutCommand());
    }
}