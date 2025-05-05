package me.deadybbb.myrosynthesis.customeffects;

import me.deadybbb.myrosynthesis.basic.LegacyTextHandler;
import me.deadybbb.myrosynthesis.customzone.ZonesHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EffectsCommandExecutor implements CommandExecutor, TabCompleter {
    private final EffectsHandler effectsHandler;
    private final LegacyTextHandler textHandler;
    private final ZonesHandler zonesHandler;
    private static final List<String> SUBCOMMANDS = Arrays.asList("add", "remove", "reload");

    public EffectsCommandExecutor(EffectsHandler effectsHandler, ZonesHandler zonesHandler, LegacyTextHandler textHandler) {
        this.effectsHandler = effectsHandler;
        this.zonesHandler = zonesHandler;
        this.textHandler = textHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Эта команда только для игроков!");
            return true;
        }

        if (!sender.hasPermission("myrosynthesis.effect")) {
            textHandler.sendFormattedMessage(player, "<red>У вас недостаточно прав для использования этой команды!");
            return true;
        }

        if (args.length == 0) {
            textHandler.sendFormattedMessage(player, "<red>Использование: /effect <add|remove|reload> [name] [stageTime] [time] [level]");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "add":
                if (args.length != 5) {
                    textHandler.sendFormattedMessage(player, "<red>Использование: /effect add <name> <stageTime> <time> <level>");
                    return true;
                }
                String name = args[1];
                try {
                    int stageTime = Integer.parseInt(args[2]);
                    int time = Integer.parseInt(args[3]);
                    int level = Integer.parseInt(args[4]);
                    if (effectsHandler.addEffect(name, stageTime, time, level)) {
                        textHandler.sendFormattedMessage(player, "<green>Эффект " + name + " успешно добавлен!");
                    } else {
                        textHandler.sendFormattedMessage(player, "<red>Ошибка: Эффект " + name + " уже существует или имеет недопустимое имя!");
                    }
                } catch (NumberFormatException e) {
                    textHandler.sendFormattedMessage(player, "<red>Параметры stageTime, time и level должны быть целыми числами!");
                }
                return true;

            case "remove":
                if (args.length != 2) {
                    textHandler.sendFormattedMessage(player, "<red>Использование: /effect remove <name>");
                    return true;
                }
                String removeName = args[1];
                if (effectsHandler.removeEffect(removeName)) {
                    if (zonesHandler.removeEffectFromZones(removeName)) {
                        textHandler.sendFormattedMessage(player, "<green>Эффект " + removeName + " успешно удалён!");
                    } else {
                        textHandler.sendFormattedMessage(player, "<red>Эффект " + removeName + " не найден!");
                    }
                } else {
                    textHandler.sendFormattedMessage(player, "<red>Эффект " + removeName + " не найден!");
                }
                return true;

            case "reload":
                if (args.length != 1) {
                    textHandler.sendFormattedMessage(player, "<red>Использование: /effect reload");
                    return true;
                }

                if(effectsHandler.reloadEffectsFromConfig()){
                    textHandler.sendFormattedMessage(player, "<green>Конфигурация загружена успешно!");
                } else {
                    textHandler.sendFormattedMessage(player, "<red>Конфигурация не была загружена!");
                }

                return true;
            default:
                textHandler.sendFormattedMessage(player, "<red>Неизвестная команда. Используйте: /effect <add|remove|reload> [name] [stageTime] [time] [level]");
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            suggestions.addAll(SUBCOMMANDS.stream()
                    .filter(cmd -> cmd.startsWith(input))
                    .toList());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            String input = args[1].toLowerCase();
            suggestions.addAll(effectsHandler.getEffectNames(input));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
            suggestions.add("<name>");
        } else if (args.length >= 3 && args.length <= 5 && args[0].equalsIgnoreCase("add")) {
            suggestions.add(String.valueOf(0));
        }

        return suggestions;
    }
}