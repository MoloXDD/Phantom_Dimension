package com.molox.infcedim.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.HashSet;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class ScenarioCoreGlowTracker {
    private static final Set<Integer> markedEntities = new HashSet<>();

    public static void mark(int entityId) {
        markedEntities.add(entityId);
    }

    public static void unmark(int entityId) {
        markedEntities.remove(entityId);
    }

    public static void clearAll() {
        markedEntities.clear();
    }

    public static boolean isMarked(int entityId) {
        return markedEntities.contains(entityId);
    }

    public static Set<Integer> getMarked() {
        return markedEntities;
    }
}