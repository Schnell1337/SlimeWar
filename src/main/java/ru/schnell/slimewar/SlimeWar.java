package ru.schnell.slimewar;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.io.IOUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.async.JdbiExecutor;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import ru.schnell.slimewar.area.Lobby;
import ru.schnell.slimewar.configuration.*;
import ru.schnell.slimewar.database.ConnectionInfo;
import ru.schnell.slimewar.listener.GameListener;
import ru.schnell.slimewar.player.SWPlayerManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SlimeWar extends JavaPlugin {

    private Jdbi jdbi;
    private JdbiExecutor jdbiExecutor;

    public void onEnable() {
        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        DatabaseConfig databaseConfig = new DatabaseConfig(this);
        initJDBI(databaseConfig.getConnectionInfo());

        ArenaConfig arenaConfig = new ArenaConfig(this);
        EnchantConfig enchantConfig = new EnchantConfig(this);
        GameConfig gameConfig = new GameConfig(this);
        LobbyConfig lobbyConfig = new LobbyConfig(this);
        SlimeConfig slimeConfig = new SlimeConfig(this);

        SWPlayerManager playerManager = new SWPlayerManager(jdbiExecutor);
        SlimeWarGame game = new SlimeWarGame(this, gameConfig, lobbyConfig, arenaConfig, slimeConfig, playerManager);
        new GameListener(this, jdbiExecutor, playerManager, game, gameConfig, enchantConfig, slimeConfig);

    }

    private void initJDBI(ConnectionInfo info) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(
                "jdbc:mysql://" + info.host() +
                        ":" + info.port() +
                        "/" + info.database() + "?autoReconnect=true&useSSL=false&characterEncoding=utf8"
        );
        config.setUsername(info.user());
        config.setPassword(info.password());


        HikariDataSource dataSource = new HikariDataSource(config);

        jdbi = Jdbi.create(dataSource);
        //jdbi.installPlugin(new CaffeineCachePlugin());
        jdbi.installPlugin(new SqlObjectPlugin());

        try (InputStream inputStream = this.getClass().getResourceAsStream("/template.sql")){
            jdbi.useHandle(handle -> handle.createScript(new String(IOUtils.toByteArray(inputStream))).execute());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Executor executor = Executors.newFixedThreadPool(8);
        jdbiExecutor = JdbiExecutor.create(jdbi, executor);

    }

}
