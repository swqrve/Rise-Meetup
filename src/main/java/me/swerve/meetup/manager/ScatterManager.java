package me.swerve.meetup.manager;

import lombok.Getter;
import me.swerve.meetup.RiseMeetup;
import me.swerve.meetup.util.ScatterLocation;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScatterManager {
    private final List<ScatterLocation> possibleScatterLocations = new ArrayList<>();
    @Getter private final List<Integer> usedScatterLocs = new ArrayList<>();
    private final Random random = new Random();
    private final int totalScatterLocations;

    public ScatterManager() {
        int id = 0;
        for (int i = -10; i < 10; i++) for (int j = -10; j < 10; j++) {
            id++;
            int x = i * 10; 
            int z = j * 10;
            ScatterLocation loc = isSafeLocation(RiseMeetup.getInstance().getMeetupWorld(), x, z, id);
            if (loc.getId() != -1) possibleScatterLocations.add(loc);
        }

        totalScatterLocations = id;
    }

    public ScatterLocation getAvailableScatterLoc() {
        int scatterLocIndex = random.nextInt(totalScatterLocations) + 1;

        if (scatterLocIndex > possibleScatterLocations.size() || usedScatterLocs.contains(scatterLocIndex)) return getAvailableScatterLoc();
        usedScatterLocs.add(scatterLocIndex);

        for (ScatterLocation loc : possibleScatterLocations) if (loc.getId() == scatterLocIndex) return loc;
        return null;
    }

    public ScatterLocation isSafeLocation(World world, int x, int z, int id) {
        for (int i = 5; i > 0; i--) if (world.getBlockAt(x, getHighestBlockY(x, z, world) - i, z).getType() == Material.LAVA || world.getBlockAt(x, getHighestBlockY(x, z, world) - i, z).getType() == Material.LAVA)
            return new ScatterLocation(x, 80, z, -1);

        return new ScatterLocation(x, world.getHighestBlockYAt(x, z) + 2, z, id);
    }

    private int getHighestBlockY(int x, int z, World world) {
        for (int i = 155; i > 0; i--) {
            if (world.getBlockAt(x, i, z).getType() == Material.AIR) continue;
            return i;
        }

        return world.getHighestBlockYAt(x, z);
    }
}
