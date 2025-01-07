package me.pizzalover.headscomeback.events;

import me.pizzalover.headscomeback.Main;
import me.pizzalover.headscomeback.db.model.headModel;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlace implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        if(event.getItemInHand().getType().equals(Material.PLAYER_HEAD) || event.getItemInHand().getType().equals(Material.PLAYER_WALL_HEAD)) {

            headModel head = Main.getDatabase().findItemInformationByLocation(event.getBlock().getLocation());
            if(head == null) {
                ItemStack itemStack = event.getItemInHand().clone();
                itemStack.setAmount(1);
                headModel headModel = new headModel(
                        itemStack,
                        event.getBlock().getLocation()
                );
                Main.getDatabase().createInformation(headModel);
            }

        }

    }

}
