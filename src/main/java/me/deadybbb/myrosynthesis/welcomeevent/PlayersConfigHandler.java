package me.deadybbb.myrosynthesis.welcomeevent;

import me.deadybbb.myrosynthesis.basic.BasicConfigHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayersConfigHandler extends BasicConfigHandler {
    public PlayersConfigHandler(JavaPlugin plugin) {
        super(plugin, "players_config.yml");
    }

    public void loadPlayersFromConfig() {
        reloadConfig();
    }

    public boolean savePlayerToConfig(String playerName) {
        if (!config.saveToString().contains(playerName)) {
            config.set(playerName, "хуй");
            return saveConfig();
        }
        return false;
    }
}
