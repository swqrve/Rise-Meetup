package me.swerve.meetup.util;

import lombok.Getter;
import lombok.Setter;

public class ScatterLocation {

    @Getter private final int x;
    @Getter private final int y;
    @Getter private final int z;

    @Getter @Setter private int id;

    public ScatterLocation(int x, int y, int z, int id) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.id = id;
    }
}
