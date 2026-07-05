package me.residencenx.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.Player;
import cn.nukkit.math.Vector3;

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
            player.sendMessage("§e/rg wand, create <name>");
            return true;
        }

        // /rg wand
        if (args[0].equalsIgnoreCase("wand")) {
            player.getInventory().addItem(
                    cn.nukkit.item.Item.get(271)
            );
            player.sendMessage("§aТы получил топор для выделения");
            return true;
        }

        // /rg create <name>
        if (args[0].equalsIgnoreCase("create")) {

            if (args.length < 2) {
                player.sendMessage("§cИспользование: /rg create <name>");
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

            // проверка пересечений
            if (Main.getInstance().getRegionManager().overlaps(cuboid, world)) {
                player.sendMessage("§cТут уже есть регион");
                return true;
            }

            Region region = new Region(
                    name,
                    world,
                    player.getUniqueId(),
                    cuboid
            );

            Main.getInstance()
                    .getRegionManager()
                    .addRegion(region);

            player.sendMessage("§aРегион создан: " + name);

            return true;
        }

        return false;
    }
}