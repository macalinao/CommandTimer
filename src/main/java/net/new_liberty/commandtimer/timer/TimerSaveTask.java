package net.new_liberty.commandtimer.timer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.new_liberty.commandtimer.CommandTimer;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Saves the timers.
 */
public class TimerSaveTask extends BukkitRunnable {
    private final TimerManager m;

    public TimerSaveTask(TimerManager m) {
        this.m = m;
    }

    @Override
    public void run() {
        CommandTimer.log(Level.INFO, "Saving timers...");
        try {
            m.save();
        } catch (IOException ex) {
            Logger.getLogger(TimerSaveTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
