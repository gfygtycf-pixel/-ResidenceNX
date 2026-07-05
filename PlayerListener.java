package me.residencenx.listener;

import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.Player;
import me.residencenx.Main;

public class PlayerListener implements Listener {

    @cn.nukkit.event.EventHandler
    public void onInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Item item = event.getItem();

        // деревянный топор (ID берем из config позже)
        if (item.getId() != 271) return;

        if (!player.hasPermission("residencenx.command")) return;

        Vector3 pos = event.getBlock().getLocation();

        if (event.getAction().name().contains("LEFT_CLICK_BLOCK")) {
            Main.getInstance().getSelectionManager().setPos1(player.getUniqueId(), pos);
            player.sendMessage("§e[ResidenceNX] §fПервая точка установлена");
        }

        if (event.getAction().name().contains("RIGHT_CLICK_BLOCK")) {
            Main.getInstance().getSelectionManager().setPos2(player.getUniqueId(), pos);
            player.sendMessage("§e[ResidenceNX] §fВторая точка установлена");
        }
    }
}