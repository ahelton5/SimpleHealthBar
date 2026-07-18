package io.github.ahelton5.simpleHealthBar;

import io.github.ahelton5.simpleHealthBar.commands.Commands;
import io.github.ahelton5.simpleHealthBar.config.HealthBarConfig;
import io.github.ahelton5.simpleHealthBar.listeners.MobDamageListener;
import io.github.ahelton5.simpleHealthBar.listeners.MobDeathListener;
import io.github.ahelton5.simpleHealthBar.listeners.PlayerQuitListener;
import io.github.ahelton5.simpleHealthBar.listeners.PlayerWorldChangeListener;
import io.github.ahelton5.simpleHealthBar.tasks.BarUpdateTask;
import io.github.ahelton5.simpleHealthBar.tracking.BarManager;
import io.github.ahelton5.simpleHealthBar.tracking.BarPreferences;
import io.github.ahelton5.simpleHealthBar.tracking.MobDamageTracker;
import io.github.ahelton5.simpleHealthBar.tracking.ViewerResyncTracker;
import org.bukkit.plugin.java.JavaPlugin;
import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform;
public final class SimpleHealthBar extends JavaPlugin {

    private BarManager barManager;

    @Override
    public void onLoad() {
        // PacketEvents must be initialized before the server finishes starting
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();

        SpigotEntityLibPlatform platform = new SpigotEntityLibPlatform(this);
        APIConfig peConfig = new APIConfig(PacketEvents.getAPI()).usePlatformLogger();
        EntityLib.init(platform, peConfig);

        saveDefaultConfig(); // copies the bundled config.yml to plugins/SimpleHealthBar/config.yml on first run
        HealthBarConfig config = new HealthBarConfig(getConfig());

        ViewerResyncTracker resyncTracker = new ViewerResyncTracker();

        // Player bars
        barManager = new BarManager();
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(barManager), this);
        getServer().getPluginManager().registerEvents(new PlayerWorldChangeListener(barManager, resyncTracker), this);
        //new PlayerBarUpdateTask(barManager).runTaskTimer(this, 0L, 10L); // every 10 ticks = 0.5s

        //Mob bars
        MobDamageTracker damageTracker = new MobDamageTracker(config.getDamageVisibleMs());
        getServer().getPluginManager().registerEvents(new MobDamageListener(damageTracker), this);
        getServer().getPluginManager().registerEvents(new MobDeathListener(barManager, damageTracker), this);
        //new MobBarUpdateTask(barManager, damageTracker).runTaskTimer(this, 0L, 10L);

        BarPreferences preferences = new BarPreferences();
        new BarUpdateTask(barManager, damageTracker, config, preferences, resyncTracker)
                .runTaskTimer(this, 0L, config.getUpdateIntervalTicks());
        //Commands
        getCommand("healthbar").setExecutor(new Commands(barManager, config, preferences, resyncTracker));

        getLogger().info("SimpleHealthBar enabled!");
    }

    @Override
    public void onDisable() {
        barManager.removeAll();
        PacketEvents.getAPI().terminate();

        getLogger().info("SimpleHealthBar disabled!");
    }
}
