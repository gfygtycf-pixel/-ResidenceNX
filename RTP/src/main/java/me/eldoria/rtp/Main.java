package me.eldoria.rtp;

import cn.nukkit.plugin.PluginBase;

public class Main extends PluginBase {

    private RTPManager rtpManager;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        rtpManager = new RTPManager(this);

        getServer()
                .getPluginManager()
                .registerEvents(
                        new RTPMoveListener(rtpManager),
                        this
                );

        getServer()
                .getCommandMap()
                .register(
                        "rtp",
                        new RTPCommand(this)
                );

        getLogger().info("==============================");
        getLogger().info(" RTP 1.0.0 enabled!");
        getLogger().info(" Random teleportation enabled.");
        getLogger().info("==============================");
    }

    @Override
    public void onDisable() {

        if (rtpManager != null) {
            rtpManager.cancelAll();
        }

        getLogger().info("RTP disabled.");
    }

    public RTPManager getRtpManager() {
        return rtpManager;
    }
}