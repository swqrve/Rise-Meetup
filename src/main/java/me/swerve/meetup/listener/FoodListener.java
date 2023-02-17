package me.swerve.meetup.listener;

import me.swerve.meetup.manager.MeetupManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.util.Random;

public class FoodListener implements Listener {
    private Random random = new Random();

    @EventHandler
    public void onFoodLoseEvent(FoodLevelChangeEvent e) {
        if (MeetupManager.getInstance().getCurrentGameState() != MeetupManager.GameState.PLAYING) {
            e.setFoodLevel(20);
            return;
        }

        Player player = (Player) e.getEntity();
        int oldFoodLevel = player.getFoodLevel();
        int newFoodLevel = e.getFoodLevel();

        if (newFoodLevel < oldFoodLevel) if (random.nextInt(100) + 1 > 40) {
            e.setCancelled(true);
        }
    }
}
