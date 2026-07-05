package me.residencenx.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.Player;
import cn.nukkit.math.Vector3;
import cn.nukkit.item.Item;

import me.residencenx.Main;
import me.residencenx.model.Cuboid;
import me.residencenx.model.Region;
import me.residencenx.model.Selection;

public class RegionCommand extends Command {

    public RegionCommand() {
        super("rg", "ResidenceNX command");
        this.setPermission("residencenx.command");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§e/rg wand");
            player.sendMessage("§e/rg create <name>");
            player.sendMessage("§e/rg info");
            player.sendMessage("§e/rg add <player>");
            player.sendMessage("§e/rg remove <player>");
            player.sendMessage("§e/rg flag <key> <true/false>");
            player.sendMessage("§e/rg home <region>");
            return true;
        }

        // =====================
        // WAND
        // =====================
        if (args[0].equalsIgnoreCase("wand")) {
            player.getInventory().addItem(Item.get(271));
            player.sendMessage("§aТы получил топор");
            return true;
        }

        // =====================
        // CREATE REGION
        // =====================
        if (args[0].equalsIgnoreCase("create")) {

            if (args.length < 2) {
                player.sendMessage("§c/rg create <name>");
                return true;
            }

            String name = args[1];

            Selection sel = Main.getInstance()
                    .getSelectionManager()
                    .get(player.getUniqueId());

            if (sel == null || !sel.isComplete()) {
                player.sendMessage("§cСначала выдели территорию");
                return true;
            }

            Vector3 pos1 = sel.getPos1();
            Vector3 pos2 = sel.getPos2();

            String world = player.getLevel().getName();

            Cuboid cuboid = new Cuboid(pos1, pos2);

            if (Main.getInstance().getRegionManager().overlaps(cuboid, world)) {
                player.sendMessage("§cТут уже есть регион");
                return true;
            }

            Region region = new Region(
                    name,
                    world,
                    player.getUniqueId(),
                    player.getName(),
                    cuboid
            );

            Main.getInstance().getRegionManager().addRegion(region);

            player.sendMessage("§aРегион создан: " + name);
            return true;
        }

        // =====================
        // INFO
        // =====================
        if (args[0].equalsIgnoreCase("info")) {

            Region region = Main.getInstance()
                    .getRegionManager()
                    .getRegionAt(player.getLevel().getName(), player.getLocation());

            if (region == null) {
                player.sendMessage("§cТы не в регионе");
                return true;
            }

            player.sendMessage("§6=== Регион ===");
            player.sendMessage("§eНазвание: §f" + region.getName());
            player.sendMessage("§eМир: §f" + region.getWorld());
            player.sendMessage("§eВладелец: §f" + region.getOwnerName());
            player.sendMessage("§eУчастников: §f" + region.getMembers().size());
            player.sendMessage("§eФлаги: §f" + region.getFlags());
            return true;
        }

        // =====================
        // ADD MEMBER
        // =====================
        if (args[0].equalsIgnoreCase("add")) {

            if (args.length < 2) {
                player.sendMessage("§c/rg add <player>");
                return true;
            }

            Region region = Main.getInstance()
                    .getRegionManager()
                    .getRegionAt(player.getLevel().getName(), player.getLocation());

            if (region == null) {
                player.sendMessage("§cТы не в регионе");
                return true;
            }

            if (!region.isOwner(player.getUniqueId())) {
                player.sendMessage("§cТолько владелец");
                return true;
            }

            Player target = player.getServer().getPlayer(args[1]);

            if (target == null) {
                player.sendMessage("§cИгрок не найден");
                return true;
            }

            region.addMember(target.getUniqueId());

            player.sendMessage("§aИгрок добавлен");
            return true;
        }

        // =====================
        // REMOVE MEMBER
        // =====================
        if (args[0].equalsIgnoreCase("remove")) {

            if (args.length < 2) {
                player.sendMessage("§c/rg remove <player>");
                return true;
            }

            Region region = Main.getInstance()
                    .getRegionManager()
                    .getRegionAt(player.getLevel().getName(), player.getLocation());

            if (region == null) {
                player.sendMessage("§cТы не в регионе");
                return true;
            }

            if (!region.isOwner(player.getUniqueId())) {
                player.sendMessage("§cТолько владелец");
                return true;
            }

            Player target = player.getServer().getPlayer(args[1]);

            if (target == null) {
                player.sendMessage("§cИгрок не найден");
                return true;
            }

            region.removeMember(target.getUniqueId());

            player.sendMessage("§cИгрок удалён");
            return true;
        }

        // =====================
        // FLAGS
        // =====================
        if (args[0].equalsIgnoreCase("flag")) {

            if (args.length < 3) {
                player.sendMessage("§c/rg flag <key> <true/false>");
                return true;
            }

            Region region = Main.getInstance()
                    .getRegionManager()
                    .getRegionAt(player.getLevel().getName(), player.getLocation());

            if (region == null) {
                player.sendMessage("§cТы не в регионе");
                return true;
            }

            if (!region.isOwner(player.getUniqueId())) {
                player.sendMessage("§cТолько владелец");
                return true;
            }

            String key = args[1];
            boolean value = Boolean.parseBoolean(args[2]);

            region.setFlag(key, value);

            player.sendMessage("§aФлаг " + key + " = " + value);
            return true;
        }

        // =====================
        // HOME
        // =====================
        if (args[0].equalsIgnoreCase("home")) {

            if (args.length < 2) {
                player.sendMessage("§c/rg home <region>");
                return true;
            }

            Region region = Main.getInstance()
                    .getRegionManager()
                    .getRegion(args[1]);

            if (region == null) {
                player.sendMessage("§cРегион не найден");
                return true;
            }

            if (!region.hasAccess(player.getUniqueId())) {
                player.sendMessage("§cНет доступа");
                return true;
            }

            player.teleport(region.getHome());

            player.sendMessage("§aТелепорт в регион " + region.getName());
            return true;
        }

        return true;
    }
}