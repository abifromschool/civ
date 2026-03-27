package com.civ;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CollisionData {

    public static class Entry {
        // future collsion
        public double nx;
        public double nz;

        // Create motion
        public double mx;
        public double my;
        public double mz;

        // check if IV is on ground surface
        public boolean ground;
    }

    public static final Map<UUID, Entry> DATA = new ConcurrentHashMap<>();
}