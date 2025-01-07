package me.pizzalover.headscomeback.db.model;

import jakarta.xml.bind.DatatypeConverter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.nio.charset.StandardCharsets;

public class headModel {

    private ItemStack item;
    private Location location;

    public headModel(ItemStack item, Location location) {
        this.item = item;
        this.location = location;
    }

    /**
     * Get the item
     * @return the item
     */
    public ItemStack getItem() {
        return item;
    }

    /**
     * Get the location
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Set the item
     * @param item the item
     */
    public void setItem(ItemStack item) {
        this.item = item;
    }

    /**
     * Set the location
     * @param location the location
     */
    public void setLocation(Location location) {
        this.location = location;
    }


    /**
     * Encode an item to base64
     * @param itemStack the itemStack
     * @return the base64 string
     */
    public String encodeItem(ItemStack itemStack) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("i", itemStack);
        return DatatypeConverter.printBase64Binary(config.saveToString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decode an item from base64
     * @param string base64 string
     * @return the itemStack
     */
    public ItemStack decodeItem(String string) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(new String(DatatypeConverter.parseBase64Binary(string), StandardCharsets.UTF_8));
        } catch (IllegalArgumentException | InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }
        return config.getItemStack("i", null);
    }


}
