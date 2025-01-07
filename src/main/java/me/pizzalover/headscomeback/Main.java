package me.pizzalover.headscomeback;

import me.pizzalover.headscomeback.db.database;
import me.pizzalover.headscomeback.events.BlockBreak;
import me.pizzalover.headscomeback.events.BlockPlace;
import me.pizzalover.headscomeback.utils.config.databaseConfig;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    private static database db = new database();

    /**
     * Get the instance of the plugin
     * @return the instance
     */
    public static Main getInstance() {
        return instance;
    }

    /**
     * Get the database
     * @return the database
     */
    public static database getDatabase() {
        return db;
    }


    @Override
    public void onEnable() {
        instance = this;

        if(!databaseConfig.configFile.exists()) {
            saveResource("database.yml", false);
        }

        databaseConfig.updateConfig();
        databaseConfig.saveConfig();
        databaseConfig.reloadConfig();

        if(!getDatabase().initializeDatabase()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new BlockBreak(), this);
        getServer().getPluginManager().registerEvents(new BlockPlace(), this);


        getLogger().info("HeadsComeBack Plugin enabled");
    }

    @Override
    public void onDisable() {

        getLogger().info("HeadsComeBack Plugin disabled");

    }
}
