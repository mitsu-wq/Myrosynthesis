package me.deadybbb.myrosynthesis;

import me.deadybbb.myrosynthesis.basic.LegacyTextHandler;
import me.deadybbb.myrosynthesis.custombooks.BooksHandler;
import me.deadybbb.myrosynthesis.custombooks.BooksListener;
import me.deadybbb.myrosynthesis.customeffects.EffectsCommandExecutor;
import me.deadybbb.myrosynthesis.customeffects.EffectsHandler;
import me.deadybbb.myrosynthesis.customzone.ZonesCommandExecutor;
import me.deadybbb.myrosynthesis.customzone.ZonesHandler;
import me.deadybbb.myrosynthesis.customzone.ZonesListener;
import me.deadybbb.myrosynthesis.welcomeevent.PlayersHandler;
import me.deadybbb.myrosynthesis.welcomeevent.WelcomeBooksHandler;
import me.deadybbb.myrosynthesis.welcomeevent.WelcomeEventCommandExecutor;
import me.deadybbb.myrosynthesis.welcomeevent.WelcomeEventListener;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class Myrosynthesis extends JavaPlugin {
    ZonesHandler zonesHandler;

    private NamespacedKey bookKey;

    @Override
    public void onEnable() {
        LegacyTextHandler legacyTextHandler = new LegacyTextHandler();

        EffectsHandler effectsHandler = new EffectsHandler(this);
        zonesHandler = new ZonesHandler(this, effectsHandler, legacyTextHandler);
        ZonesListener zonesListener = new ZonesListener(zonesHandler);
        getServer().getPluginManager().registerEvents(zonesListener, this);
        ZonesCommandExecutor zonesCommandExecutor = new ZonesCommandExecutor(zonesHandler, legacyTextHandler);
        getCommand("zone").setExecutor(zonesCommandExecutor);
        getCommand("zone").setTabCompleter(zonesCommandExecutor);

        EffectsCommandExecutor effectsCommandExecutor = new EffectsCommandExecutor(effectsHandler, zonesHandler, legacyTextHandler);
        getCommand("effect").setExecutor(effectsCommandExecutor);
        getCommand("effect").setTabCompleter(effectsCommandExecutor);



        bookKey = new NamespacedKey(this, "books");
        BooksHandler booksHandler = new BooksHandler(this, effectsHandler, bookKey, legacyTextHandler);
        getServer().getPluginManager().registerEvents(new BooksListener(booksHandler, bookKey), this);

        PlayersHandler playersHandler = new PlayersHandler(this, legacyTextHandler);
        WelcomeBooksHandler welcomeBooksHandler = new WelcomeBooksHandler(this, booksHandler);
        WelcomeEventListener welcomeEventListener = new WelcomeEventListener(this, welcomeBooksHandler, playersHandler, legacyTextHandler);
        WelcomeEventCommandExecutor welcomeEventCommandExecutor = new WelcomeEventCommandExecutor(this, welcomeEventListener, legacyTextHandler);
        getServer().getPluginManager().registerEvents(welcomeEventListener, this);
        getCommand("welcomebooks").setExecutor(welcomeEventCommandExecutor);
        getCommand("welcomebooks").setTabCompleter(welcomeEventCommandExecutor);
    }

    @Override
    public void onDisable() {
        zonesHandler.exit();
    }
}
