package io.github.ahelton5.simpleHealthBar.listeners;

import io.github.ahelton5.simpleHealthBar.tracking.BarManager;
import io.github.ahelton5.simpleHealthBar.tracking.MobDamageTracker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobDeathListener implements Listener {

    private final BarManager barManager;
    private final MobDamageTracker damageTracker;

    public MobDeathListener(BarManager barManager, MobDamageTracker damageTracker) {
        this.barManager = barManager;
        this.damageTracker = damageTracker;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        barManager.removeAllForTarget(event.getEntity().getUniqueId());
        damageTracker.clear(event.getEntity().getUniqueId());
    }
}