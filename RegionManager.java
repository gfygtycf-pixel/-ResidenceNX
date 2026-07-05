package me.residencenx.manager;

import cn.nukkit.level.Position;
import me.residencenx.model.Cuboid;
import me.residencenx.model.Region;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RegionManager {

    private final Map<String, Region> regions = new HashMap<>();

    // =====================
    // ADD REGION
    // =====================
    public void addRegion(Region region) {
        regions.put(region.getName().toLowerCase(), region);
    }

    // =====================
    // GET BY NAME
    // =====================
    public Region getRegion(String name) {
        return regions.get(name.toLowerCase());
    }

    // =====================
    // REMOVE REGION
    // =====================
    public void removeRegion(String name) {
        regions.remove(name.toLowerCase());
    }

    // =====================
    // ALL REGIONS
    // =====================
    public Collection<Region> getRegions() {
        return regions.values();
    }

    // =====================
    // GET REGION BY POSITION
    // =====================
    public Region getRegionAt(String world, Position pos) {

        for (Region region : regions.values()) {

            if (!region.getWorld().equalsIgnoreCase(world)) {
                continue;
            }

            Cuboid c = region.getCuboid();

            if (c.contains(pos)) {
                return region;
            }
        }

        return null;
    }

    // =====================
    // OVERLAP CHECK
    // =====================
    public boolean overlaps(Cuboid newCuboid, String world) {

        for (Region region : regions.values()) {

            if (!region.getWorld().equalsIgnoreCase(world)) {
                continue;
            }

            if (region.getCuboid().intersects(newCuboid)) {
                return true;
            }
        }

        return false;
    }
}