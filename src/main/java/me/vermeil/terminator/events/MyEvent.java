package me.vermeil.terminator.events;

import me.vermeil.terminator.Terminator;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public class MyEvent implements Listener {
    /**
     * A set of click actions that trigger the arrow shooting.
     */
    private static final Set<Action> CLICK_ACTIONS = EnumSet.of(
            Action.RIGHT_CLICK_AIR,
            Action.RIGHT_CLICK_BLOCK,
            Action.LEFT_CLICK_AIR,
            Action.LEFT_CLICK_BLOCK
    );
    Terminator plugin;

    public MyEvent(Terminator plugin) {
        this.plugin = plugin;
    }

    /**
     * Shoots an arrow in the given direction from the given player.
     * with a fixed velocity and damage
     *
     * @param player    the player shooting the arrow
     * @param direction the direction of the arrow
     */
    private void shootArrow(Player player, Vector direction) {
        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setCritical(true);
        arrow.setVelocity(direction.multiply(4));
        arrow.setDamage(371);
    }

    /**
     * Handles player interactions (e.g. right-clicking with an item).
     *
     * @param event the player interact event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (!Objects.requireNonNull(meta).hasDisplayName() || !meta.getDisplayName().equals(ChatColor.LIGHT_PURPLE + "Hasty Terminator " + ChatColor.GOLD + "✪✪✪✪" + ChatColor.RED + "➎")) {
            return;
        }

        if (!CLICK_ACTIONS.contains(event.getAction())) {
            return;
        }

        event.setCancelled(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                Vector forward = player.getLocation().getDirection();
                Vector right = forward.clone().rotateAroundY(Math.toRadians(15));
                Vector left = forward.clone().rotateAroundY(Math.toRadians(-15));

                shootArrow(player, forward);
                shootArrow(player, right);
                shootArrow(player, left);
            }
        }.runTask(plugin);
    }

    /**
     * Handles projectile hits (e.g. arrows hitting entities).
     * <p></p>
     * Credit to <a href="https://www.spigotmc.org/threads
     * /forcing-enderman-to-take-damage-from-projectiles.511665
     * /#post-4336602">[SpigotMC forum post]</a>
     *
     * @param event the projectile hit event
     */
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        // Credit to (https://www.spigotmc.org/threads/forcing-enderman-to-take-damage-from-projectiles.511665/#post-4336602)

        if (!(event.getEntity() instanceof Arrow)) {
            return;
        }

        Arrow arrow = (Arrow) event.getEntity();
        Player shooter = null;
        if (arrow.getShooter() instanceof Player) {
            shooter = (Player) arrow.getShooter();
        }

        if (shooter == null) {
            return;
        }

        for (Entity entity : arrow.getNearbyEntities(3, 3, 3)) {
            if (entity instanceof Enderman) {
                Enderman enderman = (Enderman) entity;
                enderman.damage(371, shooter);
                arrow.remove();
            }
        }
    }

    /**
     * <h1>Did not test it yet</h1>
     * <p>this method ill check when an entity damages another entity
     * as you can see arrow is an entity</p>
     * <p>by the way i canceled the event, i think this ill stop enderman from teleporting
     * </p>
     * <p></p>
     * <p>create a artifact and test to see if it works</p>
     * @param event
     */
    @EventHandler
    public void endermanHitEvent(EntityDamageByEntityEvent event) {
        Entity arrow = event.getDamager();
        Entity enderman = event.getEntity();
        Entity entity = event.getDamageSource().getCausingEntity(); // this returs the entity tha caused the damage
        if (arrow instanceof Arrow && enderman instanceof Enderman) {
            event.setCancelled(true);
            ((Enderman) enderman).damage(371,entity);
        }
    }
}
