package me.eldoria.rtp;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class RTPCommand extends Command {

    private final Main plugin;

    public RTPCommand(Main plugin) {

        super(
                "rtp",
                "Randomly teleport to a safe location",
                "/rtp [reload]"
        );

        this.plugin = plugin;

        setPermission("rtp.use");
    }

    @Override
    public boolean execute(
            CommandSender sender,
            String commandLabel,
            String[] args
    ) {

        /*
         * /rtp reload
         */
        if (args.length > 0 &&
                args[0].equalsIgnoreCase("reload")) {

            if (!sender.hasPermission("rtp.admin")) {
                sender.sendMessage(
                        plugin.getConfig()
                                .getString("messages.no-permission")
                );

                return true;
            }

            plugin.reloadConfig();

            sender.sendMessage(
                    plugin.getConfig()
                            .getString("messages.reloaded")
            );

            return true;
        }

        /*
         * Только игрок
         */
        if (!(sender instanceof Player)) {

            sender.sendMessage(
                    plugin.getConfig()
                            .getString("messages.player-only")
            );

            return true;
        }

        Player player = (Player) sender;

        plugin.getRtpManager().startTeleport(player);

        return true;
    }
}