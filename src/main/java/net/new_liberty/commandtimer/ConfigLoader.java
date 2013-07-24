package net.new_liberty.commandtimer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import static net.new_liberty.commandtimer.CommandTimer.log;
import net.new_liberty.commandtimer.models.CommandSet;
import org.bukkit.configuration.ConfigurationSection;
import net.new_liberty.commandtimer.models.CommandSetGroup;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.permissions.Permission;

/**
 * Loads the configuration.
 */
public final class ConfigLoader {
    /**
     * C'tor
     */
    private ConfigLoader() {
    }

    public static Map[] loadConfig(Configuration config) {
        Map msgs = loadMessages(config);
        Map[] sets = loadSets(config);
        Map groups = loadSetGroups(config, sets[0]);
        return new Map[]{msgs, sets[0], sets[1], groups};
    }

    /**
     * Loads messages from the config.
     *
     * @param config
     * @return
     */
    public static Map<String, String> loadMessages(Configuration config) {
        Map<String, String> messages = new HashMap<String, String>();
        ConfigurationSection messagesSection = config.getConfigurationSection("messages");
        if (messagesSection != null) {
            for (Map.Entry<String, Object> msg : messagesSection.getValues(false).entrySet()) {
                messages.put(msg.getKey(), msg.getValue().toString());
            }
        }
        return messages;
    }

    /**
     * Loads a CommandSet.
     *
     * @param id
     * @param section
     * @param commandMappings
     * @return
     */
    public static CommandSet loadSet(String id, ConfigurationSection section, Map<String, CommandSet> commandMappings) {
        if (section == null) {
            return null;
        }

        // Set messages
        Map<String, String> messages = new HashMap<String, String>();
        ConfigurationSection setMessagesSection = section.getConfigurationSection("messages");
        if (setMessagesSection != null) {
            for (Map.Entry<String, Object> setMessage : setMessagesSection.getValues(false).entrySet()) {
                messages.put(setMessage.getKey(), setMessage.getValue().toString());
            }
        }

        // Set commands
        Set<String> commands = new HashSet<String>();
        List<String> setCmdConfig = section.getStringList("commands");

        cmd:
        for (String setCmd : setCmdConfig) {
            // Lowercase the commands to make sure
            setCmd = setCmd.toLowerCase();

            // Check if the command has already been added in a different form to prevent conflicts

            // In this command set
            for (String cmd : commands) {
                if (cmd.startsWith(setCmd) || setCmd.startsWith(cmd)) {
                    log(Level.WARNING, "The command '" + setCmd + "' from set '" + id + "' conflicts with the command '" + cmd + "' from the same set.");
                    continue cmd;
                }
            }

            // In previous command sets
            for (String cmd : commandMappings.keySet()) {
                if (cmd.startsWith(setCmd) || setCmd.startsWith(cmd)) {
                    log(Level.WARNING, "The command '" + setCmd + "' from set '" + id + "' conflicts with the command '" + cmd + "' from set '" + commandMappings.get(cmd).getId() + "'.");
                    continue cmd;
                }
            }
            commands.add(setCmd);
        }

        return new CommandSet(id, messages, commands);
    }

    /**
     * Loads the CommandSets.
     *
     * @param config
     * @return
     */
    public static Map<String, CommandSet>[] loadSets(Configuration config) {
        Map<String, CommandSet> sets = new HashMap<String, CommandSet>();
        Map<String, CommandSet> commands = new HashMap<String, CommandSet>();
        ConfigurationSection setsSection = config.getConfigurationSection("sets");
        if (setsSection != null) {
            for (String key : setsSection.getKeys(false)) {
                // Get the set section
                ConfigurationSection setSection = setsSection.getConfigurationSection(key);
                CommandSet set = loadSet(key, setSection, commands);
                if (set == null) {
                    log(Level.WARNING, "Invalid set configuration for set '" + key + "'. Skipping.");
                    continue;
                }
                sets.put(key, set);

                // Add the commands to our mapping
                for (String cmd : set.getCommands()) {
                    commands.put(cmd, set);
                }
            }
        }

        return new Map[]{sets, commands};
    }

    /**
     * Loads the ComandSetGroup
     *
     * @param key
     * @param section
     * @param sets A map containing all the sets
     * @return
     */
    public static CommandSetGroup loadSetGroup(String key, ConfigurationSection section, Map<String, CommandSet> sets) {
        if (section == null) {
            return null;
        }

        Map<CommandSet, Integer> warmups = new HashMap<CommandSet, Integer>();
        Map<CommandSet, Integer> cooldowns = new HashMap<CommandSet, Integer>();

        // Get the group's command set configurations
        for (String set : section.getKeys(false)) {
            // Verify if this is an actual CommandSet
            CommandSet cs = sets.get(set);
            if (cs == null) {
                // Skip if not a section
                log(Level.WARNING, "The set '" + set + "' does not exist for group '" + key + "' to use. Skipping.");
                continue;
            }

            ConfigurationSection setSection = section.getConfigurationSection(set);
            if (setSection == null) {
                // Skip if not a section
                log(Level.WARNING, "Invalid group set configuration for group '" + key + "' and set '" + set + "'. Skipping.");
                continue;
            }

            int warmup = setSection.getInt("warmup", 0);
            warmups.put(cs, warmup);

            int cooldown = setSection.getInt("cooldown", 0);
            cooldowns.put(cs, cooldown);
        }

        return new CommandSetGroup(key, warmups, cooldowns);
    }

    /**
     * Loads all CommandSetGroups.
     *
     * @param config
     * @param sets
     * @return
     */
    public static Map<String, CommandSetGroup> loadSetGroups(Configuration config, Map<String, CommandSet> sets) {
        Map<String, CommandSetGroup> groups = new HashMap<String, CommandSetGroup>();
        ConfigurationSection groupsSection = config.getConfigurationSection("groups");
        if (groupsSection != null) {
            for (String key : groupsSection.getKeys(false)) {
                // Get the group section
                ConfigurationSection groupSection = groupsSection.getConfigurationSection(key);

                CommandSetGroup group = loadSetGroup(key, groupSection, sets);
                if (group == null) {
                    log(Level.WARNING, "Invalid group configuration for group '" + key + "'. Skipping.");
                }

                // Create and add a permission
                Permission perm = new Permission(group.getPermission());
                Bukkit.getPluginManager().addPermission(perm);

                groups.put(key, group);
            }
        }
        return groups;
    }
}
