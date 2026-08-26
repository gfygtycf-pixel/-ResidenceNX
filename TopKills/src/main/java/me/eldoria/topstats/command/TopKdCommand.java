package me.eldoria.topstats.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import me.eldoria.topstats.Main;
import me.eldoria.topstats.PlayerStats;

import java.util.List;
import java.util.Locale;

public class TopKdCommand extends Command {

    private final Main plugin;

    public TopKdCommand(Main plugin) {

        super(
                "topkd",
                "Show top players by K/D",
                "/topkd"
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

        List<PlayerStats> top =
                plugin.getStatsManager()
                        .getTopKd(10);

        sender.sendMessage("");
        sender.sendMessage(
                "§e§l🏆 ТОП K/D"
        );
        sender.sendMessage("");

        if (top.isEmpty()) {

            sender.sendMessage(
                    "§7Статистика пока отсутствует."
            );

            return true;
        }

        int position = 1;

        for (PlayerStats stats : top) {

            sender.sendMessage(
                    "§e#" + position +
                            " §f" + stats.getName() +
                            " §7- §6" +
                            String.format(
                                    Locale.US,
                                    "%.2f",
                                    stats.getKd()
                            )
            );

            position++;
        }

        sender.sendMessage("");

        return true;
    }
}