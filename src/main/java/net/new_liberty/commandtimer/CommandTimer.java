package net.new_liberty.commandtimer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Command Timer.
 */
public class CommandTimer extends JavaPlugin {
    private Map<String, String> messages = new HashMap<String, String>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        ConfigurationSection messagesSection = config.getConfigurationSection("messages");
        if (messagesSection != null) {
            for (Entry<String, Object> msg : messagesSection.getValues(false).entrySet()) {
                messages.put(msg.getKey(), msg.getValue().toString());
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
