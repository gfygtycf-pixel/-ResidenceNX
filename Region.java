package me.residencenx.model;

import java.util.HashSet;
import java.util.UUID;

public class Region {

    private String name;
    private String world;

    private UUID owner;

    private Cuboid cuboid;

    private HashSet<String> members = new HashSet<>();
    private HashSet<String> owners = new HashSet<>();

    public Region(String name, String world, UUID owner, Cuboid cuboid) {
        this.name = name;
        this.world = world;
        this.owner = owner;
        this.cuboid = cuboid;

        this.owners.add(owner.toString());
    }

    public String getName() {
        return name;
    }

    public String getWorld() {
        return world;
    }

    public UUID getOwner() {
        return owner;
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    public HashSet<String> getMembers() {
        return members;
    }

    public HashSet<String> getOwners() {
        return owners;
    }

    public boolean isOwner(UUID uuid) {
        return owners.contains(uuid.toString());
    }

    public boolean isMember(UUID uuid) {
        return members.contains(uuid.toString());
    }

    public boolean contains(java.util.Vector3 pos) {
        return cuboid.contains(pos);
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
}