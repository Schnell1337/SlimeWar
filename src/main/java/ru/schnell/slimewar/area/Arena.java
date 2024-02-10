package ru.schnell.slimewar.area;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Slime;
import ru.schnell.slimewar.configuration.ArenaConfig;
import ru.schnell.slimewar.configuration.SlimeConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Arena {

    private Location spawnLocation;
    private Collection<Location> slimeSpawnLocations;
    private double slimeDamagePerLvl;
    private double slimeHealthPerLvl;
    private double slimeSpeedPerLvl;

    public Arena(ArenaConfig arenaConfig, SlimeConfig slimeConfig) {
        this.spawnLocation = arenaConfig.getSpawnLocation();
        this.slimeSpawnLocations = arenaConfig.getSlimeSpawnLocations();

        this.slimeDamagePerLvl = slimeConfig.getDamagePerLvl();
        this.slimeHealthPerLvl = slimeConfig.getHealthPerLvl();
        this.slimeSpeedPerLvl = slimeConfig.getSpeedPerLvl();
    }

    public Collection<Slime> spawnSlimes(int level) {
        List<Slime> slimes = new ArrayList<>();

        slimeSpawnLocations.forEach(spawnLocation -> {
            Slime slime = spawnLocation.getWorld().spawn(spawnLocation, Slime.class);

            slime.setSize(4);

            slime.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(slimeHealthPerLvl * level);
            slime.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(slimeSpeedPerLvl * level);

            slime.setHealth(slime.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());

            slimes.add(slime);
        });

        return slimes;
    }

    public int spawnSlimeCount() {
        return slimeSpawnLocations.size();
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }
}
