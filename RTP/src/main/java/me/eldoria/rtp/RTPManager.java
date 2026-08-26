package me.eldoria.rtp;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class RTPManager {

    private final Main plugin;
    private final Random random = new Random();

    /*
     * Игроки, у которых сейчас идёт подготовка RTP.
     */
    private final Set<UUID> teleporting = new HashSet<>();

    /*
     * Время последнего RTP.
     */
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    /*
     * Текущая задержка перед телепортацией.
     */
    private final Map<UUID, Integer> countdownTasks = new HashMap<>();

    public RTPManager(Main plugin) {
        this.plugin = plugin;
    }

    public void startTeleport(Player player) {

        UUID uuid = player.getUniqueId();

        /*
         * Уже телепортируется.
         */
        if (teleporting.contains(uuid)) {

            player.sendMessage(
                    getMessage("messages.already-teleporting")
            );

            return;
        }

        /*
         * Проверяем мир.
         */
        if (!isWorldAllowed(player.getLevel())) {

            player.sendMessage(
                    getMessage("messages.world-disabled")
            );

            return;
        }

        /*
         * Проверяем cooldown.
         */
        long cooldown = plugin.getConfig()
                .getLong("rtp.cooldown", 60);

        long now = System.currentTimeMillis();

        if (cooldowns.containsKey(uuid)) {

            long last = cooldowns.get(uuid);

            long passed =
                    (now - last) / 1000L;

            if (passed < cooldown) {

                long remaining =
                        cooldown - passed;

                player.sendMessage(
                        getMessage(
                                "messages.cooldown"
                        ).replace(
                                "{seconds}",
                                String.valueOf(remaining)
                        )
                );

                return;
            }
        }

        teleporting.add(uuid);

        player.sendMessage(
                getMessage("messages.searching")
        );

        /*
         * Ищем безопасную точку.
         */
        Location location =
                findSafeLocation(player.getLevel());

        if (location == null) {

            teleporting.remove(uuid);

            player.sendMessage(
                    getMessage("messages.no-location")
            );

            return;
        }

        int delay = plugin.getConfig()
                .getInt("rtp.teleport-delay", 5);

        /*
         * Мгновенная телепортация,
         * если задержка выключена.
         */
        if (delay <= 0) {

            teleport(player, location);

            return;
        }

        /*
         * Обратный отсчёт.
         */
        final int[] seconds = {delay};

        int taskId = plugin.getServer()
                .getScheduler()
                .scheduleRepeatingTask(
                        plugin,
                        () -> {

                            if (!player.isOnline()) {

                                cancelTeleport(player);
                                return;
                            }

                            if (!teleporting.contains(uuid)) {
                                return;
                            }

                            if (seconds[0] <= 0) {

                                teleport(
                                        player,
                                        location
                                );

                                return;
                            }

                            player.sendMessage(
                                    getMessage(
                                            "messages.countdown"
                                    ).replace(
                                            "{seconds}",
                                            String.valueOf(
                                                    seconds[0]
                                            )
                                    )
                            );

                            seconds[0]--;
                        },
                        20
                ).getTaskId();

        countdownTasks.put(uuid, taskId);
    }

    private void teleport(
            Player player,
            Location location
    ) {

        UUID uuid = player.getUniqueId();

        /*
         * Удаляем старую задачу.
         */
        cancelTask(uuid);

        teleporting.remove(uuid);

        cooldowns.put(
                uuid,
                System.currentTimeMillis()
        );

        /*
         * Сам телепорт.
         */
        player.teleport(
                location,
                PlayerTeleportEvent.TeleportCause.PLUGIN
        );

        player.sendMessage(
                getMessage("messages.success")
        );
    }

    public void cancelTeleport(Player player) {

        UUID uuid = player.getUniqueId();

        if (!teleporting.contains(uuid)) {
            return;
        }

        teleporting.remove(uuid);

        cancelTask(uuid);

        player.sendMessage(
                getMessage("messages.cancelled")
        );
    }

    private void cancelTask(UUID uuid) {

        Integer taskId =
                countdownTasks.remove(uuid);

        if (taskId != null) {

            plugin.getServer()
                    .getScheduler()
                    .cancelTask(taskId);
        }
    }

    public void cancelAll() {

        for (Integer taskId :
                countdownTasks.values()) {

            plugin.getServer()
                    .getScheduler()
                    .cancelTask(taskId);
        }

        countdownTasks.clear();
        teleporting.clear();
    }

    private Location findSafeLocation(Level level) {

        int minRadius = plugin.getConfig()
                .getInt("rtp.min-radius", 500);

        int maxRadius = plugin.getConfig()
                .getInt("rtp.max-radius", 5000);

        int attempts = plugin.getConfig()
                .getInt("rtp.attempts", 20);

        /*
         * Координаты центра мира —
         * спавн уровня.
         */
        double centerX =
                level.getSafeSpawn().getX();

        double centerZ =
                level.getSafeSpawn().getZ();

        for (int attempt = 0;
             attempt < attempts;
             attempt++) {

            /*
             * Случайный радиус.
             */
            double radius =
                    minRadius +
                    random.nextDouble() *
                    (maxRadius - minRadius);

            /*
             * Случайный угол.
             */
            double angle =
                    random.nextDouble()
                    * Math.PI * 2;

            int x =
                    (int) Math.round(
                            centerX +
                            Math.cos(angle) * radius
                    );

            int z =
                    (int) Math.round(
                            centerZ +
                            Math.sin(angle) * radius
                    );

            /*
             * Проверяем, загружен ли чанк.
             */
            int chunkX = x >> 4;
            int chunkZ = z >> 4;

            if (!level.isChunkGenerated(
                    chunkX,
                    chunkZ
            )) {

                continue;
            }

            int y =
                    level.getHighestBlockAt(
                            x,
                            z
                    );

            /*
             * Защита от некорректной высоты.
             */
            if (y <= 0 || y >= 319) {
                continue;
            }

            /*
             * Три блока:
             *
             * y     — ноги
             * y + 1 — голова
             * y - 1 — земля
             */
            Block ground =
                    level.getBlock(
                            x,
                            y,
                            z
                    );

            Block feet =
                    level.getBlock(
                            x,
                            y + 1,
                            z
                    );

            Block head =
                    level.getBlock(
                            x,
                            y + 2,
                            z
                    );

            /*
             * Поверхность должна быть твёрдой.
             */
            if (!ground.isSolid()) {
                continue;
            }

            /*
             * Игроку должно быть куда встать.
             */
            if (!feet.isAir()) {
                continue;
            }

            if (!head.isAir()) {
                continue;
            }

            /*
             * Нельзя телепортироваться
             * на жидкости.
             */
            if (ground.isLiquid()) {
                continue;
            }

            /*
             * Возвращаем позицию
             * чуть выше поверхности.
             */
            return new Location(
                    x + 0.5,
                    y + 1,
                    z + 0.5,
                    level
            );
        }

        return null;
    }

    private boolean isWorldAllowed(Level level) {

        String worldName =
                level.getName();

        for (String configured :
                plugin.getConfig()
                        .getStringList("rtp.worlds")) {

            if (configured.equalsIgnoreCase(
                    worldName
            )) {

                return true;
            }
        }

        return false;
    }

    private String getMessage(String path) {

        String message =
                plugin.getConfig()
                        .getString(path);

        if (message == null) {
            return "";
        }

        String prefix =
                plugin.getConfig()
                        .getString(
                                "messages.prefix",
                                ""
                        );

        return prefix + message;
    }
}