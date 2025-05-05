package me.deadybbb.myrosynthesis.welcomeevent;

import me.deadybbb.myrosynthesis.basic.LegacyTextHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WelcomeEventCommandExecutor implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private final WelcomeEventListener listener;
    private final LegacyTextHandler textHandler;

    public WelcomeEventCommandExecutor(JavaPlugin plugin, WelcomeEventListener welcomeEventListener, LegacyTextHandler textHandler) {
        this.plugin = plugin;
        this.listener = welcomeEventListener;
        this.textHandler = textHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Эта команда доступна только игрокам!");
            return true;
        }

        if (!sender.hasPermission("myrosynthesis.welcomebooks")) {
            textHandler.sendFormattedMessage(player, "<red>У вас недостаточно прав для использования этой команды!");
            return true;
        }

        if (args.length == 0) {
            textHandler.sendFormattedMessage(player, "<red>Использование: /welcomebooks <player|add|remove|reload> [--force|name] [book-id] [welcome-text]");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if(listener.welcomeBooksHandler.reloadConfig() && listener.playersHandler.reloadConfig()) {
                    textHandler.sendFormattedMessage(player, "<green>Конфигурация перезагружена!");
                } else {
                    textHandler.sendFormattedMessage(player, "<red>Конфигурация не перезагружена!");
                }
                return true;
            case "add":
                if (args.length < 4) {
                    textHandler.sendFormattedMessage(player, "<red>Использование: /welcomebooks add <name> <book-id> <welcome-text>");
                    return true;
                }
                String name = args[1];
                String bookId = args[2];
                String welcomeText = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
                boolean status = listener.welcomeBooksHandler.addNewWelcomeBook(name, bookId, welcomeText);
                if (status) {
                    listener.welcomeBooksHandler.saveConfig();
                    textHandler.sendFormattedMessage(player, "<green>Книга " + name + " успешно добавлена в конфиг!");
                    return true;
                } else {
                    textHandler.sendFormattedMessage(player, "<red>Ошибка: имя " + name + " уже существует или book-id '" + bookId + "' не найден!");
                    return true;
                }
            case "remove":
                if (args.length < 2) {
                    textHandler.sendFormattedMessage(player, "<red>Использование: /welcomebooks remove <name>");
                    return true;
                }
                if (listener.welcomeBooksHandler.removeWelcomeBook(args[1])) {
                    listener.welcomeBooksHandler.saveConfig();
                    textHandler.sendFormattedMessage(player, "<green>Книга " + args[1] + " успешно удалена из конфига!");
                    return true;
                } else {
                    textHandler.sendFormattedMessage(player, "<red>Ошибка: имя " + args[1] + " не существует!");
                    return true;
                }
            default:
                boolean force = false;
                String targetPlayerName = null;

                for (String arg : args) {
                    if (arg.equalsIgnoreCase("--force")) {
                        force = true;
                    } else if (targetPlayerName == null) {
                        targetPlayerName = arg;
                    }
                }

                Player targetPlayer;
                if (targetPlayerName == null && sender instanceof Player) {
                    targetPlayer = (Player) sender;
                } else if (targetPlayerName != null) {
                    targetPlayer = plugin.getServer().getPlayer(targetPlayerName);
                    if (targetPlayer == null) {
                        textHandler.sendFormattedMessage(player, "<red>Игрок " + targetPlayerName + " не найден или оффлайн!");
                        return true;
                    }
                } else {
                    textHandler.sendFormattedMessage(player, "<red>Эта команда может быть использована только игроками, если цель не указана!");
                    return true;
                }

                if (listener.issueWelcomeBooks(targetPlayer, force)){
                    textHandler.sendFormattedMessage(player, "<green>Книги выданы игроку " + targetPlayer.getName() + (force ? " (принудительно)" : "") + "!");
                } else {
                    textHandler.sendFormattedMessage(player, "<red>Книги не удалось выдать игроку " + targetPlayer.getName() + (force ? " (принудительно)" : "") + "!");
                }
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.addAll(Arrays.asList("add", "reload", "remove"));
            suggestions.addAll(plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .toList());
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                suggestions.add("<name>");
            } else if (args[0].equalsIgnoreCase("remove")) {
                suggestions.addAll(listener.welcomeBooksHandler.getWelcomeBookNames());
            } else if (!args[0].equalsIgnoreCase("reload")) {
                suggestions.add("--force");
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
            suggestions.addAll(listener.welcomeBooksHandler.getBooksIds());
        } else if (args.length == 4 && args[0].equalsIgnoreCase("add")) {
            suggestions.add("<welcome-text>");
        }

        return suggestions;
    }
}