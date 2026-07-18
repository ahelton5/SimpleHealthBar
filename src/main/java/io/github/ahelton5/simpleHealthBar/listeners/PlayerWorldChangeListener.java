package io.github.ahelton5.simpleHealthBar.listeners;

import io.github.ahelton5.simpleHealthBar.tracking.BarManager;
import io.github.ahelton5.simpleHealthBar.tracking.ViewerResyncTracker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerWorldChangeListener implements Listener {

    private final BarManager barManager;
    private final ViewerResyncTracker resyncTracker;

    public PlayerWorldChangeListener(BarManager barManager, ViewerResyncTracker resyncTracker) {
        this.barManager = barManager;
        this.resyncTracker = resyncTracker;
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        barManager.removeAllForViewer(player.getUniqueId());
        barManager.removeAllForTarget(player.getUniqueId());
        resyncTracker.nukeAllForViewer(player);
        resyncTracker.markWorldChanged(player.getUniqueId());
    }
}