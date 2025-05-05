package me.deadybbb.myrosynthesis.custombooks;

import me.deadybbb.myrosynthesis.basic.LegacyTextHandler;
import me.deadybbb.myrosynthesis.customeffects.EffectsHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class BooksHandler {
    private final BooksConfigHandler booksConfigHandler;
    private final EffectsHandler effectsHandler;
    private final LegacyTextHandler legacyTextHandler;
    public final NamespacedKey key;
    public List<Book> books;
    private final JavaPlugin plugin;
    private final Map<UUID, BukkitRunnable> activeTasks = new HashMap<>();

    public BooksHandler(JavaPlugin plugin, EffectsHandler effectsHandler, NamespacedKey key, LegacyTextHandler legacyTextHandler) {
        this.plugin = plugin;
        this.key = key;
        this.legacyTextHandler = legacyTextHandler;
        this.booksConfigHandler = new BooksConfigHandler(plugin);
        this.effectsHandler = effectsHandler;
        this.books = booksConfigHandler.loadBooksFromConfig();
    }

    public ItemStack createCustomBook(Book book) {
        ItemStack bookItem = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) bookItem.getItemMeta();
        meta.setTitle(book.title);
        meta.setAuthor(book.author);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        meta.setUnbreakable(true);

        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, book.bookId);

        List<Component> pages = splitContentToPages(book.content);
        meta.pages(pages);

        bookItem.setItemMeta(meta);
        return bookItem;
    }

    public List<String> getBooksIds() {
        return books.stream().map(book -> book.bookId).collect(Collectors.toList());
    }

    public Book findBook(String bookId) {
        return books.stream()
                .filter(b -> b.bookId.equals(bookId))
                .findFirst()
                .orElse(null);
    }

    public List<Component> splitContentToPages(String content) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        List<Component> pages = new ArrayList<Component>();
        int maxCharsPerPage = 1023;
        int maxPages = 100;

        for (int i = 0; i < content.length() && pages.size() < maxPages; i += maxCharsPerPage) {
            int end = Math.min(i + maxCharsPerPage, content.length());
            String pageContent = content.substring(i, end);
            pages.add(legacyTextHandler.parseText(pageContent));
        }
        return pages;
    }

    public void startEffectTimer(Player player, ItemStack item, long delay, long period) {
        if (!item.hasItemMeta()) return;

        String bookId = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if (bookId == null) return;

        Optional<Book> bookOpt = books.stream().filter(b -> b.bookId.equals(bookId)).findFirst();
        if (bookOpt.isEmpty() || bookOpt.get().effects.isEmpty()) return;

        Book book = bookOpt.get();
        UUID playerId = player.getUniqueId();

        if (activeTasks.containsKey(playerId)) {
            activeTasks.get(playerId).cancel();
            activeTasks.remove(playerId);
        }

        BukkitRunnable task = new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                ItemStack itemInOffHand = player.getInventory().getItemInOffHand();

                if (!isValidBook(itemInMainHand, bookId) && !isValidBook(itemInOffHand, bookId)) {
                    cancel();
                    activeTasks.remove(playerId);
                    return;
                }

                for (String effectId : book.effects) {
                    effectsHandler.applyEffectById(player, effectId, ticks);
                }
                ticks += (int) period;
            }
        };

        task.runTaskTimer(plugin, delay, period);
        activeTasks.put(playerId, task);
    }

    private boolean isValidBook(ItemStack item, String bookId) {
        if (item.getType() != Material.WRITTEN_BOOK || !item.hasItemMeta()) {
            return false;
        }
        String itemBookId = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
        return bookId.equals(itemBookId);
    }
}
