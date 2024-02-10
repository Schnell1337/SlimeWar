package ru.schnell.slimewar.database;

import org.jdbi.v3.sqlobject.config.RegisterArgumentFactory;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import ru.schnell.argument.UUIDArgumentFactory;

import java.util.UUID;

@RegisterArgumentFactory(value = UUIDArgumentFactory.class)
public interface SlimeWarPlayerDao {

    @SqlUpdate("insert ignore into slime_war_players(player, `damage`, `kills`, `max_wave`) values(:player, :damage, :kills, :max_wave) ON DUPLICATE KEY UPDATE kills = kills + :kills, damage = damage + :damage, max_wave = GREATEST(max_wave, :max_wave)")
    void addValue(@Bind("player") UUID player, @Bind("damage") double damage, @Bind("kills") int kills, @Bind("max_wave") int max_wave);

    @SqlQuery("select coalesce(sum(`damage`), 0) from slime_war_players where player = ?")
    double getDamage(UUID id);

    @SqlQuery("select coalesce(sum(`kills`), 0) from slime_war_players where player = ?")
    int getKills(UUID id);

    @SqlQuery("select coalesce(sum(`max_wave`), 0) from slime_war_players where player = ?")
    int getMaxWave(UUID id);
}
