package me.deadybbb.myrosynthesis.customeffects;

public class Effect {
    public String name;
    int stageTime; // Время, после которого активируется эффект в тиках
    int time; // Время длительности в тиках
    int level; // Уровень эффекта (для PotionEffect)

    public Effect(String name, int stageTime, int time, int level) {
        this.name = name;
        this.stageTime = stageTime;
        this.time = time;
        this.level = level;
    }
}