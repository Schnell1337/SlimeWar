package ru.schnell.slimewar.configuration;

import org.bukkit.plugin.Plugin;

public class SlimeConfig extends Config{

    public SlimeConfig(Plugin plugin) {
        super(plugin, "SlimeConfig");
    }

    @Override
    protected void onFirstLoad() {

    }

    @Override
    protected void checkDefault() {
        setIfNotExists("damage-per-lvl", 2.0);
        setIfNotExists("health-per-lvl", 5.0);
        setIfNotExists("speed-per-lvl", 0.5);
    }

    public double getDamagePerLvl() {
        return config.getDouble("damage-per-lvl");
    }

    public double getHealthPerLvl() {
        return config.getDouble("health-per-lvl");
    }

    public double getSpeedPerLvl() {
        return config.getDouble("speed-per-lvl");
    }
}
