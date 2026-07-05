package me.residencenx.model;

import cn.nukkit.math.Vector3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class Region {

    private String name;
    private String world;

    // 👑 владелец (правильно: UUID + имя)
    private UUID ownerUUID;
    private String ownerName;

    private Cuboid cuboid;

    private Vector3 home;

    private HashSet<String> members = new HashSet<>();
    private HashSet<String> owners = new HashSet<>();

    private HashMap<String, Boolean> flags = new HashMap<>();

    public Region(String name, String world, UUID ownerUUID, String ownerName, Cuboid cuboid) {
        this.name = name;
        this.world = world;
        this.ownerUUID = ownerUUID;
        this.ownerName = ownerName;
        this.cuboid = cuboid;

        this.owners.add(ownerUUID.toString());

        // 🔧 дефолтные флаги
        flags.put("build", true);
        flags.put("destroy", true);
        flags.put("pvp", false);
        flags.put("container", true);
        flags.put("use", true);

        // 🏠 home по умолчанию
        this.home = cuboid.getMin();
    }

    // =====================
    // BASIC
    // =====================

    public String getName() {
        return name;
    }

    public String getWorld() {
        return world;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    // =====================
    // HOME
    // =====================

    public Vector3 getHome() {
        return home;
    }

    public void setHome(Vector3 home) {
        this.home = home;
    }

    // =====================
    // ACCESS
    // =====================

    public boolean isOwner(UUID uuid) {
        return owners.contains(uuid.toString());
    }

    public boolean isMember(UUID uuid) {
        return members.contains(uuid.toString());
    }

    public boolean hasAccess(UUID uuid) {
        return isOwner(uuid) || isMember(uuid);
    }

    public void addMember(UUID uuid) {
        members.add(uuid.toString());
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid.toString());
    }

    public void addOwner(UUID uuid) {
        owners.add(uuid.toString());
    }

    public void removeOwner(UUID uuid) {
        owners.remove(uuid.toString());
    }

    // =====================
    // FLAGS
    // =====================

    public boolean getFlag(String key) {
        return flags.getOrDefault(key.toLowerCase(), true);
    }

    public void setFlag(String key, boolean value) {
        flags.put(key.toLowerCase(), value);
    }

    public HashMap<String, Boolean> getFlags() {
        return flags;
    }
}