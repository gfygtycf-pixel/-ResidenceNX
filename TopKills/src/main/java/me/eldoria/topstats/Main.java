package me.eldoria.topstats;

import cn.nukkit.plugin.PluginBase;
import me.eldoria.topstats.command.StatsCommand;
import me.eldoria.topstats.command.TopDeathsCommand;
import me.eldoria.topstats.command.TopHoloCommand;
import me.eldoria.topstats.command.TopKdCommand;
import me.eldoria.topstats.command.TopKillsCommand;

public class Main extends PluginBase {

    private StatsManager statsManager;
    private HologramManager hologramManager;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        statsManager =
                new StatsManager(this);

        statsManager.load();

        hologramManager =
                new HologramManager(this);

        getServer()
                .getPluginManager()
                .registerEvents(
                        new KillListener(this),
                        this
                );

        getServer()
                .getCommandMap()
                .register(
                        "stats",
                        new StatsCommand(this)
                );

        getServer()
                .getCommandMap()
                .register(
                        "topkills",
                        new TopKillsCommand(this)
                );

        getServer()
                .getCommandMap()
                .register(
                        "topdeaths",
                        new TopDeathsCommand(this)
                );

        getServer()
                .getCommandMap()
                .register(
                        "topkd",
                        new TopKdCommand(this)
                );

        getServer()
                .getCommandMap()
                .register(
                        "topholo",
                        new TopHoloCommand(this)
                );

        getServer()
                .getScheduler()
                .scheduleRepeatingTask(
                        this,
                        () -> hologramManager.updateAll(),
                        20 * 30
                );

        getLogger().info(
                "=============================="
        );

        getLogger().info(
                " TopStats 1.0.0 enabled!"
        );

        getLogger().info(
                " Kill/death statistics enabled."
        );

        getLogger().info(
                " Hologram system enabled."
        );

        getLogger().info(
                "=============================="
        );
    }

    @Override
    public void onDisable() {

        if (statsManager != null) {
            statsManager.save();
        }

        if (hologramManager != null) {
            hologramManager.removeAll();
        }

        getLogger().info(
                "TopStats disabled."
        );
    }

    public StatsManager getStatsManager() {
        return statsManager;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }
}