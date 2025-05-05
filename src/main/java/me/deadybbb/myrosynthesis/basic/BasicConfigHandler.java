package me.deadybbb.myrosynthesis.basic;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class BasicConfigHandler {
    public final JavaPlugin plugin;
    public final File configFile;
    public final String configFileName;
    public FileConfiguration config;

    public BasicConfigHandler(JavaPlugin plugin, String configFileName) {
        this.plugin = plugin;
        this.configFileName = configFileName;
        this.configFile = new File(plugin.getDataFolder(), configFileName);
        reloadConfig();
    }

    public boolean reloadConfig() {
        if (!configFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                configFile.createNewFile();
                plugin.getLogger().info("Created new "+configFileName);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create "+configFileName, e);
                return false;
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        return true;
    }

    public boolean saveConfig() {
        try {
            config.save(configFile);
            plugin.getLogger().info("Successfully saved to "+configFileName);
            return true;
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save "+configFileName, e);
            return false;
        }
    }
}
