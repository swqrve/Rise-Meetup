package me.swerve.meetup.runnable.action;


import me.swerve.meetup.game.MeetupGame;
import me.swerve.meetup.manager.MeetupManager;
import me.swerve.meetup.player.logger.CombatLogger;
import me.swerve.meetup.util.BorderUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Action {
    final String message;
    TimedAction.ActionType actionType;
    public Action(String message, TimedAction.ActionType actionType) {
        this.message = message;
        this.actionType = actionType;
    }

    public void executeAction() {
        Bukkit.broadcastMessage(message);
        if (actionType == TimedAction.ActionType.NOTHING) return;

        MeetupGame game = MeetupManager.getInstance().getGame();

        if (game.getCurrentBorder() == BorderUtil.nextBorder(game.getCurrentBorder())) return;

        int newBorderSize = BorderUtil.nextBorder(game.getCurrentBorder());
        BorderUtil.createBedrockWall(newBorderSize);
        game.setCurrentBorder(newBorderSize);

        for (Player player : Bukkit.getOnlinePlayers()) BorderUtil.updatePlayer(player);
        for (CombatLogger logger : CombatLogger.getLoggers().values()) BorderUtil.updatePlayer(logger.getLogger(), true);

        int borderTime = game.getGameTime() + 60;
        TimedAction borderShrink = new TimedAction(game.getRunnable()).addAction(borderTime - 60, "&f[&6RiseUHC&f] &f1 Minute until Border Shrinks to &7[&f" + BorderUtil.nextBorder(game.getCurrentBorder()) + "x" + BorderUtil.nextBorder(game.getCurrentBorder()) + "&7]")
                .addAction(borderTime - 30, "&f[&6RiseUHC&f] &f30 Seconds until Border Shrinks to &7[&f" + BorderUtil.nextBorder(game.getCurrentBorder()) + "x" + BorderUtil.nextBorder(game.getCurrentBorder()) + "&7]")
                .addAction(borderTime, "&f[&6RiseUHC&f] Border has shrunk to &7[&f" + BorderUtil.nextBorder(game.getCurrentBorder()) + "x" + BorderUtil.nextBorder(game.getCurrentBorder()) + "&7]");

        for (int i = 10; i > 0; i--) {
            if (i > 5 && i != 10) continue;
            String withS = " Seconds";
            if (i == 1) withS = " Second";

            borderShrink.addAction(borderTime - i, "&f[&6RiseUHC&f] &f" + i + withS + " until Border Shrinks to &7[&f" + BorderUtil.nextBorder(game.getCurrentBorder()) + "x" + BorderUtil.nextBorder(game.getCurrentBorder()) + "&7]");
        }

    }
}
