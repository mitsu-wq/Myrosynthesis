package me.deadybbb.myrosynthesis.welcomeevent;

import me.deadybbb.myrosynthesis.basic.LegacyTextHandler;
import me.deadybbb.myrosynthesis.custombooks.BooksHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class WelcomeEventListener implements Listener {
    public final WelcomeBooksHandler welcomeBooksHandler;
    public final PlayersHandler playersHandler;
    private final LegacyTextHandler legacyTextHandler;
    private final JavaPlugin plugin;

    public WelcomeEventListener(JavaPlugin plugin, WelcomeBooksHandler welcomeBooksHandler, PlayersHandler playersHandler, LegacyTextHandler legacyTextHandler) {
        this.plugin = plugin;
        this.playersHandler = playersHandler;
        this.welcomeBooksHandler = welcomeBooksHandler;
        this.legacyTextHandler = legacyTextHandler;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        issueWelcomeBooks(event.getPlayer(), false);
    }

    public boolean issueWelcomeBooks(Player player, boolean force) {
        if (force || playersHandler.savePlayerToConfig(player)) {
            for (WelcomeBook welcomeBook : welcomeBooksHandler.welcomeBooks) {
                ItemStack book = welcomeBooksHandler.getWelcomeBook(welcomeBook);
                if (book != null) {
                    player.getInventory().addItem(book);
                    legacyTextHandler.sendFormattedMessage(player, welcomeBook.welcomeText);
                }
            }
            return true;
        }
        return false;
    }
}
