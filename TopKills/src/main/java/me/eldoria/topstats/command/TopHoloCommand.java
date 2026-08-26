package me.eldoria.topstats.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import me.eldoria.topstats.HologramManager;
import me.eldoria.topstats.Main;

public class TopHoloCommand extends Command {

    private final Main plugin;

    public TopHoloCommand(Main plugin) {

        super(
                "topholo",
                "Manage TopStats holograms",
                "/topholo <create|remove|reload> <kills|deaths|kd>"
        );

        this.plugin = plugin;

        setPermission("topstats.admin");
    }

    @Override
    public boolean execute(
            CommandSender sender,
            String commandLabel,
            String[] args
    ) {

        if (args.length < 1) {

            sendHelp(sender);

            return true;
        }

        String action =
                args[0].toLowerCase();

        if (action.equals("reload")) {

            plugin.getHologramManager()
                    .reload();

            sender.sendMessage(
                    "§aГолограммы перезагружены."
            );

            return true;
        }

        if (args.length < 2) {

            sendHelp(sender);

            return true;
        }

        String type =
                args[1].toLowerCase();

        if (!isValidType(type)) {

            sender.sendMessage(
                    "§cДоступные типы: kills, deaths, kd"
            );

            return true;
        }

        HologramManager manager =
                plugin.getHologramManager();

        if (action.equals("create")) {

            if (!(sender instanceof Player)) {

                sender.sendMessage(
                        "§cЭта команда доступна только игроку."
                );

                return true;
            }

            Player player =
                    (Player) sender;

            manager.create(
                    type,
                    player
            );

            sender.sendMessage(
                    "§aГолограмма §f" +
                            type +
                            " §aсоздана."
            );

            return true;
        }

        if (action.equals("remove")) {

            manager.remove(type);

            sender.sendMessage(
                    "§aГолограмма §f" +
                            type +
                            " §aудалена."
            );

            return true;
        }

        sendHelp(sender);

        return true;
    }

    private boolean isValidType(String type) {

        return type.equals("kills")
                || type.equals("deaths")
                || type.equals("kd");
    }

    private void sendHelp(CommandSender sender) {

        sender.sendMessage("");
        sender.sendMessage(
                "§6§lTopStats Holograms"
        );
        sender.sendMessage("");
        sender.sendMessage(
                "§e/topholo create kills"
        );
        sender.sendMessage(
                "§e/topholo create deaths"
        );
        sender.sendMessage(
                "§e/topholo create kd"
        );
        sender.sendMessage(
                "§e/topholo remove kills"
        );
        sender.sendMessage(
                "§e/topholo remove deaths"
        );
        sender.sendMessage(
                "§e/topholo remove kd"
        );
        sender.sendMessage(
                "§e/topholo reload"
        );
        sender.sendMessage("");
    }
}