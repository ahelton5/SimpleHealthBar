package io.github.ahelton5.simpleHealthBar.commands;

import io.github.ahelton5.simpleHealthBar.config.HealthBarConfig;
import io.github.ahelton5.simpleHealthBar.tracking.BarManager;
import io.github.ahelton5.simpleHealthBar.tracking.BarPreferences;
import io.github.ahelton5.simpleHealthBar.tracking.TrackedBar;
import io.github.ahelton5.simpleHealthBar.tracking.ViewerResyncTracker;
import io.github.ahelton5.simpleHealthBar.util.HealthTextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    private final BarManager barManager;
    private final HealthBarConfig config;
    private final BarPreferences preferences;
    private final ViewerResyncTracker resyncTracker;

    public Commands(BarManager barManager, HealthBarConfig config, BarPreferences preferences, ViewerResyncTracker resyncTracker) {
        this.barManager = barManager;
        this.config = config;
        this.preferences = preferences;
        this.resyncTracker = resyncTracker;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length == 0) {
            sender.sendMessage("Usage: /healthbar [toggle] [killall] [debug] [self]");
        }

        switch (args[0].toLowerCase()) {
            case "toggle" -> {
                boolean nowDisabled = preferences.toggle(player.getUniqueId());
                if (nowDisabled) {
                    barManager.removeAllForViewer(player.getUniqueId());
                    sender.sendMessage("Health bars hidden.");
                } else {
                    sender.sendMessage("Health bars shown.");
                }
            }
            case "killall" -> {
                if (!player.isOp()) {
                    sender.sendMessage("You don't have permission to do that.");
                    return true;
                }
                barManager.removeAll();
                for (Player online : Bukkit.getOnlinePlayers()) {
                    resyncTracker.nukeAllForViewer(online);
                }
                sender.sendMessage("Removed all active health bars.");
            }
            case "debug" -> {
                var bars = barManager.getBarsForViewer(player.getUniqueId());
                sender.sendMessage("Tracked bars as viewer: " + bars.size());
                for (TrackedBar bar : bars) {
                    sender.sendMessage(" - target=" + bar.getTargetId()
                            + " displayEntityId=" + bar.getDisplayEntity().getEntityId()
                            + " type=" + bar.getType());
                }
            }
            case "self" -> {
                barManager.createBar(player, player, HealthTextFormatter.format(player, config.getBarStyle()), config);
                sender.sendMessage("Spawned a mounted health bar above your head.");
                return true;
            }
            default -> sender.sendMessage("Usage: /healthbar [toggle] [killall] [debug] [self]");
        }

        return true;
    }
}