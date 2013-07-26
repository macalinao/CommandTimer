package net.new_liberty.commandtimer.timer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import net.new_liberty.commandtimer.set.CommandSet;
import net.new_liberty.commandtimer.set.CommandSetGroup;
import net.new_liberty.commandtimer.CommandTimer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Manages player timers.
 */
public class TimerManager {
    private final CommandTimer plugin;

    /**
     * Task to check all warmup timers.
     */
    private final WarmupExecutor warmupExecutor;

    /**
     * Task to save the timers.
     */
    private final TimerSaveTask timerSaveTask;

    /**
     * Stores warmups. Each player can only have one warmup running at a time.
     */
    private Map<String, CommandExecution> warmups;

    /**
     * Stores cooldowns.
     */
    private Map<String, Set<CommandExecution>> cooldowns;

    /**
     * C'tor
     *
     * @param plugin
     */
    public TimerManager(CommandTimer plugin) {
        this.plugin = plugin;

        warmupExecutor = new WarmupExecutor(this);
        timerSaveTask = new TimerSaveTask(this);
    }

    /**
     * Initializes this TimerManager.
     */
    public void initialize() {
        try {
            load();
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Error loading timer data!", ex);
        }
        startTasks();
    }

    /**
     * Loads the data.
     *
     * @throws IOException
     */
    public void load() throws IOException {
        warmups = new HashMap<String, CommandExecution>();
        cooldowns = new HashMap<String, Set<CommandExecution>>();

        File f = new File(plugin.getDataFolder(), "data.yml");
        f.createNewFile();

        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
        ConfigurationSection cds = c.getConfigurationSection("cooldowns");
        if (cds != null) {
            for (String key : cds.getKeys(false)) {
                Set<CommandExecution> set = getCooldownsInternal(key);
                for (Object o : cds.getList(key)) {
                    CommandExecution ce = (CommandExecution) o;
                    set.add(ce);
                }
            }
        }
        c.save(f);
    }

    /**
     * Saves the data.
     *
     * @throws IOException
     */
    public void save() throws IOException {
        File f = new File(plugin.getDataFolder(), "data.yml");
        f.createNewFile();

        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
        ConfigurationSection cds = c.createSection("cooldowns");
        for (Entry<String, Set<CommandExecution>> e : cooldowns.entrySet()) {
            cds.set(e.getKey(), new ArrayList<CommandExecution>(e.getValue()));
        }
    }

    /**
     * Starts the tasks in this TimerManager.
     */
    private void startTasks() {
        warmupExecutor.runTaskTimer(plugin, 2, 2);
        timerSaveTask.runTaskTimer(plugin, 20 * 60 * 5, 20 * 60 * 5); // Save every 5 minutes
    }

    /**
     * Clears all timers.
     */
    public void clear() {
        warmups.clear();
    }

    /**
     * Checks if this player is warming up.
     *
     * @param player
     * @return
     */
    public boolean isWarmingUp(String player) {
        return warmups.containsKey(player);
    }

    /**
     * Gets the warmup of this player.
     *
     * @param name
     * @return
     */
    public CommandExecution getWarmup(String name) {
        return warmups.get(name);
    }

    /**
     * Gets a list of all warmup timers.
     *
     * @return
     */
    public List<CommandExecution> getWarmups() {
        return new ArrayList<CommandExecution>(warmups.values());
    }

    /**
     * Cancels a player's warmup.
     *
     * @param command
     */
    public void cancelWarmup(String player) {
        warmups.remove(player);
    }

    /**
     * Removes the warmup.
     *
     * @param command
     */
    public void finishWarmup(CommandExecution command) {
        cancelWarmup(command.getPlayer());
        if (!command.isCooldownExpired()) {
            // There's a cooldown, we're going to add this to the player's cooldown list
            getCooldownsInternal(command.getPlayer()).add(command);
        }
    }

    /**
     * Starts a cooldown for a command.
     *
     * @param player
     * @param command
     * @param set
     * @param group
     */
    public void startCooldown(String player, String command, CommandSet set, CommandSetGroup group) {
        getCooldownsInternal(player).add(new CommandExecution(player, command, set, group));
    }

    /**
     * Starts a warmup for a player.
     *
     * @param player
     * @param command
     * @param set
     * @param group
     */
    public void startWarmup(String player, String command, CommandSet set, CommandSetGroup group) {
        warmups.put(player, new CommandExecution(player, command, set, group));
    }

    /**
     * Gets the time remaining for the player's cooldown to expire for a
     * CommandSet.
     *
     * @param player
     * @param set
     * @return
     */
    public int getCooldownTime(String player, CommandSet set) {
        // Find the CommandExecution
        CommandExecution c = null;
        Set<CommandExecution> cds = getCooldownsInternal(player);
        for (CommandExecution ce : cds) {
            if (ce.getSet().equals(set)) {
                c = ce;
                break;
            }
        }

        // If none then we aren't cooling down
        if (c == null) {
            return 0;
        }

        // If expired then we aren't cooling down
        int cd = c.getCdTimeLeft();
        if (cd == 0) {
            cds.remove(c); // Remove the now useless cooldown
        }
        return cd;
    }

    /**
     * Gets the current cooldowns a player is waiting on.
     *
     * @param player
     * @return
     */
    private Set<CommandExecution> getCooldownsInternal(String player) {
        Set<CommandExecution> cds = cooldowns.get(player);
        if (cds == null) {
            cds = new HashSet<CommandExecution>();
            cooldowns.put(player, cds);
        }
        return cds;
    }
}
