package me.swerve.meetup.game;

import lombok.Getter;
import lombok.Setter;
import me.swerve.meetup.RiseMeetup;
import me.swerve.meetup.loadout.Loadout;
import me.swerve.meetup.manager.MeetupManager;
import me.swerve.meetup.player.MeetupPlayer;
import me.swerve.meetup.runnable.GameRunnable;
import me.swerve.meetup.runnable.GameStartRunnable;
import me.swerve.meetup.util.ScatterLocation;
import me.swerve.meetup.util.SitUtil;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MeetupGame {
    @Getter private final List<MeetupPlayer> gamePlayers = new ArrayList<>();

    @Getter @Setter private int gameTime = 30; // We're starting it out thirty because we also use it for game countdown time, lazy, but it works haha
    @Getter @Setter private GameRunnable runnable;

    @Getter @Setter private int currentBorder;
    @Getter private int secondsTillRestart = 15;

    @Getter private int winnerKills;
    @Getter private String winnerName;

    public MeetupGame(List<MeetupPlayer> players) {
        MeetupManager.getInstance().setCurrentGameState(MeetupManager.GameState.STARTING);

        gamePlayers.addAll(players);
        gameTime = 30;

        currentBorder = 100;

        for (MeetupPlayer p : gamePlayers) scatterPlayer(p);

        new GameStartRunnable().runTaskTimer(RiseMeetup.getInstance(), 0, 20);
    }

    public void startGame() {
        gameTime = 0;

        runnable = new GameRunnable();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(RiseMeetup.getInstance(), runnable, 0, 20);
    }

    public void scatterPlayer(MeetupPlayer toScatter) {
        ScatterLocation scatterLoc = MeetupManager.getInstance().getScatterManager().getAvailableScatterLoc();
        Location playerLoc = toScatter.getPlayerObject().getLocation();
        toScatter.setCurrentScatterLoc(scatterLoc);

        toScatter.getPlayerObject().teleport(new Location(RiseMeetup.getInstance().getMeetupWorld(), scatterLoc.getX(), scatterLoc.getY(), scatterLoc.getZ(), playerLoc.getYaw(), playerLoc.getPitch()));
        SitUtil.sitPlayer(toScatter.getPlayerObject());

        Loadout loadout = new Loadout(toScatter);
        toScatter.getPlayerObject().getInventory().setContents(loadout.getFinalInventory());
        toScatter.getPlayerObject().getInventory().setArmorContents(loadout.getArmor());
    }
    public void endGame(MeetupPlayer winner) {
        MeetupManager.getInstance().setCurrentGameState(MeetupManager.GameState.ENDING);

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&f[&6RiseUHC&f] &fCongratulations to " + winner.getPlayerObject().getDisplayName() + " &ffor winning the Meetup with " + winner.getSaveManager().getSessionKills() + " kills!"));

        scheduleFireWorks(winner.getPlayerObject());

        new BukkitRunnable() {
            public void run() {
                secondsTillRestart--;

                if (secondsTillRestart <= 0) {
                    cancel();

                    MeetupManager.getInstance().setCurrentGameState(MeetupManager.GameState.WHITELISTED);
                    MeetupPlayer.getMeetupPlayers().values().forEach(meetupPlayer -> meetupPlayer.getSaveManager().saveInfo());

                    File fileToDelete = Bukkit.getWorld("meetup_world").getWorldFolder();
                    Bukkit.unloadWorld(Bukkit.getWorld("meetup_world"), false);

                    try { FileUtils.deleteDirectory(fileToDelete); }
                    catch(IOException ignored) {}

                    Bukkit.shutdown();
                }
            }
        }.runTaskTimer(RiseMeetup.getInstance(), 0, 20);
    }

    public void addGamePlayer(MeetupPlayer p) {
        gamePlayers.add(p);
        scatterPlayer(p);
    }

    private void scheduleFireWorks(Player p) {
        new BukkitRunnable() { public void run() { spawnFireWorks(p.getLocation().add(0, 1, 0)); }
        }.runTaskTimer(RiseMeetup.getInstance(), 0, 40);
    }

    private void spawnFireWorks(Location loc) {
        Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fireWorkMeta = firework.getFireworkMeta();

        fireWorkMeta.setPower(2);
        fireWorkMeta.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());

        firework.setFireworkMeta(fireWorkMeta);

        new BukkitRunnable() { public void run() { firework.detonate(); }
        }.runTaskLater(RiseMeetup.getInstance(), 15);
    }
}
