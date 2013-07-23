package net.new_liberty.commandtimer;

import net.new_liberty.commandtimer.models.CommandSet;
import net.new_liberty.commandtimer.models.CommandSetGroup;
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
        return plugin.getTimers().isWarmingUp(name);
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
        return plugin.getGroup(Bukkit.getPlayerExact(name));
    }
}
