package me.eldoria.topstats;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDeathEvent;

public class KillListener implements Listener {

    private final Main plugin;

    public KillListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player victim =
                (Player) event.getEntity();

        plugin.getStatsManager()
                .registerDeath(
                        victim.getName()
                );

        EntityDamageEvent damage =
                victim.getLastDamageCause();

        if (!(damage instanceof EntityDamageByEntityEvent)) {
            return;
        }

        EntityDamageByEntityEvent entityDamage =
                (EntityDamageByEntityEvent) damage;

        if (!(entityDamage.getDamager() instanceof Player)) {
            return;
        }

        Player killer =
                (Player) entityDamage.getDamager();

        if (killer.getName().equalsIgnoreCase(
                victim.getName()
        )) {
            return;
        }

        plugin.getStatsManager()
                .registerKill(
                        killer.getName()
                );

        plugin.getHologramManager()
                .updateAll();
    }
}