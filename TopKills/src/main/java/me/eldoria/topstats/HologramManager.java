package me.eldoria.topstats;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.particle.FloatingTextParticle;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HologramManager {

    private final Main plugin;

    private final Map<String, HologramData> holograms =
            new HashMap<>();

    private Config config;

    public HologramManager(Main plugin) {

        this.plugin = plugin;

        load();
    }

    public void load() {

        File file =
                new File(
                        plugin.getDataFolder(),
                        "holograms.yml"
                );

        config =
                new Config(
                        file,
                        Config.YAML
                );

        holograms.clear();

        loadHologram("kills");
        loadHologram("deaths");
        loadHologram("kd");

        updateAll();
    }

    private void loadHologram(String type) {

        String path = type;

        if (!config.exists(path)) {
            return;
        }

        String world =
                config.getString(
                        path + ".world"
                );

        double x =
                config.getDouble(
                        path + ".x"
                );

        double y =
                config.getDouble(
                        path + ".y"
                );

        double z =
                config.getDouble(
                        path + ".z"
                );

        Level level =
                plugin.getServer()
                        .getLevelByName(world);

        if (level == null) {

            plugin.getLogger().warning(
                    "World not loaded for hologram: "
                            + type
            );

            return;
        }

        holograms.put(
                type,
                new HologramData(
                        type,
                        level,
                        x,
                        y,
                        z
                )
        );
    }

    public void create(
            String type,
            Player player
    ) {

        remove(type);

        Location location =
                player.getLocation();

        HologramData hologram =
                new HologramData(
                        type,
                        player.getLevel(),
                        location.x,
                        location.y,
                        location.z
                );

        holograms.put(
                type,
                hologram
        );

        config.set(
                type + ".world",
                player.getLevel().getName()
        );

        config.set(
                type + ".x",
                location.x
        );

        config.set(
                type + ".y",
                location.y
        );

        config.set(
                type + ".z",
                location.z
        );

        config.save();

        update(type);
    }

    public void remove(String type) {

        HologramData hologram =
                holograms.remove(type);

        if (hologram != null) {
            hologram.remove();
        }

        config.remove(type);

        config.save();
    }

    public void reload() {

        removeParticles();

        load();
    }

    public void updateAll() {

        update("kills");
        update("deaths");
        update("kd");
    }

    public void update(String type) {

        HologramData hologram =
                holograms.get(type);

        if (hologram == null) {
            return;
        }

        hologram.update(
                getText(type)
        );
    }

    private String getText(String type) {

        StringBuilder text =
                new StringBuilder();

        if (type.equals("kills")) {

            text.append(
                    "§6§l⚔ ТОП УБИЙСТВ\n\n"
            );

            List<PlayerStats> top =
                    plugin.getStatsManager()
                            .getTopKills(10);

            appendTop(
                    text,
                    top,
                    "kills"
            );

        } else if (type.equals("deaths")) {

            text.append(
                    "§c§l☠ ТОП СМЕРТЕЙ\n\n"
            );

            List<PlayerStats> top =
                    plugin.getStatsManager()
                            .getTopDeaths(10);

            appendTop(
                    text,
                    top,
                    "deaths"
            );

        } else {

            text.append(
                    "§e§l🏆 ТОП K/D\n\n"
            );

            List<PlayerStats> top =
                    plugin.getStatsManager()
                            .getTopKd(10);

            int position = 1;

            for (PlayerStats stats : top) {

                text.append(
                        "§e#"
                ).append(position)
                 .append(" §f")
                 .append(stats.getName())
                 .append(" §7- §6")
                 .append(
                         String.format(
                                 Locale.US,
                                 "%.2f",
                                 stats.getKd()
                         )
                 )
                 .append("\n");

                position++;
            }
        }

        if (text.toString().trim().equals(
                "§6§l⚔ ТОП УБИЙСТВ"
        ) || text.toString().trim().equals(
                "§c§l☠ ТОП СМЕРТЕЙ"
        ) || text.toString().trim().equals(
                "§e§l🏆 ТОП K/D"
        )) {

            text.append(
                    "§7Пока нет данных."
            );
        }

        return text.toString();
    }

    private void appendTop(
            StringBuilder text,
            List<PlayerStats> top,
            String type
    ) {

        if (top.isEmpty()) {

            text.append(
                    "§7Пока нет данных."
            );

            return;
        }

        int position = 1;

        for (PlayerStats stats : top) {

            text.append(
                    "§e#"
            ).append(position)
             .append(" §f")
             .append(stats.getName())
             .append(" §7- ");

            if (type.equals("kills")) {

                text.append("§a")
                    .append(stats.getKills());

            } else {

                text.append("§c")
                    .append(stats.getDeaths());
            }

            text.append("\n");

            position++;
        }
    }

    private void removeParticles() {

        for (HologramData hologram :
                holograms.values()) {

            hologram.remove();
        }
    }

    public void removeAll() {

        removeParticles();
    }

    private static class HologramData {

        private final String type;
        private final Level level;

        private final double x;
        private final double y;
        private final double z;

        private FloatingTextParticle particle;

        private HologramData(
                String type,
                Level level,
                double x,
                double y,
                double z
        ) {

            this.type = type;
            this.level = level;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        private void update(String text) {

            remove();

            particle =
                    new FloatingTextParticle(
                            new Location(
                                    x,
                                    y,
                                    z,
                                    level
                            ),
                            text,
                            ""
                    );

            level.addParticle(
                    particle
            );
        }

        private void remove() {

            if (particle == null) {
                return;
            }

            level.removeParticle(
                    particle
            );

            particle = null;
        }
    }
}