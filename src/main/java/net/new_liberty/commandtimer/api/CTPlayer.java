package net.new_liberty.commandtimer.api;

import net.new_liberty.commandtimer.CommandTimer;
import net.new_liberty.commandtimer.models.CommandSet;

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
}
