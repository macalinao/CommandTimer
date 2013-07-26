package net.new_liberty.commandtimer.timer;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.new_liberty.commandtimer.CommandTimer;
import net.new_liberty.commandtimer.set.CommandSet;
import net.new_liberty.commandtimer.set.CommandSetGroup;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

/**
 * Contains a command that has been executed by a player.
 */
public class CommandExecution implements ConfigurationSerializable {
    private final String player;

    private final String command;

    /**
     * The set that this command is part of.
     */
    private final CommandSet set;

    /**
     * The group this player was in when executing the command.
     */
    private final CommandSetGroup group;

    /**
     * The time this command was executed at.
     */
    private final long time;

    /**
     * C'tor used for commands.
     *
     * @param player
     * @param command
     * @param set The CommandSet of this CommandExecution.
     */
    public CommandExecution(String player, String command, CommandSet set, CommandSetGroup group) {
        this(player, command, set, group, System.currentTimeMillis());
    }

    /**
     * Internal c'tor.
     *
     * @param player
     * @param command
     * @param set The CommandSet of this CommandExecution.
     * @param time The time of the execution.
     */
    private CommandExecution(String player, String command, CommandSet set, CommandSetGroup group, long time) {
        this.player = player;
        this.command = command;
        this.set = set;
        this.group = group;
        this.time = time;
    }

    /**
     * Gets the player associated with this command execution.
     *
     * @return
     */
    public String getPlayer() {
        return player;
    }

    /**
     * Gets the command associated with this command execution.
     *
     * @return
     */
    public String getCommand() {
        return command;
    }

    /**
     * Checks if this warmup timer has expired.
     *
     * @return True if this warmup has expired
     */
    public boolean isWarmupExpired() {
        return System.currentTimeMillis() >= getWarmupExpiryTime();
    }

    /**
     * Gets the time this warmup will expire.
     *
     * @return
     */
    public long getWarmupExpiryTime() {
        return time + (group.getWarmup(set) * 1000);
    }

    /**
     * Gets the time this cooldown will expire.
     *
     * @return
     */
    public boolean isCooldownExpired() {
        return System.currentTimeMillis() >= getCooldownExpiryTime();
    }

    /**
     * Gets the time in seconds remaining before this cooldown expires.
     *
     * @return
     */
    public int getCdTimeLeft() {
        long diff = getCooldownExpiryTime() - System.currentTimeMillis();
        if (diff < 0) {
            return 0;
        }

        return (int) Math.ceil(diff / 1000);
    }

    /**
     * Gets the time this cooldown will expire.
     *
     * @return
     */
    public long getCooldownExpiryTime() {
        return getWarmupExpiryTime() + (group.getCooldown(set) * 1000);
    }

    /**
     * Gets the CommandSet associated with this execution.
     *
     * @return
     */
    public CommandSet getSet() {
        return set;
    }

    /**
     * Executes the command on this CommandExecution.
     *
     * @return True if the command was dispatched
     */
    public boolean execute() {
        Player p = Bukkit.getPlayerExact(player);
        if (p == null) {
            return false;
        }
        try {
            Bukkit.dispatchCommand(p, command);
        } catch (CommandException ex) {
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public Map<String, Object> serialize() {
        return ImmutableMap.<String, Object>builder()
                .put("player", player)
                .put("command", command)
                .put("set", set.getId())
                .put("group", group.getId())
                .put("time", time).build();
    }

    /**
     * Deserializes the CommandExecution.
     *
     * @param s
     * @return
     */
    public static CommandExecution deserialize(Map<String, Object> s) {
        String player = s.get("player").toString();
        String command = s.get("command").toString();
        CommandSet set = CommandTimer.getInstance().getCommandSets().getSet(s.get("set").toString());
        CommandSetGroup group = CommandTimer.getInstance().getCommandSets().getGroup(s.get("group").toString());
        long time = ((Long) s.get("time")).longValue();

        return new CommandExecution(player, command, set, group);
    }

    /**
     * ValueOf
     *
     * @param s
     * @return
     */
    public static CommandExecution valueOf(Map<String, Object> s) {
        return deserialize(s);
    }
}
