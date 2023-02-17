package me.swerve.meetup.listener;

import me.swerve.meetup.RiseMeetup;
import me.swerve.meetup.manager.MeetupManager;
import me.swerve.meetup.player.MeetupPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        Player p = (Player) e.getEntity();
        MeetupPlayer player = MeetupPlayer.getMeetupPlayers().get(p.getUniqueId());
        if (player.getCurrentState() != MeetupPlayer.PlayerState.PLAYING) e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (MeetupManager.getInstance().getCurrentGameState() != MeetupManager.GameState.PLAYING) {
            e.setCancelled(true);
            return;
        }

        if (e.getDamager() instanceof Arrow && e.getEntity() instanceof Player) {
            Arrow arrow = (Arrow) e.getDamager();

            if (arrow.getShooter() instanceof Player) {
                Player damager = (Player) arrow.getShooter();
                Player damaged = (Player) e.getEntity();

                Bukkit.getScheduler().runTaskLater(RiseMeetup.getInstance(), () -> damager.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6" + damaged.getName() + " &fis now at &6" + (int) Math.floor((damaged.getHealth() / 20) * 100)) + "%"), 1);
            }
        }

        MeetupPlayer p = MeetupPlayer.getMeetupPlayers().get(e.getDamager().getUniqueId());
        if (p.getCurrentState() == MeetupPlayer.PlayerState.SPECTATING) e.setCancelled(true);
    }
}
