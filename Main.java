package me.residencenx;

import cn.nukkit.plugin.PluginBase;

import me.residencenx.manager.RegionManager;
import me.residencenx.manager.SelectionManager;

import me.residencenx.listener.PlayerListener;
import me.residencenx.listener.BlockListener;

import me.residencenx.command.RegionCommand;

import me.residencenx.storage.RegionStorage;

public class Main extends PluginBase {

    private static Main instance;

    private SelectionManager selectionManager;
    private RegionManager regionManager;

    public static Main getInstance() {
        return instance;
    }

    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }

    @Override
    public void onEnable() {

        instance = this;

        // менеджеры
        this.selectionManager = new SelectionManager();
        this.regionManager = new RegionManager();

        // конфиг
        saveDefaultConfig();

        // слушатели
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);

        // команда /rg
        this.getServer().getCommandMap().register(
                "rg",
                new RegionCommand()
        );

        getLogger().info("ResidenceNX enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("ResidenceNX disabled");
    }
}