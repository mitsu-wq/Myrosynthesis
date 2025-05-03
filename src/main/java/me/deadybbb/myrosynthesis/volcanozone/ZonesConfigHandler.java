package me.deadybbb.myrosynthesis.volcanozone;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class ZonesConfigHandler {
    private final JavaPlugin plugin;
    private final File configFile;
    private FileConfiguration config;

    public ZonesConfigHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "zones_config.yml");
        if (!configFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                configFile.createNewFile();
                plugin.getLogger().info("Created new zones_config.yml");
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create zones_config.yml", e);
            }
        }

        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public List<Zone> loadZonesFromConfig() {
        List<Zone> zones = new ArrayList<>();
        if (config.getConfigurationSection("zones") == null) return zones;

        for (String zoneName : config.getConfigurationSection("zones").getKeys(false)) {
            String path = "zones." + zoneName;
            Location min = new Location(
                    Bukkit.getWorld(Objects.requireNonNull(config.getString(path + ".world"))),
                    config.getDouble(path + ".min.x"),
                    config.getDouble(path + ".min.y"),
                    config.getDouble(path + ".min.z")
            );
            Location max = new Location(
                    Bukkit.getWorld(Objects.requireNonNull(config.getString(path + ".world"))),
                    config.getDouble(path + ".max.x"),
                    config.getDouble(path + ".max.y"),
                    config.getDouble(path + ".max.z")
            );
            boolean display = config.getBoolean(path + ".display", false);
            zones.add(new Zone(zoneName, min, max, display));
        }

        return zones;
    }

    public boolean saveZonesToConfig(List<Zone> zones) {
        try {
            config.set("zones", null);

            for (Zone zone : zones) {
                String path = "zones." + zone.name;
                config.set(path + ".world", zone.min.getWorld().getName());
                config.set(path + ".min.x", zone.min.getX());
                config.set(path + ".min.y", zone.min.getY());
                config.set(path + ".min.z", zone.min.getZ());
                config.set(path + ".max.x", zone.max.getX());
                config.set(path + ".max.y", zone.max.getY());
                config.set(path + ".max.z", zone.max.getZ());
                config.set(path + ".display", zone.displayEnabled);
            }

            config.save(configFile);
            plugin.getLogger().info("Successfully saved zones to zones_config.yml");
            return true;
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save zones_config.yml", e);
            return false;
        }
    }
}