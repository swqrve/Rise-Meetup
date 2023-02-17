package me.swerve.meetup.runnable;

import lombok.Getter;
import lombok.Setter;
import me.swerve.meetup.game.MeetupGame;
import me.swerve.meetup.manager.MeetupManager;
import me.swerve.meetup.player.MeetupPlayer;
import me.swerve.meetup.runnable.action.TimedAction;
import me.swerve.meetup.util.BorderUtil;
import me.swerve.meetup.util.ItemCreator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameRunnable extends BukkitRunnable {
    @Getter private final List<TimedAction> timedActions = new ArrayList<>();
    @Getter @Setter private int taskId;

    public GameRunnable() {
        MeetupGame game = MeetupManager.getInstance().getGame();

        for (MeetupPlayer p : game.getGamePlayers()) p.setCurrentState(MeetupPlayer.PlayerState.PLAYING);

        MeetupManager.getInstance().setPvPEnabled(true);

        ShapedRecipe goldenHead = new ShapedRecipe(MeetupManager.getInstance().getGoldenHead());
        goldenHead.shape("!!!","!@!","!!!");
        goldenHead.setIngredient('!', Material.GOLD_INGOT);
        goldenHead.setIngredient('@', new ItemCreator(Material.SKULL_ITEM, 1).setData(3).getItem().getData());

        Bukkit.getServer().addRecipe(goldenHead);

        int borderTime = 60;
        TimedAction borderShrink = new TimedAction(this).addAction(borderTime - 60, "&f[&6RiseUHC&f] &f1 Minute until Border Shrinks to &7[&f" + BorderUtil.nextBorder(game.getCurrentBorder()) + "x" + BorderUtil.nextBorder(game.getCurrentBorder()) + "&7]")
                .addAction(borderTime - 30, "&f[&6RiseUHC&f] &f30 Seconds until Border Shrinks to &7[&f" + BorderUtil.nextBorder(game.getCurrentBorder()) + "x" + BorderUtil.nextBorder(game.getCurrentBorder()) + "&7]")
                .addAction(borderTime, "&f[&6RiseUHC&f] Border has shrunk to &7[&f" + BorderUtil.nextBorder(game.getCurrentBorder()) + "x" + BorderUtil.nextBorder(game.getCurrentBorder()) + "&7]", TimedAction.ActionType.SHRINK_BORDER);

        for (int i = 10; i > 0; i--) {
            if (i > 5 && i != 10) continue;
            String withS = " Seconds";
            if (i == 1) withS = " Second";

            borderShrink.addAction(borderTime - i, "&f[&6RiseUHC&f] &f" + i + withS + " until Border Shrinks to &7[&f" + BorderUtil.nextBorder(game.getCurrentBorder()) + "x" + BorderUtil.nextBorder(game.getCurrentBorder()) + "&7]");
        }
    }

    public void run() {
        if (MeetupManager.getInstance().getCurrentGameState() != MeetupManager.GameState.PLAYING) return;

        setGameTime(getGameTime() + 1);

        for (TimedAction action : new ArrayList<>(timedActions)) action.update(getGameTime());

        if (MeetupManager.getInstance().getCurrentGameState() != MeetupManager.GameState.ENDING) if (MeetupManager.getInstance().getGame().getGamePlayers().size() < 2) {
            MeetupManager.getInstance().getGame().endGame(MeetupManager.getInstance().getGame().getGamePlayers().get(0));
            cancel();
        }
    }

    private void setGameTime(int i) {
        MeetupManager.getInstance().getGame().setGameTime(i);
    }

    private int getGameTime() {
        return MeetupManager.getInstance().getGame().getGameTime();
    }
}
