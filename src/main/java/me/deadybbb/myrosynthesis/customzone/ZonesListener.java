package me.deadybbb.myrosynthesis.customzone;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;


public class ZonesListener implements Listener {
    private final ZonesHandler zonesHandler;

    public ZonesListener(ZonesHandler zonesHandler) {
        this.zonesHandler = zonesHandler;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        BukkitRunnable task = zonesHandler.activeTasks.remove(playerId);
        if (task != null) {
            task.cancel();
        }
    }
}
