package io.github.ahelton5.simpleHealthBar.tracking;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MobDamageTracker {

    private final long damageVisibleMs;
    private final Map<UUID, Long> lastDamagedAt = new ConcurrentHashMap<>();

    public MobDamageTracker(long damageVisibleMs) {
        this.damageVisibleMs = damageVisibleMs;
    }

    public void markDamaged(UUID mobId) {
        lastDamagedAt.put(mobId, System.currentTimeMillis());
    }

    public boolean isRecentlyDamaged(UUID mobId) {
        Long time = lastDamagedAt.get(mobId);
        if (time == null) return false;
        return (System.currentTimeMillis() - time) < damageVisibleMs;
    }

    public void clear(UUID mobId) {
        lastDamagedAt.remove(mobId);
    }
}