package me.residencenx.model;

import cn.nukkit.math.Vector3;

public class Selection {

    private Vector3 pos1;
    private Vector3 pos2;

    public Vector3 getPos1() {
        return pos1;
    }

    public void setPos1(Vector3 pos1) {
        this.pos1 = pos1;
    }

    public Vector3 getPos2() {
        return pos2;
    }

    public void setPos2(Vector3 pos2) {
        this.pos2 = pos2;
    }

    public boolean isComplete() {
        return pos1 != null && pos2 != null;
    }
}