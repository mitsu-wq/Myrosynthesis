package me.deadybbb.myrosynthesis.welcomeevent;

import me.deadybbb.myrosynthesis.basic.BasicConfigHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class WelcomeBooksConfigHandler extends BasicConfigHandler {
    public WelcomeBooksConfigHandler(JavaPlugin plugin) {
        super(plugin, "welcomebooks_config.yml");
    }

    public List<WelcomeBook> loadWelcomeBooksFromConfig() {
        reloadConfig();
        List<WelcomeBook> welcomeBooks = new ArrayList<WelcomeBook>();
        if (config.getConfigurationSection("welcome-books") == null) return welcomeBooks;

        for (String name : config.getConfigurationSection("welcome-books").getKeys(false)) {
            String path = "welcome-books." + name;
            String bookId = config.getString(path + ".book-id");
            String welcomeText = config.getString(path + ".welcome-text");

            welcomeBooks.add(new WelcomeBook(name, bookId, welcomeText));
        }
        plugin.getLogger().info("Loaded " + welcomeBooks.size() + " welcome books.");
        return welcomeBooks;
    }

    public boolean saveWelcomeBooksToConfig(List<WelcomeBook> welcomeBooks) {
        config.set("welcome-books", null);

        for (WelcomeBook welcomeBook : welcomeBooks) {
            String path = "welcome-books." + welcomeBook.name;
            config.set(path + ".book-id", welcomeBook.bookId);
            config.set(path + ".welcome-text", welcomeBook.welcomeText);
        }

        return saveConfig();
    }
}
