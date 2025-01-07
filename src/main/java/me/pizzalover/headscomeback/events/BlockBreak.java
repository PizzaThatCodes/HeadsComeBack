package me.pizzalover.headscomeback.events;

import me.pizzalover.headscomeback.Main;
import me.pizzalover.headscomeback.db.model.headModel;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreak implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        if(event.getBlock().getType().equals(Material.PLAYER_HEAD) || event.getBlock().getType().equals(Material.PLAYER_WALL_HEAD)) {
            // TODO: Add code to drop the head by looking into the database
            headModel head = Main.getDatabase().findItemInformationByLocation(event.getBlock().getLocation());
            if(head != null) {

                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), head.getItem());
                Main.getDatabase().deleteInformation(head);


            }

        }


    }

}
