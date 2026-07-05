package me.residencenx.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import me.residencenx.Main;
import me.residencenx.model.Region;

public class BlockListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();

        if (player.hasPermission("residencenx.bypass")) {
            return;
        }

        Region region = Main.getInstance()
                .getRegionManager()
                .getRegionAt(
                        event.getBlock().getLevel().getName(),
                        event.getBlock().getLocation()
                );

        if (region == null) return;

        if (!region.isOwner(player.getUniqueId()) && !region.isMember(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage("§c[ResidenceNX] Ломать здесь нельзя (чужой регион)");
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {

        Player player = event.getPlayer();

        if (player.hasPermission("residencenx.bypass")) {
            return;
        }

        Region region = Main.getInstance()
                .getRegionManager()
                .getRegionAt(
                        event.getBlock().getLevel().getName(),
                        event.getBlock().getLocation()
                );

        if (region == null) return;

        if (!region.isOwner(player.getUniqueId()) && !region.isMember(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage("§c[ResidenceNX] Строить здесь нельзя (чужой регион)");
        }
    }
}