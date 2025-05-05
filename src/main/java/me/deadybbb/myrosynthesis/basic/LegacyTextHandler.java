package me.deadybbb.myrosynthesis.basic;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class LegacyTextHandler {
    private static final Pattern LEGACY_PATTERN = Pattern.compile("&[0-9a-fk-or]");

    public Component parseText(String text) {
        Component message;
        if (LEGACY_PATTERN.matcher(text).find()) {
            message = LegacyComponentSerializer.legacyAmpersand().deserialize(text);
        } else {
            message = MiniMessage.miniMessage().deserialize(text);
        }
        return message;
    }

    public void sendFormattedMessage(Player player, String text) {
        player.sendMessage(parseText(text));
    }
}
