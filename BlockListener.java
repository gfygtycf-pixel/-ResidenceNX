package me.residencenx.listener;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;

import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.BlockIgniteEvent;
import cn.nukkit.event.block.BlockBurnEvent;

import cn.nukkit.event.player.PlayerInteractEvent;

import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;

import me.residencenx.Main;
import me.residencenx.model.Region;

public class BlockListener implements Listener {

    // =========================
    // GET REGION HELPER
    // =========================
    private Region getRegion(Player player) {
        return Main.getInstance()
                .getRegionManager()
                .getRegionAt(player.getLevel().getName(), player.getLocation());
    }

    // =========================
    // BLOCK BREAK
    // =========================
    @EventHandler
    public void onBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();
        Region region = getRegion(player);

        if (region == null) return;

        if (region.hasAccess(player.getUniqueId())) return;

        if (!region.getFlag("destroy")) {
            event.setCancelled(true);
            player.sendMessage("§cТы не можешь ломать блоки здесь");
        }
    }

    // =========================
    // BLOCK PLACE
    // =========================
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {

        Player player = event.getPlayer();
        Region region = getRegion(player);

        if (region == null) return;

        if (region.hasAccess(player.getUniqueId())) return;

        if (!region.getFlag("build")) {
            event.setCancelled(true);
            player.sendMessage("§cТы не можешь ставить блоки здесь");
        }
    }

    // =========================
    // INTERACT (DOORS, BUTTONS, CHESTS)
    // =========================
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Region region = getRegion(player);

        if (region == null) return;

        if (region.hasAccess(player.getUniqueId())) return;

        Block block = event.getBlock();
        if (block == null) return;

        int id = block.getId();

        boolean isContainer =
                id == Block.CHEST ||
                id == Block.TRAPPED_CHEST ||
                id == Block.FURNACE ||
                id == Block.HOPPER ||
                id == Block.DISPENSER ||
                id == Block.DROPPER;

        boolean isInteract =
                id == Block.DOOR_BLOCK ||
                id == Block.ACACIA_DOOR_BLOCK ||
                id == Block.BIRCH_DOOR_BLOCK ||
                id == Block.JUNGLE_DOOR_BLOCK ||
                id == Block.SPRUCE_DOOR_BLOCK ||
                id == Block.WOODEN_BUTTON ||
                id == Block.STONE_BUTTON ||
                id == Block.LEVER;

        // контейнеры
        if (isContainer && !region.getFlag("container")) {
            event.setCancelled(true);
            player.sendMessage("§cСундуки запрещены в этом регионе");
            return;
        }

        // use (двери/рычаги/кнопки)
        if (isInteract && !region.getFlag("use")) {
            event.setCancelled(true);
            player.sendMessage("§cИспользование блоков запрещено здесь");
        }
    }

    // =========================
    // PVP
    // =========================
    @EventHandler
    public void onPvP(EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;

        Player damager = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

        Region region = Main.getInstance()
                .getRegionManager()
                .getRegionAt(victim.getLevel().getName(), victim.getLocation());

        if (region == null) return;

        if (!region.getFlag("pvp")) {
            event.setCancelled(true);
            damager.sendMessage("§cPvP запрещено в этом регионе");
        }
    }

    // =========================
    // EXPLOSIONS (TNT / CREEPER)
    // =========================
    @EventHandler
    public void onExplode(EntityExplodeEvent event) {

        if (event.getPosition() == null) return;

        Region region = Main.getInstance()
                .getRegionManager()
                .getRegionAt(
                        event.getPosition().getLevel().getName(),
                        event.getPosition()
                );

        if (region == null) return;

        if (!region.getFlag("destroy")) {
            event.setCancelled(true);
        }
    }

    // =========================
    // FIRE (IGNITE / BURN)
    // =========================
    @EventHandler
    public void onIgnite(BlockIgniteEvent event) {

        Region region = Main.getInstance()
                .getRegionManager()
                .getRegionAt(
                        event.getBlock().getLevel().getName(),
                        event.getBlock().getLocation()
                );

        if (region == null) return;

        if (!region.getFlag("destroy")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBurn(BlockBurnEvent event) {

        Region region = Main.getInstance()
                .getRegionManager()
                .getRegionAt(
                        event.getBlock().getLevel().getName(),
                        event.getBlock().getLocation()
                );

        if (region == null) return;

        if (!region.getFlag("destroy")) {
            event.setCancelled(true);
        }
    }
}