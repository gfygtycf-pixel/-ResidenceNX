package me.eldoria.topstats;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerDeathEvent;

public class KillListener implements Listener {

    private final Main plugin;

    public KillListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

        Player victim = event.getEntity();

        // Засчитываем смерть всегда
        plugin.getStatsManager()
                .registerDeath(victim.getName());

        // Проверяем причину последнего урона
        EntityDamageEvent damage =
                victim.getLastDamageCause();

        if (damage instanceof EntityDamageByEntityEvent) {

            EntityDamageByEntityEvent entityDamage =
                    (EntityDamageByEntityEvent) damage;

            // Если убил другой игрок — засчитываем убийство
            if (entityDamage.getDamager() instanceof Player) {

                Player killer =
                        (Player) entityDamage.getDamager();

                // Самоубийство не считается убийством
                if (!killer.getName().equalsIgnoreCase(
                        victim.getName()
                )) {

                    plugin.getStatsManager()
                            .registerKill(killer.getName());
                }
            }
        }

        // Обновляем голограммы после смерти
        plugin.getHologramManager()
                .updateAll();
    }
}