package me.deadybbb.myrosynthesis.welcomeevent;

import me.deadybbb.myrosynthesis.custombooks.Book;
import me.deadybbb.myrosynthesis.custombooks.BooksHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class WelcomeBooksHandler {
    private final JavaPlugin plugin;
    private final WelcomeBooksConfigHandler configHandler;
    private final BooksHandler booksHandler;
    public List<WelcomeBook> welcomeBooks;

    public WelcomeBooksHandler(JavaPlugin plugin, BooksHandler booksHandler) {
        this.plugin = plugin;
        this.configHandler = new WelcomeBooksConfigHandler(plugin);
        this.welcomeBooks = configHandler.loadWelcomeBooksFromConfig();
        this.booksHandler = booksHandler;
    }

    public boolean reloadConfig() {
        try{
            this.welcomeBooks = configHandler.loadWelcomeBooksFromConfig();
            return true;
        } catch (Exception e){
            plugin.getLogger().log(Level.SEVERE, "Failed to load welcome books config", e);
            return false;
        }
    }

    public List<String> getWelcomeBookNames() {
        return welcomeBooks.stream().map(book -> book.name).collect(Collectors.toList());
    }

    public List<String> getBooksIds() {
        return booksHandler.getBooksIds();
    }

    public ItemStack getWelcomeBook(WelcomeBook welcomeBook) {
        Book book = booksHandler.findBook(welcomeBook.bookId);
        if (book != null) {
            return booksHandler.createCustomBook(book);
        }
        return null;
    }

    public boolean addNewWelcomeBook(String name, String bookId, String welcomeText) {
        if (name == null || bookId == null || welcomeText == null) return false;
        if (!name.matches("[a-zA-Z0-9_-]+")) return false;
        if (booksHandler.findBook(bookId) == null) return false;
        for (WelcomeBook welcomeBook : welcomeBooks) {
            if (welcomeBook.name.equals(name)) return false;
        }
        WelcomeBook welcomeBook = new WelcomeBook(name, bookId, welcomeText);
        welcomeBooks.add(welcomeBook);
        return true;
    }

    public boolean removeWelcomeBook(String name) {
        if (name == null) return false;
        return welcomeBooks.removeIf(book -> book.name.equals(name));
    }

    public void saveConfig() {
        configHandler.saveWelcomeBooksToConfig(welcomeBooks);
    }
}
