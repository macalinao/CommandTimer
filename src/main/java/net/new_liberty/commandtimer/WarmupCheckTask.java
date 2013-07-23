package net.new_liberty.commandtimer;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Task that checks all timers.
 */
public class WarmupCheckTask extends BukkitRunnable {
    private final TimerManager manager;

    public WarmupCheckTask(TimerManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        for (WarmupTimer warmup : manager.getWarmups()) {
            if (warmup.isExpired()) {
                warmup.execute();
            }
        }
    }
}
