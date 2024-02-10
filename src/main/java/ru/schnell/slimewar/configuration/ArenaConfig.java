package ru.schnell.slimewar.configuration;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ArenaConfig extends Config{

    public ArenaConfig(Plugin plugin) {
        super(plugin, "ArenaConfig");
    }

    @Override
    protected void onFirstLoad() {

    }

    @Override
    protected void checkDefault() {
        setIfNotExists("player-spawn-location", "0 0 0");
        setIfNotExists("slime-spawn-locations", Arrays.asList("0 0 0"));
    }

    public Location getSpawnLocation() {
        String[] args = config.getString("player-spawn-location").split(" ");

        World world = Bukkit.getWorld("world");
        int x = Integer.valueOf(args[0]);
        int y = Integer.valueOf(args[1]);
        int z = Integer.valueOf(args[2]);

        return new Location(world, x, y, z);
    }

    public Collection<Location> getSlimeSpawnLocations() {
        List<Location> spawnLocations = new ArrayList<>();

        for (String wrapped : config.getStringList("slime-spawn-locations")) {
            String[] args = wrapped.split(" ");

            World world = Bukkit.getWorld("world");
            int x = Integer.valueOf(args[0]);
            int y = Integer.valueOf(args[1]);
            int z = Integer.valueOf(args[2]);

            spawnLocations.add(new Location(world, x, y, z));
        }

        return spawnLocations;
    }

}
