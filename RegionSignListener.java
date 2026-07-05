package me.residencenx.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.blockentity.BlockEntitySign;

import me.residencenx.Main;
import me.residencenx.model.Region;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegionSignListener implements Listener {

    // временное хранение продаж
    private final Map<String, Integer> prices = new HashMap<>();

    // ======================
    // СОЗДАНИЕ ТАБЛИЧКИ
    // ======================
    @EventHandler
    public void onSign(SignChangeEvent event) {

        Player player = event.getPlayer();

        if (!player.hasPermission("residencenx.create.sign")) return;

        String line0 = event.getLine(0);

        if (!"[RG]".equalsIgnoreCase(line0)) return;

        String regionName = event.getLine(1);
        int price;

        try {
            price = Integer.parseInt(event.getLine(2));
        } catch (Exception e) {
            player.sendMessage("§cЦена должна быть числом");
            return;
        }

        Region region = Main.getInstance()
                .getRegionManager()
                .getRegion(regionName);

        if (region == null) {
            player.sendMessage("§cРегион не найден");
            return;
        }

        if (!region.isOwner(player.getUniqueId())) {
            player.sendMessage("§cТы не владелец региона");
            return;
        }

        prices.put(regionName, price);

        event.setLine(0, "§6[Продажа]");
        event.setLine(1, regionName);
        event.setLine(2, "§a" + price + "$");

        player.sendMessage("§aРегион выставлен на продажу");
    }

    // ======================
    // ПОКУПКА
    // ======================
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (event.getBlock() == null) return;

        if (!(event.getBlock().getLevel().getBlockEntity(event.getBlock().getLocation()) instanceof BlockEntitySign)) return;

        BlockEntitySign sign = (BlockEntitySign) event.getBlock().getLevel()
                .getBlockEntity(event.getBlock().getLocation());

        String line0 = sign.getText()[0];

        if (!line0.contains("[Продажа]")) return;

        Player player = event.getPlayer();

        String regionName = sign.getText()[1].replace("§6", "").replace("§a", "");

        Region region = Main.getInstance()
                .getRegionManager()
                .getRegion(regionName);

        if (region == null) {
            player.sendMessage("§cРегион не найден");
            return;
        }

        int price = prices.getOrDefault(regionName, 0);

        // 💡 тут позже подключим EconomyAPI
        player.sendMessage("§eПокупка региона за " + price + "$ (экономика будет подключена позже)");

        region.addOwner(player.getUniqueId());

        player.sendMessage("§aТы купил регион!");
    }
}