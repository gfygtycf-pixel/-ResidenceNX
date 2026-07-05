package me.residencenx;

import cn.nukkit.plugin.PluginBase;

import me.residencenx.manager.RegionManager;
import me.residencenx.manager.SelectionManager;

import me.residencenx.storage.RegionStorage;

import me.residencenx.listener.PlayerListener;
import me.residencenx.listener.BlockListener;
import me.residencenx.listener.RegionSignListener;

import me.residencenx.command.RegionCommand;

public class Main extends PluginBase {

    private static Main instance;

    private SelectionManager selectionManager;
    private RegionManager regionManager;
    private RegionStorage regionStorage;

    public static Main getInstance() {
        return instance;
    }

    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }

    public RegionStorage getRegionStorage() {
        return regionStorage;
    }

    @Override
    public void onEnable() {

        instance = this;

        // менеджеры
        this.selectionManager = new SelectionManager();
        this.regionManager = new RegionManager();

        // storage (regions.yml)
        this.regionStorage = new RegionStorage();

        saveDefaultConfig();

        // загрузка регионов при старте
        this.regionStorage.loadAll();

        // listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new RegionSignListener(), this);

        // команда /rg
        this.getServer().getCommandMap().register(
                "rg",
                new RegionCommand()
        );

        getLogger().info("ResidenceNX enabled");
    }

    @Override
    public void onDisable() {

        // сохранение регионов
        if (regionStorage != null) {
            regionStorage.saveAll();
        }

        getLogger().info("ResidenceNX disabled");
    }
}