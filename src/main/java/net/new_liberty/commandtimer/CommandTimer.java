package net.new_liberty.commandtimer;

import net.new_liberty.commandtimer.set.CommandSet;
import net.new_liberty.commandtimer.set.CommandSetGroup;
import net.new_liberty.commandtimer.timer.TimerManager;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Command Timer.
 */
public class CommandTimer extends JavaPlugin {
    private static CommandTimer instance;

    private static final Map<String, String> DEFAULT_MESSAGES;

    static {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String>builder();

        builder.put("warmup", "&6Command will run in &c%time% seconds. Don't move.");
        builder.put("warmup-cancelled", "&cPending command request cancelled.");
        builder.put("warmup-in-progress", "&cThis command is warming up. Don't move.");
        builder.put("warmup-no-interact", "&cError: &6You can't do this while the command is warming up!");
        builder.put("cooldown", "&cError: &6You must wait &c%time% seconds to use this command again.");

        DEFAULT_MESSAGES = builder.build();
    }
    /**
     * Manages our timers (warmups and cooldowns)
     */
    private TimerManager timers;

    /**
     * Stores the global messages.
     */
    private Map<String, String> messages;

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

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        loadConfig();

        Bukkit.getPluginManager().registerEvents(new CTListener(this), this);

        timers = new TimerManager(this);
        timers.startTasks();
    }

    /**
     * Loads the configuration.
     */
    private void loadConfig() {
        Map[] c = ConfigLoader.loadConfig(getConfig());
        messages = c[0];
        sets = c[1];
        commands = c[2];
        groups = c[3];
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
    public CommandSet getCommandSet(String command) {
        for (Entry<String, CommandSet> e : commands.entrySet()) {
            if (command.startsWith(e.getKey() + " ")) {
                return e.getValue();
            }
        }
        return null;
    }

    /**
     * Gets a CTPlayer.
     *
     * @param name
     * @return
     */
    public CTPlayer getPlayer(String name) {
        return new CTPlayer(this, name);
    }

    /**
     * Gets the TimerManager instance.
     *
     * @return
     */
    public TimerManager getTimers() {
        return timers;
    }

    /**
     * Gets the default message.
     *
     * @param key
     * @return
     */
    public String getMessage(String key) {
        String msg = messages.get(key);
        if (msg == null) {
            msg = DEFAULT_MESSAGES.get(key);
        }
        return msg;
    }

    /**
     * Gets this CommandTimer instance.
     *
     * @return
     */
    public static CommandTimer getInstance() {
        return instance;
    }

    /**
     * Logs a message.
     *
     * @param level
     * @param msg
     */
    public static void log(Level level, String msg) {
        instance.getLogger().log(level, msg);
    }
}
