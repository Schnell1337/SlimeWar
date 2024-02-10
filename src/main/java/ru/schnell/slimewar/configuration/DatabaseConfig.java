package ru.schnell.slimewar.configuration;

import org.bukkit.plugin.Plugin;
import ru.schnell.slimewar.database.ConnectionInfo;

public class DatabaseConfig extends Config{

    public DatabaseConfig(Plugin plugin) {
        super(plugin, "Database");
    }

    @Override
    protected void onFirstLoad() {

    }

    @Override
    protected void checkDefault() {
        setIfNotExists("host", "localhost");
        setIfNotExists("port", 3306);
        setIfNotExists("database", "db");
        setIfNotExists("user", "root");
        setIfNotExists("password", "root");
    }

    public ConnectionInfo getConnectionInfo() {
        String host = config.getString("host");
        int port = config.getInt("port");
        String database = config.getString("database");
        String user = config.getString("user");
        String password = config.getString("password");

        return new ConnectionInfo(host, port, database, user, password);
    }

}
