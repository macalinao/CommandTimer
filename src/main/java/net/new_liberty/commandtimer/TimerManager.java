package net.new_liberty.commandtimer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manages player timers.
 */
public class TimerManager {
    private final CommandTimer plugin;

    /**
     * Task to check all warmup timers.
     */
    private final WarmupCheckTask warmupTask;

    private Map<String, WarmupTimer> warmups;

    public TimerManager(CommandTimer plugin) {
        this.plugin = plugin;

        warmupTask = new WarmupCheckTask(this);
    }

    /**
     * Starts all tasks.
     */
    public void startTasks() {
        warmupTask.runTaskTimer(plugin, 2L, 2L);
    }

    /**
     * Clears all timers.
     */
    public void clear() {
        warmups.clear();
    }

    /**
     * Returns true if this player is warming up.
     *
     * @param player The name of the player, case sensitive.
     * @return
     */
    public boolean isWarmingUp(String player) {
        return warmups.containsKey(player);
    }

    /**
     * Gets a list of all warmup timers.
     *
     * @return
     */
    public List<WarmupTimer> getWarmups() {
        return new ArrayList<WarmupTimer>(warmups.values());
    }

    /**
     * Starts a warmup for a player.
     *
     * @param player
     * @param command
     * @param duration
     */
    public void startWarmup(String player, String command, int duration) {
        warmups.put(player, new WarmupTimer(player, command, duration));
    }
}
