package me.eldoria.rtp;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerMoveEvent;

public class RTPMoveListener implements Listener {

    private final RTPManager rtpManager;

    public RTPMoveListener(RTPManager rtpManager) {
        this.rtpManager = rtpManager;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        Player player =
                event.getPlayer();

        /*
         * Если игрок действительно
         * изменил координаты — отменяем RTP.
         */
        if (event.getFrom().distance(
                event.getTo()
        ) > 0.05) {

            rtpManager.cancelTeleport(player);
        }
    }
}