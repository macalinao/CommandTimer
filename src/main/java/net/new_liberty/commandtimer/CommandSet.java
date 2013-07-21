package net.new_liberty.commandtimer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a set of commands.
 */
public final class CommandSet {
    private final CommandTimer plugin;

    private final String id;

    private final Map<String, String> messages;

    private final Set<String> cmds;

    public CommandSet(CommandTimer plugin, String id, Map<String, String> messages, Set<String> cmds) {
        this.plugin = plugin;
        this.id = id;
        this.messages = messages;
        this.cmds = cmds;
    }

    /**
     * Gets the id of this CommandSet.
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Gets a message from its key.
     *
     * @param key
     * @return
     */
    public String getMessage(String key) {
        String ret = messages.get(key);
        if (ret == null) {
            ret = plugin.getMessage(key);
        }
        return ret;
    }

    /**
     * Gets a list of all commands in this CommandSet.
     *
     * @return
     */
    public List<String> getCommands() {
        return new ArrayList<String>(cmds);
    }
}
