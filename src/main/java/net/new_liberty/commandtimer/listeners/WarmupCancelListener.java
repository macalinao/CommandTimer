package net.new_liberty.commandtimer.listeners;

import net.new_liberty.commandtimer.CTPlayer;
import net.new_liberty.commandtimer.CommandTimer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Checks for canceled warmups.
 */
public class WarmupCancelListener implements Listener {
    private final CommandTimer plugin;

    /**
     * C'tor
     *
     * @param plugin
     */
    public WarmupCancelListener(CommandTimer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            cancelWarmup((Player) e.getEntity(), "damage");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        cancelWarmup(e.getPlayer(), "interact");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!e.getFrom().getBlock().equals(e.getTo().getBlock())) {
            cancelWarmup(e.getPlayer(), "move");
        }
    }

    /**
     * Cancels the warmup if it should be canceled.
     *
     * @param player
     * @param type
     * @return
     */
    private boolean cancelWarmup(Player player, String type) {
        CTPlayer p = plugin.getPlayer(player.getName());
        if (p.isWarmingUp()) {
            player.sendMessage(p.getWarmup().getSet().getMessage("warmup-no-" + type));
            p.cancelWarmup();
            return false;
        }
        return true;
    }
}
