package me.deadybbb.myrosynthesis.volcanozone;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ZonesCommandExecutor implements CommandExecutor, TabCompleter {
    ZonesHandler zonesHandler;

    public ZonesCommandExecutor(ZonesHandler zonesHandler) {
        this.zonesHandler = zonesHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Эта команда только для игроков!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Использование: /zone <create|pos1|pos2|toggle|delete|change> [name]");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "pos1":
                zonesHandler.pos1.put(player.getUniqueId(), player.getLocation());
                player.sendMessage("Первая точка установлена: " + player.getLocation().toVector());
                return true;

            case "pos2":
                zonesHandler.pos2.put(player.getUniqueId(), player.getLocation());
                player.sendMessage("Вторая точка установлена: " + player.getLocation().toVector());
                return true;

            case "create":
                if (args.length < 2) {
                    player.sendMessage("Укажите имя зоны: /zone create <name>");
                    return true;
                }
                String zoneName = args[1];
                if (zonesHandler.zones.stream().anyMatch(z -> z.name.equals(zoneName))) {
                    player.sendMessage("Зона с именем " + zoneName + " уже существует!");
                    return true;
                }
                Location p1 = zonesHandler.pos1.get(player.getUniqueId());
                Location p2 = zonesHandler.pos2.get(player.getUniqueId());
                if (p1 == null || p2 == null || !p1.getWorld().equals(p2.getWorld())) {
                    player.sendMessage("Установите обе точки в одном мире!");
                    return true;
                }
                zonesHandler.zones.add(new Zone(zoneName, p1, p2, false));
                zonesHandler.saveZones();
                player.sendMessage("Зона " + zoneName + " создана!");
                return true;

            case "toggle":
                if (args.length < 2) {
                    player.sendMessage("Укажите имя зоны: /zone toggle <name>");
                    return true;
                }
                Zone toggleZone = zonesHandler.zones.stream().filter(z -> z.name.equals(args[1])).findFirst().orElse(null);
                if (toggleZone == null) {
                    player.sendMessage("Зона " + args[1] + " не найдена!");
                    return true;
                }
                toggleZone.displayEnabled = !toggleZone.displayEnabled;
                zonesHandler.saveZones();
                player.sendMessage("Отображение зоны " + args[1] + " " + (toggleZone.displayEnabled ? "включено" : "выключено"));
                return true;

            case "delete":
                if (args.length < 2) {
                    player.sendMessage("Укажите имя зоны: /zone delete <name>");
                    return true;
                }
                Zone deleteZone = zonesHandler.zones.stream().filter(z -> z.name.equals(args[1])).findFirst().orElse(null);
                if (deleteZone == null) {
                    player.sendMessage("Зона " + args[1] + " не найдена!");
                    return true;
                }
                zonesHandler.zones.remove(deleteZone);
                zonesHandler.saveZones();
                player.sendMessage("Зона " + args[1] + " удалена!");
                return true;

            case "change":
                if (args.length < 2) {
                    player.sendMessage("Укажите имя зоны: /zone change <name>");
                    return true;
                }
                String changeZoneName = args[1];
                Zone changeZone = zonesHandler.zones.stream().filter(z -> z.name.equals(changeZoneName)).findFirst().orElse(null);
                if (changeZone == null) {
                    player.sendMessage("Зона " + changeZoneName + " не найдена!");
                    return true;
                }
                Location newP1 = zonesHandler.pos1.get(player.getUniqueId());
                Location newP2 = zonesHandler.pos2.get(player.getUniqueId());
                if (newP1 == null || newP2 == null || !newP1.getWorld().equals(newP2.getWorld())) {
                    player.sendMessage("Установите обе точки в одном мире!");
                    return true;
                }
                changeZone.min = newP1;
                changeZone.max = newP2;
                zonesHandler.saveZones();
                player.sendMessage("Границы зоны " + changeZoneName + " обновлены!");
                return true;

            default:
                player.sendMessage("Неизвестная команда. Используйте: /zone <create|pos1|pos2|toggle|delete|change>");
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subCommands = List.of("create", "pos1", "pos2", "toggle", "delete", "change");
            String input = args[0].toLowerCase();
            suggestions.addAll(subCommands.stream()
                    .filter(cmd -> cmd.startsWith(input))
                    .toList());
        } else if (args.length == 2 && List.of("toggle", "delete", "change").contains(args[0].toLowerCase())) {
            String input = args[1].toLowerCase();
            suggestions.addAll(zonesHandler.zones.stream()
                    .map(zone -> zone.name)
                    .filter(name -> name.toLowerCase().startsWith(input))
                    .toList());
        }

        return suggestions;
    }
}
