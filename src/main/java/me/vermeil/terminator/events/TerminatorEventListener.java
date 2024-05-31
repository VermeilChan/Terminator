/**
 * This class implements event listeners for the Terminator.
 */
package me.vermeil.terminator.events;

import me.vermeil.terminator.Terminator;
import me.vermeil.terminator.utils.Utils;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public class TerminatorEventListener implements Listener {
    private static final Set<Action> CLICK_ACTIONS = EnumSet.of(
            Action.RIGHT_CLICK_AIR,
            Action.RIGHT_CLICK_BLOCK,
            Action.LEFT_CLICK_AIR,
            Action.LEFT_CLICK_BLOCK
    );

    private final Terminator plugin;

    /**
     * Constructor for the TerminatorEventListener class.
     *
     * @param plugin The main plugin instance.
     */
    public TerminatorEventListener(Terminator plugin) {
        this.plugin = plugin;
    }

    /**
     * Shoots arrows when the player interacts with the Terminator Bow.
     *
     * @param player     The player interacting.
     * @param direction  The direction to shoot the arrows.
     */
    private void shootArrow(Player player, Vector direction) {
        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setCritical(true);
        arrow.setVelocity(direction.multiply(4));
        arrow.setDamage(371);
    }

    /**
     * Handles player interactions with the Terminator Bow.
     *
     * @param event The player interact event.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        String displayName = Objects.requireNonNull(meta).getDisplayName();
        String targetDisplayName = Utils.color("&dHasty Terminator &6✪✪✪✪&c➎");
        if (!displayName.equals(targetDisplayName)) return;

        if (!CLICK_ACTIONS.contains(event.getAction())) return;

        event.setCancelled(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                Vector forward = player.getLocation().getDirection();
                shootArrow(player, forward);
                shootArrow(player, forward.clone().rotateAroundY(Math.toRadians(15)));
                shootArrow(player, forward.clone().rotateAroundY(Math.toRadians(-15)));
            }
        }.runTask(plugin);
    }

    /**
     * Handles projectile hits related to the Terminator.
     *
     * @param event The projectile hit event.
     */
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;

        if (!(arrow.getShooter() instanceof Player shooter)) return;

        for (Entity entity : arrow.getNearbyEntities(3, 3, 3)) {
            if (entity instanceof Enderman enderman) {
                enderman.damage(371, shooter);
                arrow.remove();
                break;
            }
        }
    }
}
