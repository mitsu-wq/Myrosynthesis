package me.deadybbb.myrosynthesis;

import org.bukkit.plugin.java.JavaPlugin;

public final class Myrosynthesis extends JavaPlugin {
    WelcomeBookListener welcomeBookListener;

    @Override
    public void onEnable() {
        welcomeBookListener = new WelcomeBookListener(this);
        welcomeBookListener.checkData();
        getServer().getPluginManager().registerEvents(welcomeBookListener, this);
    }

    @Override
    public void onDisable() {
        welcomeBookListener.saveData();
    }
}
