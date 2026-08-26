package me.eldoria.topstats.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import me.eldoria.topstats.Main;
import me.eldoria.topstats.PlayerStats;

import java.util.List;

public class TopKillsCommand extends Command {

    private final Main plugin;

    public TopKillsCommand(Main plugin) {

        super(
                "topkills",
                "Show top players by kills",
                "/topkills"
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
                        .getTopKills(10);

        sender.sendMessage("");
        sender.sendMessage(
                "§6§l⚔ ТОП УБИЙЦ"
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
                            " §7- §a" +
                            stats.getKills()
            );

            position++;
        }

        sender.sendMessage("");

        return true;
    }
}