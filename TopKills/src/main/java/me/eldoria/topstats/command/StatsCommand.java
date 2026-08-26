package me.eldoria.topstats.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import me.eldoria.topstats.Main;
import me.eldoria.topstats.PlayerStats;

import java.util.Locale;

public class StatsCommand extends Command {

    private final Main plugin;

    public StatsCommand(Main plugin) {

        super(
                "stats",
                "Show player statistics",
                "/stats [player]"
        );

        this.plugin = plugin;

        setPermission("topstats.command");
    }

    @Override
    public boolean execute(
            CommandSender sender,
            String commandLabel,
            String[] args
    ) {

        String playerName;

        if (args.length > 0) {
            playerName = args[0];

        } else {

            if (!(sender instanceof Player)) {

                sender.sendMessage(
                        "§cFrom console use: /stats <player>"
                );

                return true;
            }

            playerName =
                    sender.getName();
        }

        PlayerStats stats =
                plugin.getStatsManager()
                        .getStats(playerName);

        sender.sendMessage("");
        sender.sendMessage(
                "§6§l⚔ Статистика игрока"
        );
        sender.sendMessage("");

        sender.sendMessage(
                "§7Игрок: §f" + stats.getName()
        );

        sender.sendMessage(
                "§7Убийств: §a" + stats.getKills()
        );

        sender.sendMessage(
                "§7Смертей: §c" + stats.getDeaths()
        );

        sender.sendMessage(
                "§7K/D: §e" +
                        String.format(
                                Locale.US,
                                "%.2f",
                                stats.getKd()
                        )
        );

        sender.sendMessage("");

        return true;
    }
}