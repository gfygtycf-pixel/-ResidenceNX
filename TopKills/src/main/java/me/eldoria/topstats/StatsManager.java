package me.eldoria.topstats;

import cn.nukkit.utils.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatsManager {

    private final Main plugin;

    private final Map<String, PlayerStats> stats = new HashMap<>();

    private Config config;

    public StatsManager(Main plugin) {
        this.plugin = plugin;
    }

    public void load() {

        File file = new File(
                plugin.getDataFolder(),
                "stats.yml"
        );

        config = new Config(
                file,
                Config.YAML
        );

        stats.clear();

        for (String key : config.getKeys(false)) {

            Object object = config.get(key);

            if (!(object instanceof Map)) {
                continue;
            }

            Map<?, ?> data = (Map<?, ?>) object;

            int kills = getInt(
                    data.get("kills")
            );

            int deaths = getInt(
                    data.get("deaths")
            );

            PlayerStats playerStats =
                    new PlayerStats(
                            key,
                            kills,
                            deaths
                    );

            stats.put(
                    key.toLowerCase(Locale.ROOT),
                    playerStats
            );
        }

        plugin.getLogger().info(
                "Loaded " + stats.size() + " player statistics."
        );
    }

    private int getInt(Object value) {

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        return 0;
    }

    public PlayerStats getStats(String name) {

        String key =
                name.toLowerCase(Locale.ROOT);

        PlayerStats playerStats =
                stats.get(key);

        if (playerStats == null) {

            playerStats =
                    new PlayerStats(name);

            stats.put(
                    key,
                    playerStats
            );
        }

        return playerStats;
    }

    public void registerKill(String player) {

        getStats(player).addKill();

        save();
    }

    public void registerDeath(String player) {

        getStats(player).addDeath();

        save();
    }

    public List<PlayerStats> getTopKills(int limit) {

        List<PlayerStats> result =
                new ArrayList<>(stats.values());

        result.sort(
                Comparator
                        .comparingInt(PlayerStats::getKills)
                        .reversed()
                        .thenComparing(
                                PlayerStats::getName,
                                String.CASE_INSENSITIVE_ORDER
                        )
        );

        return limitList(result, limit);
    }

    public List<PlayerStats> getTopDeaths(int limit) {

        List<PlayerStats> result =
                new ArrayList<>(stats.values());

        result.sort(
                Comparator
                        .comparingInt(PlayerStats::getDeaths)
                        .reversed()
                        .thenComparing(
                                PlayerStats::getName,
                                String.CASE_INSENSITIVE_ORDER
                        )
        );

        return limitList(result, limit);
    }

    public List<PlayerStats> getTopKd(int limit) {

        List<PlayerStats> result =
                new ArrayList<>(stats.values());

        result.sort(
                Comparator
                        .comparingDouble(PlayerStats::getKd)
                        .reversed()
                        .thenComparing(
                                PlayerStats::getName,
                                String.CASE_INSENSITIVE_ORDER
                        )
        );

        return limitList(result, limit);
    }

    private List<PlayerStats> limitList(
            List<PlayerStats> list,
            int limit
    ) {

        return list.subList(
                0,
                Math.min(limit, list.size())
        );
    }

    public void save() {

        if (config == null) {
            return;
        }

        Config newConfig =
                new Config(
                        new File(
                                plugin.getDataFolder(),
                                "stats.yml"
                        ),
                        Config.YAML
                );

        for (PlayerStats player : stats.values()) {

            Map<String, Object> data =
                    new HashMap<>();

            data.put(
                    "kills",
                    player.getKills()
            );

            data.put(
                    "deaths",
                    player.getDeaths()
            );

            newConfig.set(
                    player.getName(),
                    data
            );
        }

        newConfig.save();

        config = newConfig;
    }
}