package net.new_liberty.commandtimer.set;

import java.util.Map;
import org.bukkit.entity.Player;

/**
 * Manages CommandSets.
 */
public class CommandSetManager {

    /**
     * Stores sets.
     */
    private Map<String, CommandSet> sets;

    /**
     * Stores commands mapped to their corresponding sets.
     */
    private Map<String, CommandSet> commands;

    /**
     * Stores the command groups.
     */
    private Map<String, CommandSetGroup> groups;

    public CommandSetManager(Map<String, CommandSet> sets, Map<String, CommandSet> commands, Map<String, CommandSetGroup> groups) {
        this.sets = sets;
        this.commands = commands;
        this.groups = groups;
    }

    /**
     * Gets the CommandSetGroup of a given player.
     *
     * @param p
     * @return
     */
    public CommandSetGroup getGroup(Player p) {
        if (p == null) {
            return null; // Player is offline, silently fail
        }

        for (CommandSetGroup g : groups.values()) {
            if (p.hasPermission(g.getPermission())) {
                return g;
            }
        }
        return groups.get("default");
    }

    /**
     * Gets a CommandSet from the corresponding command.
     *
     * @param command
     * @return
     */
    public CommandSet getSet(String command) {
        for (Map.Entry<String, CommandSet> e : commands.entrySet()) {
            if (command.startsWith(e.getKey() + " ")) {
                return e.getValue();
            }
        }
        return null;
    }
}
