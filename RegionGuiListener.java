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

    private final String TITLE_MAIN = "§6Region Menu";
    private final String TITLE_FLAGS = "§eRegion Flags";
    private final String TITLE_MEMBERS = "§bRegion Members";

    // =========================
    // OPEN MAIN MENU
    // =========================
    public void open(Player player, Region region) {

        Inventory inv = player.getServer().createInventory(player, 27, TITLE_MAIN);

        Item home = Item.get(Item.COMPASS);
        home.setCustomName("§aTeleport Home");

        Item flags = Item.get(Item.REDSTONE);
        flags.setCustomName("§eEdit Flags");

        Item members = Item.get(Item.SKULL);
        members.setCustomName("§bManage Members");

        Item info = Item.get(Item.PAPER);
        info.setCustomName("§fRegion Info");

        inv.setItem(11, home);
        inv.setItem(13, flags);
        inv.setItem(15, members);
        inv.setItem(22, info);

        player.addWindow(inv);
    }

    // =========================
    // FLAGS MENU
    // =========================
    private void openFlags(Player player, Region region) {

        Inventory inv = player.getServer().createInventory(player, 27, TITLE_FLAGS);

        inv.setItem(10, makeFlag("build", region));
        inv.setItem(11, makeFlag("destroy", region));
        inv.setItem(12, makeFlag("pvp", region));
        inv.setItem(13, makeFlag("use", region));
        inv.setItem(14, makeFlag("container", region));

        player.addWindow(inv);
    }

    private Item makeFlag(String key, Region region) {

        boolean value = region.getFlag(key);

        Item item = Item.get(Item.STAINED_GLASS_PANE, value ? 5 : 14);
        item.setCustomName("§e" + key + " §7: " + (value ? "§aON" : "§cOFF"));

        return item;
    }

    // =========================
    // MEMBERS MENU
    // =========================
    private void openMembers(Player player, Region region) {

        Inventory inv = player.getServer().createInventory(player, 27, TITLE_MEMBERS);

        Item add = Item.get(Item.EMERALD);
        add.setCustomName("§aAdd Member");

        Item remove = Item.get(Item.REDSTONE);
        remove.setCustomName("§cRemove Member");

        inv.setItem(11, add);
        inv.setItem(15, remove);

        player.addWindow(inv);
    }

    // =========================
    // CLICK HANDLER
    // =========================
    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();

        String title = event.getInventory().getTitle();
        Item item = event.getCurrentItem();

        if (item == null || item.getId() == 0) return;

        Region region = Main.getInstance()
                .getRegionManager()
                .getRegionAt(player.getLevel().getName(), player.getLocation());

        if (region == null) return;

        event.setCancelled(true);

        String name = item.hasCustomName() ? item.getCustomName() : "";

        // ================= MAIN =================
        if (title.equals(TITLE_MAIN)) {

            if (name.contains("Teleport Home")) {
                player.teleport(region.getHome());
            }

            if (name.contains("Edit Flags")) {
                openFlags(player, region);
            }

            if (name.contains("Manage Members")) {
                openMembers(player, region);
            }

            if (name.contains("Region Info")) {
                player.sendMessage("§6Region: §f" + region.getName());
                player.sendMessage("§6Owner: §f" + region.getOwnerName());
                player.sendMessage("§6Members: §f" + region.getMembers().size());
            }
        }

        // ================= FLAGS =================
        if (title.equals(TITLE_FLAGS)) {

            if (!region.isOwner(player.getUniqueId())) return;

            String key = name.replace("§e", "").split(" ")[0];

            boolean current = region.getFlag(key);
            region.setFlag(key, !current);

            player.sendMessage("§aFlag " + key + " = " + !current);

            openFlags(player, region);
        }

        // ================= MEMBERS =================
        if (title.equals(TITLE_MEMBERS)) {

            if (!region.isOwner(player.getUniqueId())) return;

            if (name.contains("Add")) {
                player.sendMessage("§eИспользуй: /rg add <player>");
            }

            if (name.contains("Remove")) {
                player.sendMessage("§eИспользуй: /rg remove <player>");
            }
        }
    }
}