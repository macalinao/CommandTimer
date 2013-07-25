package net.new_liberty.commandtimer;

import net.new_liberty.commandtimer.set.CommandSet;
import net.new_liberty.commandtimer.set.CommandSetGroup;
import net.new_liberty.commandtimer.timer.CommandExecution;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Listener
 */
public class CTListener implements Listener {
    /**
     * Plugin instance
     */
    private final CommandTimer plugin;

    /**
     * C'tor
     *
     * @param plugin
     */
    public CTListener(CommandTimer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        // Check if we changed blocks
        if (e.getFrom().getBlock().equals(e.getTo().getBlock())) {
            return;
        }

        Player player = e.getPlayer();
        CTPlayer p = plugin.getPlayer(player.getName());
        if (p.isWarmingUp()) {
            player.sendMessage(plugin.getMessage("warmup-cancelled"));
            p.cancelWarmup();
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        CTPlayer p = plugin.getPlayer(player.getName());
        if (p.isWarmingUp()) {
            player.sendMessage(plugin.getMessage("warmup-no-interact"));
            p.cancelWarmup();
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        String cmd = e.getMessage();
        cmd = cmd.substring(1);

        CommandSet set = plugin.getCommandSets().getSet(cmd);
        if (set == null) {
            return; // Ignore if this command isn't part of a set
        }

        Player player = e.getPlayer();
        CTPlayer p = plugin.getPlayer(player.getName());
        CommandSetGroup g = p.getGroup();
        if (g == null) {
            return; // Ignore if player isn't in a group
        }

        // Check if we're already warming up. Don't let them use commands while doing so.
        CommandExecution ce = p.getWarmup();
        if (ce != null) {
            if (ce.isWarmupExpired()) {
                plugin.getTimers().finishWarmup(ce);
            } else {
                player.sendMessage(plugin.getMessage("warmup-in-progress"));
                e.setCancelled(true);
            }
            return;
        }

        // Check if we're currently on cooldown
        int cd = p.getCooldownTime(set);
        if (cd != 0) {
            // We're on cooldown
            player.sendMessage(plugin.getMessage("cooldown").replaceAll("%time%", Integer.toString(cd)));
            e.setCancelled(true);
            return;
        }

        // Check if we have to do warmups
        int wu = g.getWarmup(set);
        if (wu != 0) {
            // Do a warmup
            p.startWarmup(cmd, set);
            player.sendMessage(plugin.getMessage("warmup").replaceAll("%time%", Integer.toString(wu)));
            e.setCancelled(true);
            return;
        }

        // No warmup (or the warmup has been cancelled) so don't do anything.
    }
}
