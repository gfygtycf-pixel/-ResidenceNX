package me.residencenx.storage;

import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;

import me.residencenx.Main;
import me.residencenx.model.Cuboid;
import me.residencenx.model.Region;

import java.io.File;
import java.util.Map;
import java.util.UUID;

public class RegionStorage {

    private Config config;

    public RegionStorage() {
        File file = new File(Main.getInstance().getDataFolder(), "regions.yml");
        this.config = new Config(file, Config.YAML);
    }

    // 💾 сохранить все регионы
    public void saveAll() {

        config.getAll().clear();

        for (Region region : Main.getInstance().getRegionManager().getRegions()) {

            String path = "regions." + region.getName();

            config.set(path + ".world", region.getWorld());
            config.set(path + ".owner", region.getOwner().toString());

            config.set(path + ".pos1.x", region.getCuboid().getMin().getX());
            config.set(path + ".pos1.y", region.getCuboid().getMin().getY());
            config.set(path + ".pos1.z", region.getCuboid().getMin().getZ());

            config.set(path + ".pos2.x", region.getCuboid().getMax().getX());
            config.set(path + ".pos2.y", region.getCuboid().getMax().getY());
            config.set(path + ".pos2.z", region.getCuboid().getMax().getZ());
        }

        config.save();
    }

    // 📥 загрузить регионы
    public void loadAll() {

        if (!config.exists("regions")) return;

        Map<String, Object> regions = config.getSection("regions").getAllMap();

        for (String name : regions.keySet()) {

            String path = "regions." + name;

            String world = config.getString(path + ".world");
            UUID owner = UUID.fromString(config.getString(path + ".owner"));

            Vector3 pos1 = new Vector3(
                    config.getDouble(path + ".pos1.x"),
                    config.getDouble(path + ".pos1.y"),
                    config.getDouble(path + ".pos1.z")
            );

            Vector3 pos2 = new Vector3(
                    config.getDouble(path + ".pos2.x"),
                    config.getDouble(path + ".pos2.y"),
                    config.getDouble(path + ".pos2.z")
            );

            Cuboid cuboid = new Cuboid(pos1, pos2);

            Region region = new Region(
                    name,
                    world,
                    owner,
                    cuboid
            );

            Main.getInstance().getRegionManager().addRegion(region);
        }
    }
}