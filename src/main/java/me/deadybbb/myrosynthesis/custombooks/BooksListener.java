package me.deadybbb.myrosynthesis.custombooks;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class BooksListener implements Listener {
    private final BooksHandler handler;
    private final NamespacedKey key;

    public BooksListener(BooksHandler handler, NamespacedKey key) {
        this.handler = handler;
        this.key = key;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();

        checkEvent(event, itemInMainHand);
        checkEvent(event, itemInOffHand);
    }

    private void checkEvent(PlayerInteractEvent event, ItemStack item) {
        if (item.getType() == Material.WRITTEN_BOOK && item.hasItemMeta()) {
            String bookId = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
            if (bookId != null && handler.books.stream().anyMatch(b -> b.bookId.equals(bookId))) {
                handler.startEffectTimer(event.getPlayer(), item, 0L, 20L);
            }
        }
    }
}
