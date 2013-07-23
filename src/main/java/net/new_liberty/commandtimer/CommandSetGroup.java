package net.new_liberty.commandtimer;

import java.util.Map;

/**
 * Represents a group of CommandSets.
 */
public final class CommandSetGroup {
    private final CommandTimer plugin;

    private final String id;

    private final Map<CommandSet, Integer> warmups;

    private final Map<CommandSet, Integer> cooldowns;

    public CommandSetGroup(CommandTimer plugin, String id, Map<CommandSet, Integer> warmups, Map<CommandSet, Integer> cooldowns) {
        this.plugin = plugin;
        this.id = id;
        this.warmups = warmups;
        this.cooldowns = cooldowns;
    }

    /**
     * Gets the warmup of the given CommandSet.
     *
     * @param set
     * @return
     */
    public int getWarmup(CommandSet set) {
        Integer r = warmups.get(set);
        return r == null ? 0 : r;
    }

    /**
     * Gets the cooldown of the given CommandSet.
     *
     * @param set
     * @return
     */
    public int getCooldown(CommandSet set) {
        Integer r = cooldowns.get(set);
        return r == null ? 0 : r;
    }
}
