package ru.schnell.slimewar.configuration;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class LobbyConfig extends Config{
    public LobbyConfig(Plugin plugin) {
        super(plugin, "LobbyConfig");
    }

    @Override
    protected void onFirstLoad() {

    }

    @Override
    protected void checkDefault() {
        setIfNotExists("lobby-location", "0 0 0");
    }

    public Location getLobbyLocation() {
        String[] args = config.getString("lobby-location").split(" ");

        World world = Bukkit.getWorld("world");
        int x = Integer.valueOf(args[0]);
        int y = Integer.valueOf(args[1]);
        int z = Integer.valueOf(args[2]);

        return new Location(world, x, y, z);
    }
}
