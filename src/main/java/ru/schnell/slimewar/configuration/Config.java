package ru.schnell.slimewar.configuration;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public abstract class Config {

    private File rawConfig;
    protected YamlConfiguration config;

    public Config(Plugin plugin, String name) {
        this.rawConfig = new File(plugin.getDataFolder(), name + ".yml");
        boolean isFirstLoad = false;
        if (!rawConfig.exists()) {
            try {
                rawConfig.createNewFile();
                isFirstLoad = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.config = YamlConfiguration.loadConfiguration(rawConfig);

        if (isFirstLoad) onFirstLoad();
        checkDefault();

        save();
    }

    protected void save() {
        try {
            config.save(rawConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void setIfNotExists(String path, Object o) {
        if (!config.contains(path)) config.set(path, o);
    }

    protected abstract void onFirstLoad();

    protected abstract void checkDefault();

}
