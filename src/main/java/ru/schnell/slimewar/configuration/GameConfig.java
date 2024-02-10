package ru.schnell.slimewar.configuration;

import org.bukkit.plugin.Plugin;

public class GameConfig extends Config{

    public GameConfig(Plugin plugin) {
        super(plugin, "GameConfig");
    }

    @Override
    protected void onFirstLoad() {

    }

    @Override
    protected void checkDefault() {
        setIfNotExists("players-to-start", 1);
        setIfNotExists("upgrade-chance", 5.0);
    }

    public int getPlayersToStart() {
        return config.getInt("players-to-start");
    }

    public double getUpgradeChance() {
        return config.getDouble("upgrade-chance");
    }

}
