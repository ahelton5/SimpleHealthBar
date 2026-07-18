package io.github.ahelton5.simpleHealthBar.tracking;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BarPreferences {

    private final Set<UUID> disabled = ConcurrentHashMap.newKeySet();

    /** Returns the new state: true if bars are now disabled for this viewer. */
    public boolean toggle(UUID viewer) {
        if (disabled.contains(viewer)) {
            disabled.remove(viewer);
            return false;
        } else {
            disabled.add(viewer);
            return true;
        }
    }

    public boolean isDisabled(UUID viewer) {
        return disabled.contains(viewer);
    }
}