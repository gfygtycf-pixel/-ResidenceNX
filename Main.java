package me.residencenx;

import cn.nukkit.plugin.PluginBase;
import me.residencenx.manager.RegionManager;
import me.residencenx.manager.SelectionManager;
import me.residencenx.listener.PlayerListener;

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

        this.selectionManager = new SelectionManager();
        this.regionManager = new RegionManager();

        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        getLogger().info("ResidenceNX enabled");
    }
}