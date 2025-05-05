package me.deadybbb.myrosynthesis.customeffects;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class EffectsHandler {
    private final EffectsConfigHandler effectsConfigHandler;
    public List<Effect> effects = new ArrayList<>();
    private final JavaPlugin plugin;
    private final Map<String, PotionEffectType> effectTypes = new HashMap<>();
    private static final List<String> VALID_PREFIXES = List.of("slow", "poison", "wither", "confusion", "blindness", "fire");

    public EffectsHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.effectsConfigHandler = new EffectsConfigHandler(plugin);
        this.effects.addAll(effectsConfigHandler.loadEffectsFromConfig());
        effectTypes.put("slow", PotionEffectType.SLOW);
        effectTypes.put("poison", PotionEffectType.POISON);
        effectTypes.put("wither", PotionEffectType.WITHER);
        effectTypes.put("confusion", PotionEffectType.CONFUSION);
        effectTypes.put("blindness", PotionEffectType.BLINDNESS);
    }

    public boolean reloadEffectsFromConfig() {
        try{
            this.effects.clear();
            this.effects.addAll(effectsConfigHandler.loadEffectsFromConfig());
            return true;
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load effects config", e);
            return false;
        }
    }

    /**
     * Applies an effect to a player based on the effect ID and elapsed ticks.
     * @param player The player to apply the effect to.
     * @param effectId The ID of the effect (e.g., slow1, fire1).
     * @param ticks The number of ticks elapsed.
     */
    public void applyEffectById(Player player, String effectId, int ticks) {
        if (effectId == null || effectId.isEmpty()) return;

        for (Effect effect : effects) {
            if (!effect.name.equals(effectId) || ticks < effect.stageTime) continue;

            if (effect.name.startsWith("fire")) {
                player.setFireTicks(effect.time);
            } else {
                for (Map.Entry<String, PotionEffectType> entry : effectTypes.entrySet()) {
                    if (effect.name.startsWith(entry.getKey())) {
                        player.addPotionEffect(new PotionEffect(entry.getValue(), effect.time, effect.level - 1));
                        return;
                    }
                }
                plugin.getLogger().warning("Unknown effect ID: " + effectId);
            }
        }
    }

    /**
     * Adds a new effect to the list and saves it to the configuration.
     * @param name The name of the effect (must start with a valid prefix).
     * @param stageTime The time (in ticks) before the effect activates.
     * @param time The duration of the effect (in ticks).
     * @param level The level of the effect.
     * @return true if the effect was added, false if the name is invalid or already exists.
     */
    public boolean addEffect(String name, int stageTime, int time, int level) {
        if (name == null || name.isEmpty() || stageTime < 0 || time <= 0 || level < 0) return false;
        if (VALID_PREFIXES.stream().noneMatch(name::startsWith)) {
            plugin.getLogger().warning("Effect name " + name + " does not start with a valid prefix: " + VALID_PREFIXES);
            return false;
        }
        if (effects.stream().anyMatch(e -> e.name.equals(name))) {
            return false;
        }
        Effect effect = new Effect(name, stageTime, time, level);
        effects.add(effect);
        effectsConfigHandler.saveEffectsToConfig(effects);
        return true;
    }

    /**
     * Removes an effect from the list and saves the configuration.
     * @param name The name of the effect to remove.
     * @return true if the effect was removed, false if it was not found.
     */
    public boolean removeEffect(String name) {
        if (name == null || name.isEmpty()) return false;
        boolean removed = effects.removeIf(e -> e.name.equals(name));
        if (removed) {
            effectsConfigHandler.saveEffectsToConfig(effects);
        }
        return removed;
    }

    /**
     * Gets a list of effect names for autocompletion.
     * @param prefix The prefix to filter by.
     * @return A list of effect names starting with the prefix.
     */
    public List<String> getEffectNames(String prefix) {
        return effects.stream()
                .map(e -> e.name)
                .filter(name -> name.toLowerCase().startsWith(prefix.toLowerCase()))
                .toList();
    }
}