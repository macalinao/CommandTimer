package net.new_liberty.commandtimer;

import net.new_liberty.commandtimer.set.CommandSet;
import net.new_liberty.commandtimer.set.CommandSetGroup;
import net.new_liberty.commandtimer.timer.CommandExecution;
import org.bukkit.Bukkit;

/**
 * Represents a player in CommandTimer.
 */
public class CTPlayer {
    /**
     * The plugin
     */
    private final CommandTimer plugin;

    /**
     * The case-sensitive name of this player.
     */
    private final String name;

    /**
     * C'tor
     *
     * @param plugin
     * @param name
     */
    public CTPlayer(CommandTimer plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    /**
     * Checks if this player is currently warming up.
     *
     * @return
     */
    public boolean isWarmingUp() {
        return getWarmup() != null;
    }

    /**
     * Gets the CommandExecution (warmup) associated with a player.
     *
     * @return
     */
    public CommandExecution getWarmup() {
        return plugin.getTimers().getWarmup(name);
    }

    /**
     * Cancels this player's warmup.
     */
    public void cancelWarmup() {
        plugin.getTimers().cancelWarmup(name);
    }

    /**
     * Gets the time left for this player's cooldown to expire. Returns -1 if it
     * is already expired.
     *
     * @param set The set to check.
     * @return
     */
    public int getCooldownTime(CommandSet set) {
        return plugin.getTimers().getCooldownTime(name, set);
    }

    /**
     * Gets this player's CommandSetGroup. Note: The player must be online for
     * this to work!
     *
     * @return
     */
    public CommandSetGroup getGroup() {
        return plugin.getCommandSets().getGroup(Bukkit.getPlayerExact(name));
    }

    /**
     * Starts a warmup for this player.
     *
     * @param cmd
     * @param set
     */
    public void startWarmup(String cmd, CommandSet set) {
        plugin.getTimers().startWarmup(name, cmd, set, getGroup());
    }
}
