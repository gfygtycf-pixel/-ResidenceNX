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

        // админ обход
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

        // доступ
        if (!region.hasAccess(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage("§c[ResidenceNX] Это чужой регион");
            return;
        }

        // флаг destroy
        if (!region.getFlag("destroy")) {
            event.setCancelled(true);
            player.sendMessage("§c[ResidenceNX] Ломание запрещено в этом регионе");
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {

        Player player = event.getPlayer();

        // админ обход
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

        // доступ
        if (!region.hasAccess(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage("§c[ResidenceNX] Это чужой регион");
            return;
        }

        // флаг build
        if (!region.getFlag("build")) {
            event.setCancelled(true);
            player.sendMessage("§c[ResidenceNX] Строительство запрещено в этом регионе");
        }
    }
}