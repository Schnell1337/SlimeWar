package ru.schnell.slimewar.player;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jdbi.v3.core.async.JdbiExecutor;
import ru.schnell.slimewar.SlimeWarGame;
import ru.schnell.slimewar.database.SlimeWarPlayerDao;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SWPlayerManager {

    private Map<UUID, SWPlayer> players = new ConcurrentHashMap<>();
    private JdbiExecutor executor;

    public SWPlayerManager(JdbiExecutor executor) {
        this.executor = executor;
    }

    public SWPlayer register(Player player) {
        SWPlayer swPlayer = players.get(player.getUniqueId());

        if (swPlayer == null) {

            swPlayer = new SWPlayer(player.getUniqueId());
            players.put(player.getUniqueId(), swPlayer);

            SWPlayer finalSwPlayer = swPlayer;

            executor.useExtension(SlimeWarPlayerDao.class, dao -> {
                finalSwPlayer.setKills(dao.getKills(player.getUniqueId()));
                finalSwPlayer.setDamage(dao.getDamage(player.getUniqueId()));
                finalSwPlayer.setMaxWave(dao.getMaxWave(player.getUniqueId()));
            }).exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });
        }

        return swPlayer;
    }

    public SWPlayer getPlayer(UUID id) {
        return players.get(id);
    }

    public SWPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public Collection<SWPlayer> getPlayers() {
        return Collections.unmodifiableCollection(players.values());
    }

}
