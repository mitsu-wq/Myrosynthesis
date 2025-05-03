package me.deadybbb.myrosynthesis.welcomebook;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class WelcomeBookListener implements Listener {
    private File playerDataFile;
    private FileConfiguration playerData;
    private final JavaPlugin plugin;

    public WelcomeBookListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void checkData() {
        playerDataFile = new File(plugin.getDataFolder(), "playerData.yml");
        if (!playerDataFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                playerDataFile.createNewFile();
                plugin.getLogger().info("Created new playerData.yml");
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create playerData.yml", e);
            }
        }
        playerData = YamlConfiguration.loadConfiguration(playerDataFile);
    }

    public void saveData() {
        try {
            playerData.save(playerDataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save player data", e);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        if (!playerData.saveToString().contains(playerName)) {
            playerData.set(playerName, "хуй");
            saveData();
            ItemStack book = createWelcomeBook();
            event.getPlayer().getInventory().addItem(book);
            event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<italic><gold>Вы чувствуете что-то странное</gold></italic>"));
        }
    }

    public ItemStack createWelcomeBook() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();

        bookMeta.setTitle("Руководство для новичков");
        bookMeta.setAuthor("§kхуййййййй");

        MiniMessage miniMessage = MiniMessage.miniMessage();
        List<Component> pages = new ArrayList<>();

        Component page1 = miniMessage.deserialize(
                "<italic>Добро пожаловать! Пожалуйста, просмотрите сообщение, подготовленное специально для вас (кликните на него):</italic>\n" +
                "<click:open_url:'https://youtu.be/kbFS_on8M7E?si=eAvhFnUznYND0p6N'><obf>https://youtu.be/kbFS_on8M7E?si=eAvhFnUznYND0p6N</obf></click>"
        );
        pages.add(page1);

        bookMeta.pages(pages);
        book.setItemMeta(bookMeta);

        return book;
    }
}
