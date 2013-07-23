package net.new_liberty.commandtimer;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * Task that executes all expired warmup timers.
 */
public class WarmupExecutor extends BukkitRunnable {
    private final TimerManager manager;

    public WarmupExecutor(TimerManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        for (CommandExecution warmup : manager.getWarmups()) {
            if (warmup.isWarmupExpired()) {
                warmup.execute();
            }
        }
    }
}
