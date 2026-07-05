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

public class RegionSignListener implements Listener {

    // 💰 временное хранение цен (позже можно вынести в storage)
    private final Map<String, Integer> prices = new HashMap<>();

    // =========================
    // СОЗДАНИЕ ТАБЛИЧКИ
    // =========================
    @EventHandler
    public void onSignChange(SignChangeEvent event) {

        Player player = event.getPlayer();

        if (!player.hasPermission("residencenx.create.sign")) return;

        if (!"[RG]".equalsIgnoreCase(event.getLine(0))) return;

        String regionName = event.getLine(1);
        String priceLine = event.getLine(2);

        int price;

        try {
            price = Integer.parseInt(priceLine);
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

        prices.put(regionName.toLowerCase(), price);

        event.setLine(0, "§6[Продажа]");
        event.setLine(1, regionName);
        event.setLine(2, "§a" + price + "$");

        player.sendMessage("§aРегион выставлен на продажу");
    }

    // =========================
    // ПОКУПКА РЕГИОНА
    // =========================
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (event.getBlock() == null) return;

        if (!(event.getBlock().getLevel().getBlockEntity(event.getBlock().getLocation()) instanceof BlockEntitySign)) {
            return;
        }

        BlockEntitySign sign = (BlockEntitySign) event.getBlock().getLevel()
                .getBlockEntity(event.getBlock().getLocation());

        String[] lines = sign.getText();

        if (lines == null || lines.length == 0) return;

        if (!lines[0].contains("[Продажа]")) return;

        Player player = event.getPlayer();

        String regionName = lines[1].replace("§6", "").replace("§a", "").toLowerCase();

        Region region = Main.getInstance()
                .getRegionManager()
                .getRegion(regionName);

        if (region == null) {
            player.sendMessage("§cРегион не найден");
            return;
        }

        int price = prices.getOrDefault(regionName, 0);

        double money = Main.getInstance().getEconomy().myMoney(player);

        if (money < price) {
            player.sendMessage("§cНедостаточно денег");
            return;
        }

        // 💰 списание денег у покупателя
        Main.getInstance().getEconomy().reduceMoney(player, price);

        // 💰 выдача денег продавцу (по имени, безопасно)
        String ownerName = region.getOwnerName();

        Main.getInstance().getEconomy().addMoney(ownerName, price);

        // 👑 передача региона новому владельцу
        region.addOwner(player.getUniqueId());

        player.sendMessage("§aТы купил регион за " + price + "$");
    }
}