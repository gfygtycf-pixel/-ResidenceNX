package me.residencenx.listener;

import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;

import me.residencenx.Main;
import me.residencenx.model.Region;

public class RegionGuiListener implements Listener {

    private final String TITLE = "§6Region Menu";

    // =====================
    // ОТКРЫТИЕ МЕНЮ
    // =====================
    public void open(Player player, Region region) {

        Inventory inv = cn.nukkit.inventory.InventoryType.CHEST
                .getDefaultSize() > 0
                ? player.getServer().createInventory(player, 27, TITLE)
                : null;

        if (inv == null) return;

        // 🏠 HOME
        Item home = Item.get(Item.COMPASS);
        home.setCustomName("§aTeleport Home");

        // 👥 ADD
        Item add = Item.get(Item.EMERALD);
        add.setCustomName("§eAdd Member");

        // ❌ REMOVE
        Item remove = Item.get(Item.REDSTONE);
        remove.setCustomName("§cRemove Member");

        // ⚙ INFO
        Item info = Item.get(Item.PAPER);
        info.setCustomName("§bInfo");

        inv.setItem(11, home);
        inv.setItem(13, info);
        inv.setItem(15, add);
        inv.setItem(22, remove);

        player.addWindow(inv);
    }

    // =====================
    // КЛИКИ
    // =====================
    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();

        if (!event.getInventory().getTitle().equals(TITLE)) return;

        event.setCancelled(true);

        Region region = Main.getInstance()
                .getRegionManager()
                .getRegionAt(player.getLevel().getName(), player.getLocation());

        if (region == null) return;

        Item item = event.getCurrentItem();

        if (item == null || item.getId() == 0) return;

        String name = item.hasCustomName() ? item.getCustomName() : "";

        // 🏠 HOME
        if (name.contains("Teleport Home")) {
            player.teleport(region.getHome());
            player.sendMessage("§aТелепорт в регион");
        }

        // 👥 ADD (упрощённо)
        if (name.contains("Add Member")) {
            player.sendMessage("§eИспользуй: /rg add <player>");
        }

        // ❌ REMOVE
        if (name.contains("Remove Member")) {
            player.sendMessage("§eИспользуй: /rg remove <player>");
        }

        // ⚙ INFO
        if (name.contains("Info")) {
            player.sendMessage("§6=== REGION INFO ===");
            player.sendMessage("§eName: §f" + region.getName());
            player.sendMessage("§eOwner: §f" + region.getOwnerName());
            player.sendMessage("§eMembers: §f" + region.getMembers().size());
        }
    }
}