package me.deadybbb.myrosynthesis.welcomeevent;


import me.deadybbb.myrosynthesis.basic.LegacyTextHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class PlayersHandler {
    JavaPlugin plugin;
    PlayersConfigHandler configHandler;
    LegacyTextHandler textHandler;

    public PlayersHandler(JavaPlugin plugin, LegacyTextHandler textHandler) {
        this.plugin = plugin;
        this.configHandler = new PlayersConfigHandler(plugin);
        this.textHandler = textHandler;
    }

    public boolean reloadConfig() {
        try {
            configHandler.loadPlayersFromConfig();
            return true;
        } catch (Exception e){
            plugin.getLogger().log(Level.SEVERE, "Failed to load welcome books config", e);
            return false;
        }
    }

    public boolean savePlayerToConfig(Player player) {
        try{
            return configHandler.savePlayerToConfig(player.getName());
        } catch (Exception e){
            plugin.getLogger().log(Level.SEVERE, "Failed to save players config", e);
            return false;
        }
    }
}
