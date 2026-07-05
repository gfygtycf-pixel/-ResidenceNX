package me.residencenx;

import cn.nukkit.plugin.PluginBase;
import me.residencenx.manager.SelectionManager;
import me.residencenx.listener.PlayerListener;

public class Main extends PluginBase {

    private static Main instance;

    private SelectionManager selectionManager;

    public static Main getInstance() {
        return instance;
    }

    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    @Override
    public void onEnable() {

        instance = this;

        this.selectionManager = new SelectionManager();

        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        getLogger().info("ResidenceNX enabled");
    }
}