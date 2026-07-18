package io.github.ahelton5.simpleHealthBar.tracking;

import io.github.ahelton5.simpleHealthBar.config.HealthBarConfig;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import me.tofaa.entitylib.meta.display.AbstractDisplayMeta;
import me.tofaa.entitylib.meta.display.TextDisplayMeta;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BarManager {

    // viewer UUID -> (target UUID -> TrackedBar)
    private final Map<UUID, Map<UUID, TrackedBar>> bars = new ConcurrentHashMap<>();

    public boolean hasBar(UUID viewer, UUID target) {
        Map<UUID, TrackedBar> map = bars.get(viewer);
        return map != null && map.containsKey(target);
    }
    public TrackedBar createBar(Player viewer, LivingEntity target, Component initialText, HealthBarConfig config) {
        User user = PacketEvents.getAPI().getPlayerManager().getUser(viewer);

        WrapperEntity display = new WrapperEntity(
                com.github.retrooper.packetevents.protocol.entity.type.EntityTypes.TEXT_DISPLAY);

        TextDisplayMeta meta = (TextDisplayMeta) display.getEntityMeta();
        meta.setText(initialText);
        meta.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
        float offset = (target instanceof Player) ? config.getVerticalOffset() : 0f;
        meta.setTranslation(new Vector3f(0f, offset, 0f));

        display.spawn(new com.github.retrooper.packetevents.protocol.world.Location(
                target.getLocation().getX(),
                target.getLocation().getY() + 2.1,
                target.getLocation().getZ(),
                0f, 0f));
        display.addViewer(user);

        WrapperPlayServerSetPassengers mount = new WrapperPlayServerSetPassengers(
                target.getEntityId(),
                new int[]{display.getEntityId()}
        );
        user.sendPacket(mount);

        TrackedBar bar = new TrackedBar(viewer.getUniqueId(), target.getUniqueId(), display, TrackedBar.BarType.AUTO);
        bars.computeIfAbsent(viewer.getUniqueId(), k -> new ConcurrentHashMap<>())
                .put(target.getUniqueId(), bar);
        return bar;
    }

    public void updateBarText(UUID viewer, UUID target, Component text) {
        TrackedBar bar = get(viewer, target);
        if (bar == null) return;
        if (text.equals(bar.getLastSentText())) return; // no change, skip the packet

        TextDisplayMeta meta = (TextDisplayMeta) bar.getDisplayEntity().getEntityMeta();
        meta.setText(text);
        bar.setLastSentText(text);
    }

    public void removeBar(UUID viewer, UUID target) {
        Map<UUID, TrackedBar> map = bars.get(viewer);
        if (map == null) return;
        TrackedBar bar = map.remove(target);
        if (bar != null) bar.getDisplayEntity().remove(); // verify method name — see note below
    }

    public void removeStaleForViewer(UUID viewer, Set<UUID> stillInRange, TrackedBar.BarType type) {
        Map<UUID, TrackedBar> map = bars.get(viewer);
        if (map == null) return;
        map.entrySet().removeIf(entry -> {
            TrackedBar bar = entry.getValue();
            if (bar.getType() != type) return false; // not this sweep's concern
            if (!stillInRange.contains(entry.getKey())) {
                bar.getDisplayEntity().remove();
                return true;
            }
            return false;
        });
    }

    public void removeAllForViewer(UUID viewer) {
        Map<UUID, TrackedBar> map = bars.remove(viewer);
        if (map == null) return;
        map.values().forEach(bar -> bar.getDisplayEntity().remove());
    }

    public void removeAllForTarget(UUID target) {
        bars.values().forEach(map -> {
            TrackedBar bar = map.remove(target);
            if (bar != null) bar.getDisplayEntity().remove();
        });
    }

    private TrackedBar get(UUID viewer, UUID target) {
        Map<UUID, TrackedBar> map = bars.get(viewer);
        return map == null ? null : map.get(target);
    }

    public java.util.List<TrackedBar> getBarsByType(TrackedBar.BarType type) {
        java.util.List<TrackedBar> result = new java.util.ArrayList<>();
        bars.values().forEach(map -> map.values().forEach(bar -> {
            if (bar.getType() == type) result.add(bar);
        }));
        return result;
    }

    public void removeAll() {
        bars.values().forEach(map -> map.values().forEach(bar -> bar.getDisplayEntity().remove()));
        bars.clear();
    }

    public java.util.Collection<TrackedBar> getBarsForViewer(UUID viewer) {
        Map<UUID, TrackedBar> map = bars.get(viewer);
        return map == null ? java.util.Collections.emptyList() : java.util.Collections.unmodifiableCollection(map.values());
    }
}