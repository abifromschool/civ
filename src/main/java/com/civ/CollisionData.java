package com.civ;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CollisionData {

    public static class Entry {
        public double mx;
        public double my;
        public double mz;

        public boolean ground;
    }

    public static final Map<UUID, Entry> DATA = new ConcurrentHashMap<>();
}