package me.pizzalover.headscomeback.db;

import me.pizzalover.headscomeback.Main;
import me.pizzalover.headscomeback.db.model.headModel;
import me.pizzalover.headscomeback.utils.config.databaseConfig;
import me.pizzalover.headscomeback.utils.utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.*;

public class database {

    private Connection connection;

    /**
     * Get a connection to a database
     * @return the connection
     * @throws SQLException
     */
    public Connection getConnection() {

        try {
            if (connection != null) {
                if(!connection.isClosed() && connection.isValid(500)) {
                    return connection;
                }
            }

            switch(databaseConfig.getConfig().getString("schemas.headscomeback-items.type")) {
                case "sqlite": {
                    if (Main.getInstance().getDataFolder().mkdirs()) {
                        Main.getInstance().getLogger().info("Created the plugin folder.");
                    }
                    String databasePath = Main.getInstance().getDataFolder().getAbsolutePath() + "/database.db"; // Path to your SQLite database file

                    // Try to connect to SQLite
                    String url = "jdbc:sqlite:" + databasePath;

                    Connection connection = DriverManager.getConnection(url);

                    this.connection = connection;

                    Main.getInstance().getLogger().info("Connected to SQLite database.");

                    return connection;
                }
                case "mysql": {
                    String schema = "schemas.headscomeback-items.";
                    if (
                            databaseConfig.getConfig().getString(schema + "username").equalsIgnoreCase("root")
                                    && databaseConfig.getConfig().getString(schema + "password").equalsIgnoreCase("password")
                                    && databaseConfig.getConfig().getString(schema + "host").equalsIgnoreCase("localhost")
                                    && databaseConfig.getConfig().getString(schema + "port").equalsIgnoreCase("3306")) {
                        Bukkit.getServer().getLogger().info("Please configure the schema 1 in the config.yml!");
                        return null;
                    }

                    //Try to connect to MySQL
                    String username = databaseConfig.getConfig().getString(schema + "username");
                    String password = databaseConfig.getConfig().getString(schema + "password");
                    String host = databaseConfig.getConfig().getString(schema + "host");
                    String port = databaseConfig.getConfig().getString(schema + "port");
                    String database = databaseConfig.getConfig().getString(schema + "database");
                    String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true";

                    Connection connection = DriverManager.getConnection(url, username, password);

                    this.connection = connection;

                    Bukkit.getServer().getLogger().info("Connected to MySQL database.");
                    return connection;
                }
            }

        } catch (SQLException e) {
            Bukkit.getServer().getLogger().info("Cannot connect to database, maybe the configuration is wrong");
            Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
        }
        return null;
    }

    /**
     * Initialize the database
     * @throws SQLException
     */
    public boolean initializeDatabase() {
        try {
            if(getConnection() == null) {
                Bukkit.getServer().getLogger().info("Cannot initialize database, maybe the configuration is wrong");
                return false;
            }
            Statement statement = getConnection().createStatement();

            //Create the player_stats table
            String sql = "CREATE TABLE IF NOT EXISTS items ( location varchar(35) primary key, itemstack text )";

            statement.execute(sql);

            statement.close();
        } catch (SQLException e) {
            Bukkit.getServer().getLogger().info("Cannot initialize database, maybe the configuration is wrong");
            return false;
        }
        return true;
    }


    /**
     * Create a new item in the database
     * @param headModel the item model
     * @throws SQLException
     */
    public void createInformation(headModel headModel) {

        try {
            PreparedStatement statement = getConnection()
                    .prepareStatement("INSERT INTO items(location, itemstack) VALUES (?, ?)");
            String location = "";
            location += headModel.getLocation().getBlockX() + ",";
            location += headModel.getLocation().getBlockY() + ",";
            location += headModel.getLocation().getBlockZ() + ",";
            location += headModel.getLocation().getWorld().getName();
            statement.setString(1, location);
            statement.setString(2, headModel.encodeItem(headModel.getItem()));

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().info("Cannot create item in the database");
            e.printStackTrace();
        }
    }

    /**
     * Update the item information
     * @param headModel the item model
     * @throws SQLException
     */
    public void updateInformation(headModel headModel) {

        try {
            PreparedStatement statement = getConnection().prepareStatement("UPDATE items SET itemstack = ? WHERE location = ?");
            statement.setString(1, headModel.encodeItem(headModel.getItem()));
            String location = "";
            location += headModel.getLocation().getBlockX() + ",";
            location += headModel.getLocation().getBlockY() + ",";
            location += headModel.getLocation().getBlockZ() + ",";
            location += headModel.getLocation().getWorld().getName();
            statement.setString(2, location);

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().info("Cannot update item in the database");
            e.printStackTrace();
        }
    }

    /**
     * Delete the item information
     * @param headModel the item model
     * @throws SQLException
     */
    public void deleteInformation(headModel headModel) {

        try {
            PreparedStatement statement = getConnection().prepareStatement("DELETE FROM items WHERE location = ?");
            String location = "";
            location += headModel.getLocation().getBlockX() + ",";
            location += headModel.getLocation().getBlockY() + ",";
            location += headModel.getLocation().getBlockZ() + ",";
            location += headModel.getLocation().getWorld().getName();
            statement.setString(1, location);

            statement.executeUpdate();

            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().info("Cannot delete item in the database");
            e.printStackTrace();
        }
    }

    /**
     * Find the item information by the item location
     * @param location the item location
     * @return the item model
     * @throws SQLException
     */
    public headModel findItemInformationByLocation(Location location) {

        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM items WHERE location = ?");
            String locationTXT = "";
            locationTXT += location.getBlockX() + ",";
            locationTXT += location.getBlockY() + ",";
            locationTXT += location.getBlockZ() + ",";
            locationTXT += location.getWorld().getName();
            statement.setString(1, locationTXT);

            ResultSet resultSet = statement.executeQuery();

            headModel headModel;

            if (resultSet.next()) {

                headModel = new headModel(
                        utils.decodeItem(resultSet.getString("itemstack")),
                        location
                );

                statement.close();

                return headModel;
            }

            statement.close();

        } catch (SQLException e) {
            Bukkit.getLogger().info("An error occurred while trying to find the item in the database");
            e.printStackTrace();
        }
        return null;
    }



}
