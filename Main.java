package me.residencenx;

import cn.nukkit.plugin.PluginBase;

import me.residencenx.manager.RegionManager;
import me.residencenx.manager.SelectionManager;
import me.residencenx.storage.RegionStorage;

import me.residencenx.listener.PlayerListener;
import me.residencenx.listener.BlockListener;
import me.residencenx.listener.RegionSignListener;

import me.residencenx.command.RegionCommand;

import onebone.economyapi.EconomyAPI;

public class Main extends PluginBase {

    private static Main instance;

    private SelectionManager selectionManager;
    private RegionManager regionManager;
    private RegionStorage regionStorage;

    private EconomyAPI economy;

    // =====================
    // INSTANCE
    // =====================
    public static Main getInstance() {
        return instance;
    }

    // =====================
    // GETTERS
    // =====================
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }

    public RegionStorage getRegionStorage() {
        return regionStorage;
    }

    public EconomyAPI getEconomy() {
        return economy;
    }

    // =====================
    // ENABLE
    // =====================
    @Override
    public void onEnable() {

        instance = this;

        // managers
        this.selectionManager = new SelectionManager();
        this.regionManager = new RegionManager();

        // storage
        this.regionStorage = new RegionStorage();

        saveDefaultConfig();

        // load regions
        this.regionStorage.loadAll();

        // economy
        this.economy = EconomyAPI.getInstance();

        // listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new RegionSignListener(), this);

        // command
        this.getServer().getCommandMap().register(
                "rg",
                new RegionCommand()
        );

        getLogger().info("ResidenceNX enabled");
    }

    // =====================
    // DISABLE
    // =====================
    @Override
    public void onDisable() {

        if (regionStorage != null) {
            regionStorage.saveAll();
        }

        getLogger().info("ResidenceNX disabled");
    }
}