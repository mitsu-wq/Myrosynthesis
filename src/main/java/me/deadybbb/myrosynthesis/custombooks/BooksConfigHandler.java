package me.deadybbb.myrosynthesis.custombooks;

import me.deadybbb.myrosynthesis.basic.BasicConfigHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class BooksConfigHandler extends BasicConfigHandler {
    protected BooksConfigHandler(JavaPlugin plugin) {
        super(plugin, "books_config.yml");
    }

    public List<Book> loadBooksFromConfig() {
        List<Book> books = new ArrayList<Book>();
        if(config.getConfigurationSection("books") == null) return books;

        for (String bookId : config.getConfigurationSection("books").getKeys(false)) {
            String path = "books." + bookId;
            String author = config.getString(path + ".author");
            String title = config.getString(path + ".title");
            String content = config.getString(path + ".content");
            String receiveMessage = config.getString(path + ".receive-message");

            List<String> effects = config.getStringList(path + ".effects");
            books.add(new Book(bookId, title, author, content, receiveMessage, effects));
        }
        plugin.getLogger().info("Loaded " + books.size() + " books.");
        return books;
    }
}
