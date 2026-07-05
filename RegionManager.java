package me.residencenx.manager;

import me.residencenx.model.Region;
import me.residencenx.model.Cuboid;

import java.util.*;

public class RegionManager {

    private final Map<String, Region> regions = new HashMap<>();

    public void addRegion(Region region) {
        regions.put(region.getName().toLowerCase(), region);
    }

    public void removeRegion(String name) {
        regions.remove(name.toLowerCase());
    }

    public Region getRegion(String name) {
        return regions.get(name.toLowerCase());
    }

    public Collection<Region> getRegions() {
        return regions.values();
    }

    public Region getRegionAt(String world, cn.nukkit.math.Vector3 pos) {

        for (Region region : regions.values()) {
            if (!region.getWorld().equalsIgnoreCase(world)) continue;

            if (region.contains(pos)) {
                return region;
            }
        }

        return null;
    }

    public boolean isInsideAny(String world, cn.nukkit.math.Vector3 pos) {
        return getRegionAt(world, pos) != null;
    }

    public boolean overlaps(Cuboid cuboid, String world) {

        for (Region region : regions.values()) {
            if (!region.getWorld().equalsIgnoreCase(world)) continue;

            // простая проверка пересечения через точки
            if (region.getCuboid().contains(cuboid.getMin()) ||
                region.getCuboid().contains(cuboid.getMax())) {
                return true;
            }
        }

        return false;
    }
}