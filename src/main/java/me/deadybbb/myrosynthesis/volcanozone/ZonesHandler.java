package me.deadybbb.myrosynthesis.volcanozone;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ZonesHandler {
    private final ZonesConfigHandler configHandler;

    public final List<Zone> zones;
    public final Map<UUID, Location> pos1 = new HashMap<>();
    public final Map<UUID, Location> pos2 = new HashMap<>();
    public final Map<UUID, Map<String, Integer>> playerTimeInZones = new HashMap<>();

    public ZonesHandler(JavaPlugin plugin) {
        configHandler = new ZonesConfigHandler(plugin);
        zones = configHandler.loadZonesFromConfig();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    handlePlayerZones(p);
                }
                displayZones();
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void handlePlayerZones(Player p) {
        UUID uuid = p.getUniqueId();
        Map<String, Integer> timeInZones = playerTimeInZones.computeIfAbsent(uuid, k -> new HashMap<>());

        for (Zone zone : zones) {
            if (isPlayerInZone(p, zone)) {
                int timeInZone = timeInZones.getOrDefault(zone.name, 0) + 20;
                timeInZones.put(zone.name, timeInZone);
                applyEffects(p, timeInZone);
            } else {
                timeInZones.remove(zone.name);
            }
        }

        if (timeInZones.isEmpty()) {
            playerTimeInZones.remove(uuid);
        }
    }

    private boolean isPlayerInZone(Player p, Zone zone) {
        Location loc = p.getLocation();
        return loc.getWorld().equals(zone.min.getWorld()) &&
                loc.getX() >= Math.min(zone.min.getX(), zone.max.getX()) &&
                loc.getX() <= Math.max(zone.min.getX(), zone.max.getX()) &&
                loc.getY() >= Math.min(zone.min.getY(), zone.max.getY()) &&
                loc.getY() <= Math.max(zone.min.getY(), zone.max.getY()) &&
                loc.getZ() >= Math.min(zone.min.getZ(), zone.max.getZ()) &&
                loc.getZ() <= Math.max(zone.min.getZ(), zone.max.getZ());
    }

    private void applyEffects(Player p, int timeInZone) {
        int seconds = timeInZone / 20;

        p.setFireTicks(40);
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1, false, false));
        if (seconds >= 20){
            p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 1, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 40, 1, false, false));
        }
        if (seconds >= 40){
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1, false, false));
        }
    }

    private void displayZones() {
        for (Zone zone : zones) {
            if (!zone.displayEnabled) continue;

            Location min = zone.min;
            Location max = zone.max;
            double minX = Math.min(min.getX(), max.getX());
            double maxX = Math.max(min.getX(), max.getX());
            double minY = Math.min(min.getY(), max.getY());
            double maxY = Math.max(min.getY(), max.getY());
            double minZ = Math.min(min.getZ(), max.getZ());
            double maxZ = Math.max(min.getZ(), max.getZ());

            for (double x = minX; x <= maxX; x += 1.0) {
                for (double z = minZ; z <= maxZ; z += 1.0) {
                    min.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, x, minY, z, 1, 0, 0, 0, 0);
                    min.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, x, maxY, z, 1, 0, 0, 0, 0);
                }
            }
            for (double y = minY; y <= maxY; y += 1.0) {
                for (double z = minZ; z <= maxZ; z += 1.0) {
                    min.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, minX, y, z, 1, 0, 0, 0, 0);
                    min.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, maxX, y, z, 1, 0, 0, 0, 0);
                }
            }
            for (double x = minX; x <= maxX; x += 1.0) {
                for (double y = minY; y <= maxY; y += 1.0) {
                    min.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, x, y, minZ, 1, 0, 0, 0, 0);
                    min.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, x, y, maxZ, 1, 0, 0, 0, 0);
                }
            }
        }
    }

    public void saveZones() {
        configHandler.saveZonesToConfig(zones);
    }
}
