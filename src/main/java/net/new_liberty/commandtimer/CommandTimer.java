package net.new_liberty.commandtimer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Command Timer.
 */
public class CommandTimer extends JavaPlugin {
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

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
    }

    /**
     * Loads the configuration.
     */
    private void loadConfig() {
        FileConfiguration config = getConfig();

        // Load messages
        messages = new HashMap<String, String>();
        ConfigurationSection messagesSection = config.getConfigurationSection("messages");
        if (messagesSection != null) {
            for (Entry<String, Object> msg : messagesSection.getValues(false).entrySet()) {
                messages.put(msg.getKey(), msg.getValue().toString());
            }
        }

        // Load sets
        sets = new HashMap<String, CommandSet>();
        commands = new HashMap<String, CommandSet>();
        ConfigurationSection setsSection = config.getConfigurationSection("sets");
        if (setsSection != null) {
            for (String key : setsSection.getKeys(false)) {
                // Get the set section
                ConfigurationSection setSection = setsSection.getConfigurationSection(key);
                if (setSection == null) {
                    // Skip if not a section
                    getLogger().log(Level.WARNING, "Invalid set configuration for set '" + key + "'. Skipping.");
                    continue;
                }

                // Set messages
                Map<String, String> setMessages = new HashMap<String, String>();
                ConfigurationSection setMessagesSection = setSection.getConfigurationSection("messages");
                if (setMessagesSection != null) {
                    for (Entry<String, Object> setMessage : setMessagesSection.getValues(false).entrySet()) {
                        setMessages.put(setMessage.getKey(), setMessage.getValue().toString());
                    }
                }

                // Set commands
                Set<String> setCommands = new HashSet<String>();
                List<String> setCmdConfig = setSection.getStringList("commands");

                cmd:
                for (String setCmd : setCmdConfig) {
                    // Lowercase the commands to make sure
                    setCmd = setCmd.toLowerCase();

                    // Check if the command has already been added in a different form to prevent conflicts

                    // In this command set
                    for (String cmd : setCommands) {
                        if (cmd.startsWith(setCmd) || setCmd.startsWith(cmd)) {
                            getLogger().log(Level.WARNING, "The command ''{0}'' from set ''{1}'' conflicts with the command ''{2}'' from the same set.", new Object[]{setCmd, key, cmd});
                            continue cmd;
                        }
                    }

                    // In previous command sets
                    for (String cmd : commands.keySet()) {
                        if (cmd.startsWith(setCmd) || setCmd.startsWith(cmd)) {
                            getLogger().log(Level.WARNING, "The command ''{0}'' from set ''{1}'' conflicts with the command ''{2}'' from set ''{3}''.", new Object[]{setCmd, key, cmd, commands.get(cmd).getId()});
                            continue cmd;
                        }
                    }

                    setCommands.add(setCmd);
                }

                // Add the set to memory
                CommandSet set = new CommandSet(this, key, setMessages, setCommands);
                sets.put(key, set);

                // Add the commands to our mapping
                for (String cmd : set.getCommands()) {
                    commands.put(cmd, set);
                }
            }
        }
    }

    /**
     * Gets the default message.
     *
     * @param key
     * @return
     */
    public String getMessage(String key) {
        return messages.get(key);
    }
}
