package me.swerve.meetup.runnable.action;

import me.swerve.meetup.runnable.GameRunnable;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class TimedAction {
    private final List<ValidatedTime> validatedTimes = new ArrayList<>();
    public enum ActionType { NOTHING, SHRINK_BORDER }

    public TimedAction(GameRunnable runnable) {
        runnable.getTimedActions().add(this);
    }

    public TimedAction addAction(int time, String message, ActionType action) {
        validatedTimes.add(new ValidatedTime(time, new Action(ChatColor.translateAlternateColorCodes('&', message), action)));
        return this;
    }

    public TimedAction addAction(int time, String message) {
        validatedTimes.add(new ValidatedTime(time, new Action(ChatColor.translateAlternateColorCodes('&', message), ActionType.NOTHING)));
        return this;
    }

    public void update(int gameTime) {
        validatedTimes.forEach(time -> { if (time.getTime() <= gameTime) time.getAction().executeAction(); });
        validatedTimes.removeIf(time -> time.getTime() <= gameTime);
    }
}

