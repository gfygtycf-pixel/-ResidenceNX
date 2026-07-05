package me.residencenx.manager;

import me.residencenx.model.Selection;

import java.util.HashMap;
import java.util.UUID;

public class SelectionManager {

    private final HashMap<UUID, Selection> selections = new HashMap<>();

    public Selection getSelection(UUID uuid) {
        return selections.computeIfAbsent(uuid, k -> new Selection());
    }

    public void setPos1(UUID uuid, cn.nukkit.math.Vector3 pos) {
        getSelection(uuid).setPos1(pos);
    }

    public void setPos2(UUID uuid, cn.nukkit.math.Vector3 pos) {
        getSelection(uuid).setPos2(pos);
    }

    public Selection get(UUID uuid) {
        return selections.get(uuid);
    }

    public void clear(UUID uuid) {
        selections.remove(uuid);
    }
}