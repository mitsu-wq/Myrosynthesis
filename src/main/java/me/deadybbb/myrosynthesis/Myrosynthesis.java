package me.deadybbb.myrosynthesis;

import me.deadybbb.myrosynthesis.volcanozone.ZonesCommandExecutor;
import me.deadybbb.myrosynthesis.volcanozone.ZonesHandler;
import me.deadybbb.myrosynthesis.welcomebook.WelcomeBookListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Myrosynthesis extends JavaPlugin {
    WelcomeBookListener welcomeBookListener;
    ZonesHandler zonesHandler;

    @Override
    public void onEnable() {
        welcomeBookListener = new WelcomeBookListener(this);
        welcomeBookListener.checkData();
        getServer().getPluginManager().registerEvents(welcomeBookListener, this);

        zonesHandler = new ZonesHandler(this);
        getCommand("zone").setExecutor(new ZonesCommandExecutor(zonesHandler));
        getCommand("zone").setTabCompleter(new ZonesCommandExecutor(zonesHandler));
    }

    @Override
    public void onDisable() {
        welcomeBookListener.saveData();
        zonesHandler.playerTimeInZones.clear();
        zonesHandler.zones.clear();
    }
}
