package net.new_liberty.commandtimer;

import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import net.new_liberty.commandtimer.models.CommandSet;
import net.new_liberty.commandtimer.models.CommandSetGroup;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Tests configuration loading.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CommandTimer.class})
public class ConfigLoaderTest {
    private Configuration config;

    @Before
    public void setup() {
        PowerMockito.mockStatic(CommandTimer.class);

        URL url = getClass().getResource("/config.yml");
        config = YamlConfiguration.loadConfiguration(new File(url.getFile()));
    }

    @Test
    public void testLoadSet() {
        ConfigurationSection section = config.getConfigurationSection("sets.teleport");
        CommandSet set = ConfigLoader.loadSet("teleport", section, new HashMap<String, CommandSet>());

        assertEquals("teleport", set.getId());

        assertEquals("&6Teleporting in %time% seconds. Don't move.", set.getMessage("warmup"));

        assertTrue(set.getCommands().contains("home"));
        assertFalse(set.getCommands().contains("homeboy"));
    }

    @Test
    public void testLoadSetGroup() {
        CommandSet horses = PowerMockito.mock(CommandSet.class);
        CommandSet teleport = PowerMockito.mock(CommandSet.class);

        Map<String, CommandSet> sets = ImmutableMap.<String, CommandSet>builder().put("horses", horses).put("teleport", teleport).build();

        ConfigurationSection section = config.getConfigurationSection("groups.default");
        CommandSetGroup group = ConfigLoader.loadSetGroup("default", section, sets);

        assertEquals(5, group.getWarmup(horses));
        assertEquals(15, group.getCooldown(teleport));
    }
}
