package me.eldoria.topstats.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import me.eldoria.topstats.Main;
import me.eldoria.topstats.PlayerStats;

import java.util.List;

public class TopDeathsCommand extends Command {

    private final Main plugin;

    public TopDeathsCommand(Main plugin) {

        super(
                "topdeaths",
                "Show top players by deaths",
                "/topdeaths"
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
                        .getTopDeaths(10);

        sender.sendMessage("");
        sender.sendMessage(
                "§c§l☠ ТОП СМЕРТЕЙ"
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
                            " §7- §c" +
                            stats.getDeaths()
            );

            position++;
        }

        sender.sendMessage("");

        return true;
    }
}