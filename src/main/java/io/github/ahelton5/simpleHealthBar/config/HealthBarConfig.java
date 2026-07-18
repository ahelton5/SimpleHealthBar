package io.github.ahelton5.simpleHealthBar.config;

import io.github.ahelton5.simpleHealthBar.util.BarStyle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import java.util.HashSet;
import java.util.Set;

public class HealthBarConfig {

    private final double showDistance;
    private final double lookReach;
    private final long damageVisibleMs;
    private final long updateIntervalTicks;
    private final float verticalOffset;
    private final Set<EntityType> excludedEntityTypes;
    private final boolean playerBarsEnabled;
    private final boolean mobBarsEnabled;
    private final BarStyle barStyle;


    public HealthBarConfig(FileConfiguration config) {
        this.showDistance = config.getDouble("show-distance", 20.0);
        this.lookReach = config.getDouble("look-reach", 5.0);
        this.damageVisibleMs = (long) (config.getDouble("damage-visible-seconds", 5.0) * 1000);
        this.updateIntervalTicks = config.getLong("update-interval-ticks", 10);
        this.verticalOffset = (float) config.getDouble("vertical-offset", 0.3);
        this.excludedEntityTypes = new HashSet<>();
        for (String name : config.getStringList("excluded-entity-types")) {
            try {
                excludedEntityTypes.add(EntityType.valueOf(name.toUpperCase()));
            } catch (IllegalArgumentException e) {
                // unknown entity type name in config — skip it rather than crash startup
            }
        }
        this.playerBarsEnabled = config.getBoolean("enable-player-bars", true);
        this.mobBarsEnabled = config.getBoolean("enable-mob-bars", true);
        BarStyle parsedStyle;
        try {
            parsedStyle = BarStyle.valueOf(config.getString("bar-style", "HEARTS_SCALED").toUpperCase());
        } catch (IllegalArgumentException e) {
            parsedStyle = BarStyle.HEARTS_SCALED; // unknown value in config — fall back rather than crash
        }
        this.barStyle = parsedStyle;
    }

    public double getShowDistance() { return showDistance; }
    public double getLookReach() { return lookReach; }
    public long getDamageVisibleMs() { return damageVisibleMs; }
    public long getUpdateIntervalTicks() { return updateIntervalTicks; }
    public float getVerticalOffset() { return verticalOffset; }
    public Set<EntityType> getExcludedEntityTypes() { return excludedEntityTypes; }
    public boolean isPlayerBarsEnabled() { return playerBarsEnabled; }
    public boolean isMobBarsEnabled() { return mobBarsEnabled; }
    public BarStyle getBarStyle() { return barStyle; }


}