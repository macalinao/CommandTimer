package net.new_liberty.commandtimer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Contains a command that will be executed by a player after a waiting period.
 */
public class WarmupTimer {
    private final String player;

    private final String command;

    private final long expire;

    /**
     * C'tor
     *
     * @param player
     * @param command
     * @param seconds The seconds of
     */
    public WarmupTimer(String player, String command, int seconds) {
        this.player = player;
        this.command = command;
        this.expire = System.currentTimeMillis() + (seconds * 1000);
    }

    /**
     * Checks if this warmup timer has expired.
     *
     * @return True if this warmup has expired
     */
    public boolean isExpired() {
        return System.currentTimeMillis() >= expire;
    }

    /**
     * Executes the command on this WarmupTimer.
     *
     * @return True if the command was dispatched
     */
    public boolean execute() {
        Player p = Bukkit.getPlayerExact(player);
        if (p == null) {
            return false;
        }
        Bukkit.dispatchCommand(p, command);
        return true;
    }
}
