package io.github.ahelton5.simpleHealthBar.listeners;

import io.github.ahelton5.simpleHealthBar.tracking.BarManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final BarManager barManager;

    public PlayerQuitListener(BarManager barManager) {
        this.barManager = barManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        barManager.removeAllForViewer(player.getUniqueId());
        barManager.removeAllForTarget(player.getUniqueId());
    }
}