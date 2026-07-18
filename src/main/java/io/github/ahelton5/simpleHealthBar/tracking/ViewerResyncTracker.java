package io.github.ahelton5.simpleHealthBar.tracking;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ViewerResyncTracker {

    private static final long WORLD_CHANGE_GRACE_MS = 1500;

    private final Map<UUID, Set<Integer>> spawnedIds = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastWorldChangeAt = new ConcurrentHashMap<>();

    public void recordSpawn(UUID viewer, int entityId) {
        spawnedIds.computeIfAbsent(viewer, k -> ConcurrentHashMap.newKeySet()).add(entityId);
    }

    public void markWorldChanged(UUID viewer) {
        lastWorldChangeAt.put(viewer, System.currentTimeMillis());
    }

    public boolean isInWorldChangeGrace(UUID viewer) {
        Long time = lastWorldChangeAt.get(viewer);
        if (time == null) return false;
        return (System.currentTimeMillis() - time) < WORLD_CHANGE_GRACE_MS;
    }

    /** Destroys every entity ID ever spawned for this viewer, regardless of whether
     *  BarManager still tracks it — a direct safety net against orphaned entities. */
    public void nukeAllForViewer(Player viewer) {
        Set<Integer> ids = spawnedIds.remove(viewer.getUniqueId());
        if (ids == null || ids.isEmpty()) return;

        User user = PacketEvents.getAPI().getPlayerManager().getUser(viewer);
        int[] idArray = ids.stream().mapToInt(Integer::intValue).toArray();
        user.sendPacket(new WrapperPlayServerDestroyEntities(idArray));
    }

    public void clearForViewer(UUID viewer) {
        spawnedIds.remove(viewer);
        lastWorldChangeAt.remove(viewer);
    }
}