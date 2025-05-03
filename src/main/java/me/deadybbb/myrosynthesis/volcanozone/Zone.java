package me.deadybbb.myrosynthesis.volcanozone;

import org.bukkit.Location;

public class Zone {
    public String name;
    public Location min;
    public Location max;
    public boolean displayEnabled;

    public Zone(String name, Location min, Location max, boolean displayEnabled) {
        this.name = name;
        this.min = min;
        this.max = max;
        this.displayEnabled = displayEnabled;
    }
}
