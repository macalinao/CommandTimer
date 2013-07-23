package net.new_liberty.commandtimer;

import net.new_liberty.commandtimer.models.CommandSet;
import net.new_liberty.commandtimer.models.CommandSetGroup;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Listener
 */
public class CTListener implements Listener {
    /**
     * Plugin instance
     */
    private final CommandTimer plugin;

    /**
     * C'tor
     *
     * @param plugin
     */
    public CTListener(CommandTimer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        String cmd = e.getMessage();
        cmd = cmd.substring(1, cmd.length());

        CommandSet set = plugin.getCommandSet(cmd);
        if (set == null) {
            return;
        }

        CTPlayer p = plugin.getPlayer(e.getPlayer().getName());
        CommandSetGroup g = p.getGroup();
    }
}
