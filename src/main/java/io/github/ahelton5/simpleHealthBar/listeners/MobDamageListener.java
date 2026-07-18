package io.github.ahelton5.simpleHealthBar.listeners;

import io.github.ahelton5.simpleHealthBar.tracking.MobDamageTracker;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class MobDamageListener implements Listener {

    private final MobDamageTracker damageTracker;

    public MobDamageListener(MobDamageTracker damageTracker) {
        this.damageTracker = damageTracker;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;
        if (event.getEntity() instanceof Player) return; // players are always-visible, handled separately
        damageTracker.markDamaged(event.getEntity().getUniqueId());
    }
}