package ru.schnell.slimewar.area;

import org.bukkit.Location;
import ru.schnell.slimewar.configuration.LobbyConfig;

public class Lobby {

    private Location spawnLocation;

    public Lobby(LobbyConfig config) {
        this.spawnLocation = config.getLobbyLocation();
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }
}
