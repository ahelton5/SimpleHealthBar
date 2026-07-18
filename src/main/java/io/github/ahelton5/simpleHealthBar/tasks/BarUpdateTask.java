package io.github.ahelton5.simpleHealthBar.tasks;

import io.github.ahelton5.simpleHealthBar.config.HealthBarConfig;
import io.github.ahelton5.simpleHealthBar.tracking.*;
import io.github.ahelton5.simpleHealthBar.util.HealthTextFormatter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BarUpdateTask extends BukkitRunnable {

    private final BarManager barManager;
    private final MobDamageTracker damageTracker;
    private final HealthBarConfig config;
    private final BarPreferences preferences;
    private final ViewerResyncTracker resyncTracker;

    public BarUpdateTask(BarManager barManager, MobDamageTracker damageTracker, HealthBarConfig config, BarPreferences preferences, ViewerResyncTracker resyncTracker) {
        this.barManager = barManager;
        this.damageTracker = damageTracker;
        this.config = config;
        this.preferences = preferences;
        this.resyncTracker = resyncTracker;
    }

    @Override
    public void run() {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (preferences.isDisabled(viewer.getUniqueId())) {
                barManager.removeAllForViewer(viewer.getUniqueId());
                continue;
            }

            Set<UUID> visibleNow = new HashSet<>();
            LivingEntity lookedAt = getLookedAtEntity(viewer);

            // Self-bars are opt-in via /healthbar — we never spawn one automatically,
            // but if one already exists, keep it alive and live-updated like any other bar.
            if (config.isPlayerBarsEnabled() && barManager.hasBar(viewer.getUniqueId(), viewer.getUniqueId())) {
                visibleNow.add(viewer.getUniqueId());
                barManager.updateBarText(viewer.getUniqueId(), viewer.getUniqueId(),
                        HealthTextFormatter.format(viewer, config.getBarStyle()));
            }

            // one spatial query instead of two — covers both players and mobs
            for (Entity entity : viewer.getNearbyEntities(config.getShowDistance(), config.getShowDistance(), config.getShowDistance())) {
                if (!(entity instanceof LivingEntity target)) continue;
                if (config.getExcludedEntityTypes().contains(target.getType())) continue;

                boolean shouldShow;
                if (target instanceof Player) {
                    if (!config.isPlayerBarsEnabled()) continue;
                    shouldShow = true;
                } else {
                    if (!config.isMobBarsEnabled()) continue;
                    boolean damaged = damageTracker.isRecentlyDamaged(target.getUniqueId());
                    boolean lookedDirectly = target.equals(lookedAt);
                    shouldShow = damaged || lookedDirectly;
                }

                if (!shouldShow) continue;

                visibleNow.add(target.getUniqueId());
                Component healthText = HealthTextFormatter.format(target, config.getBarStyle());

                if (!barManager.hasBar(viewer.getUniqueId(), target.getUniqueId())) {
                    if (resyncTracker.isInWorldChangeGrace(viewer.getUniqueId())) continue; // let vanilla's own tracking catch up first
                    TrackedBar bar = barManager.createBar(viewer, target, healthText, config);
                    resyncTracker.recordSpawn(viewer.getUniqueId(), bar.getDisplayEntity().getEntityId());
                } else {
                    barManager.updateBarText(viewer.getUniqueId(), target.getUniqueId(), healthText);
                }
            }

            barManager.removeStaleForViewer(viewer.getUniqueId(), visibleNow, TrackedBar.BarType.AUTO);
        }
    }

    private LivingEntity getLookedAtEntity(Player viewer) {
        RayTraceResult result = viewer.rayTraceEntities((int) config.getLookReach());
        if (result == null || result.getHitEntity() == null) return null;
        return result.getHitEntity() instanceof LivingEntity le ? le : null;
    }
}