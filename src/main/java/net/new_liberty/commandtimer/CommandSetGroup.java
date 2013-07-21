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

    private final CommandSetGroup inherit;

    public CommandSetGroup(CommandTimer plugin, String id, Map<CommandSet, Integer> warmups, Map<CommandSet, Integer> cooldowns, CommandSetGroup inherit) {
        this.plugin = plugin;
        this.id = id;
        this.warmups = warmups;
        this.cooldowns = cooldowns;
        this.inherit = inherit;
    }
}
