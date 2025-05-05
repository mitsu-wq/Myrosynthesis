package me.deadybbb.myrosynthesis.customeffects;

import me.deadybbb.myrosynthesis.basic.BasicConfigHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class EffectsConfigHandler extends BasicConfigHandler {
    private static final List<String> VALID_PREFIXES = List.of("slow", "poison", "wither", "confusion", "blindness", "fire");

    public EffectsConfigHandler(JavaPlugin plugin) {
        super(plugin, "effects_config.yml");
    }

    public List<Effect> loadEffectsFromConfig() {
        reloadConfig();
        List<Effect> effects = new ArrayList<>();
        if (config.getConfigurationSection("effects") == null) return effects;

        for (String effectName : config.getConfigurationSection("effects").getKeys(false)) {
            if (effects.stream().anyMatch(e -> e.name.equals(effectName))) {
                plugin.getLogger().warning("Duplicate effect name found: " + effectName);
                continue;
            }
            if (VALID_PREFIXES.stream().noneMatch(prefix -> effectName.startsWith(prefix))) {
                plugin.getLogger().warning("Effect " + effectName + " does not start with a valid prefix: " + VALID_PREFIXES);
                continue;
            }
            String path = "effects." + effectName;
            int stageTime = config.getInt(path + ".stageTime", 0);
            int time = config.getInt(path + ".time", 0);
            int level = config.getInt(path + ".level", 0);
            effects.add(new Effect(effectName, stageTime, time, level));
        }
        plugin.getLogger().info("Loaded " + effects.size() + " effects.");
        return effects;
    }

    public boolean saveEffectsToConfig(List<Effect> effects) {
        config.set("effects", null);

        for (Effect effect : effects) {
            String path = "effects." + effect.name;
            config.set(path + ".stageTime", effect.stageTime);
            config.set(path + ".time", effect.time);
            config.set(path + ".level", effect.level);
        }

        return saveConfig();
    }
}