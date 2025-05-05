package me.deadybbb.myrosynthesis.customzone;

import me.deadybbb.myrosynthesis.basic.LegacyTextHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ZonesCommandExecutor implements CommandExecutor, TabCompleter {
    ZonesHandler zonesHandler;
    LegacyTextHandler textHandler;

    public ZonesCommandExecutor(ZonesHandler zonesHandler, LegacyTextHandler textHandler) {
        this.zonesHandler = zonesHandler;
        this.textHandler = textHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Эта команда только для игроков!");
            return true;
        }

        if (!sender.hasPermission("myrosynthesis.zone")) {
            textHandler.sendFormattedMessage(player, "<red>У вас недостаточно прав для использования этой команды!");
            return true;
        }

        if (args.length == 0) {
            textHandler.sendFormattedMessage(player, "<red>Использование: /zone <create|pos1|pos2|toggle|delete|change|effects|reload> <name|add|remove> [zoneName] [effectName]");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "pos1":
                zonesHandler.pos1.put(player.getUniqueId(), player.getLocation());
                textHandler.sendFormattedMessage(player, "<green>Первая точка установлена: " + player.getLocation().toVector().toString().replace(",", " "));
                return true;

            case "pos2":
                zonesHandler.pos2.put(player.getUniqueId(), player.getLocation());
                textHandler.sendFormattedMessage(player, "<green>Вторая точка установлена: " + player.getLocation().toVector().toString().replace(",", " "));
                return true;

            case "create":
                if (args.length < 2) {
                    textHandler.sendFormattedMessage(player, "<red>Укажите имя зоны: /zone create <name>");
                    return true;
                }
                String zoneName = args[1];
                if (zonesHandler.zones.stream().anyMatch(z -> z.name.equals(zoneName))) {
                    textHandler.sendFormattedMessage(player, "<red>Зона с именем " + zoneName + " уже существует!");
                    return true;
                }
                Location p1 = zonesHandler.pos1.get(player.getUniqueId());
                Location p2 = zonesHandler.pos2.get(player.getUniqueId());
                if (p1 == null || p2 == null || !p1.getWorld().equals(p2.getWorld())) {
                    textHandler.sendFormattedMessage(player, "<red>Установите обе точки в одном мире!");
                    return true;
                }
                zonesHandler.zones.add(new Zone(zoneName, p1, p2, new ArrayList<String>(), false));
                zonesHandler.saveZones();
                textHandler.sendFormattedMessage(player, "<green>Зона "+zoneName+" создана!");
                return true;

            case "toggle":
                if (args.length < 2) {
                    textHandler.sendFormattedMessage(player, "<red>Укажите имя зоны: /zone toggle <name>");
                    return true;
                }
                Zone toggleZone = zonesHandler.zones.stream().filter(z -> z.name.equals(args[1])).findFirst().orElse(null);
                if (toggleZone == null) {
                    textHandler.sendFormattedMessage(player, "<red>Зона " + args[1] + " не найдена!");
                    return true;
                }
                toggleZone.displayEnabled = !toggleZone.displayEnabled;
                zonesHandler.saveZones();
                textHandler.sendFormattedMessage(player, "<green>Отображение зоны " + args[1] + " " + (toggleZone.displayEnabled ? "включено" : "выключено"));
                return true;

            case "remove":
                if (args.length < 2) {
                    textHandler.sendFormattedMessage(player, "<red>Укажите имя зоны: /zone remove <name>");
                    return true;
                }
                String zoneNameRemove = args[1];
                boolean removed = zonesHandler.zones.removeIf(z -> z.name.equals(zoneNameRemove));
                if (removed) {
                    zonesHandler.saveZones();
                    textHandler.sendFormattedMessage(player, "<green>Зона " + zoneNameRemove + " удалена!");
                } else {
                    textHandler.sendFormattedMessage(player, "<red>Зона " + zoneNameRemove + " не найдена!");
                }
                return true;

            case "change":
                if (args.length < 2) {
                    textHandler.sendFormattedMessage(player, "<red>Укажите имя зоны: /zone change <name>");
                    return true;
                }
                String changeZoneName = args[1];
                Zone changeZone = zonesHandler.zones.stream().filter(z -> z.name.equals(changeZoneName)).findFirst().orElse(null);
                if (changeZone == null) {
                    textHandler.sendFormattedMessage(player, "<red>Зона " + args[1] + " не найдена!");
                    return true;
                }
                Location newP1 = zonesHandler.pos1.get(player.getUniqueId());
                Location newP2 = zonesHandler.pos2.get(player.getUniqueId());
                if (newP1 == null || newP2 == null || !newP1.getWorld().equals(newP2.getWorld())) {
                    textHandler.sendFormattedMessage(player, "<red>Установите обе точки в одном мире!");
                    return true;
                }
                changeZone.min = newP1;
                changeZone.max = newP2;
                zonesHandler.saveZones();
                textHandler.sendFormattedMessage(player, "<green>Границы зоны " + changeZoneName + " обновлены!");
                return true;

            case "effects":
                if (args.length < 2) {
                    textHandler.sendFormattedMessage(player, "<red>Использование команды: /zone effects <add|remove> [zoneName] [effectName]");
                    return true;
                }
                switch (args[1].toLowerCase()) {
                    case "add":
                        if (args.length < 4) {
                            textHandler.sendFormattedMessage(player, "<red>Использование: /zone effects add <zoneName> <effectName>");
                            return true;
                        }
                        if (zonesHandler.addEffectToZone(args[2], args[3])) {
                            textHandler.sendFormattedMessage(player, "<green>Эффект " + args[3] + " добавлен в зону " + args[2] + "!");
                        } else {
                            textHandler.sendFormattedMessage(player, "<red>Эффект " + args[3] + " не может быть добавлен в зону "+args[2]+"!");
                        }
                        return true;
                    case "remove":
                        if (args.length < 4) {
                            textHandler.sendFormattedMessage(player, "<red>Использование: /zone effects remove <zoneName> <effectName>");
                            return true;
                        }
                        if (zonesHandler.removeEffectFromZone(args[2], args[3])) {
                            textHandler.sendFormattedMessage(player, "<green>Эффект " + args[3] + " убран из зоны " + args[2] + "!");
                        } else {
                            textHandler.sendFormattedMessage(player, "<red>Эффект " + args[3] + " не может быть убран из зоны "+args[2]+"!");
                        }
                        return true;
                }
            case "reload":
                if (args.length < 2) {
                    textHandler.sendFormattedMessage(player, "<red>Использование команды: /zone reload");
                    return true;
                }

                if (zonesHandler.reloadZonesFromConfig()) {
                    textHandler.sendFormattedMessage(player, "<green>Конфигурация загружена успешно!");
                } else {
                    textHandler.sendFormattedMessage(player, "<red>Конфигурация не была загружена!");
                }
                return true;
            default:
                textHandler.sendFormattedMessage(player, "<red>Неизвестная команда. Используйте: /zone <create|pos1|pos2|toggle|delete|change|reload> [name]");
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subCommands = List.of("create", "pos1", "pos2", "toggle", "remove", "change", "effects");
            String input = args[0].toLowerCase();
            suggestions.addAll(subCommands.stream()
                    .filter(cmd -> cmd.startsWith(input))
                    .toList());
        } else if (args.length == 2 && List.of("toggle", "delete", "change").contains(args[0].toLowerCase())) {
            String input = args[1].toLowerCase();
            suggestions.addAll(zonesHandler.getAllZonesNames(input));
        } else if (args.length == 2 && Objects.equals("effects", args[0].toLowerCase())) {
            List<String> subCommands = List.of("add", "remove");
            String input = args[1].toLowerCase();
            suggestions.addAll(subCommands.stream().filter(cmd -> cmd.startsWith(input)).toList());
        } else if (args.length == 3 && Objects.equals("effects", args[0].toLowerCase()) && List.of("add", "remove").contains(args[1].toLowerCase())) {
            String input = args[2].toLowerCase();
            suggestions.addAll(zonesHandler.getAllZonesNames(input));
        } else if (args.length == 4 && Objects.equals("effects", args[0].toLowerCase()) && List.of("add", "remove").contains(args[1].toLowerCase())) {
            String input = args[3].toLowerCase();
            suggestions.addAll(zonesHandler.getAllEffectsNames(input));
        }

        return suggestions;
    }
}
