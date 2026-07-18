package io.github.ahelton5.simpleHealthBar.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

public class HealthTextFormatter {

    public static Component format(LivingEntity entity, BarStyle style) {
        double health = entity.getHealth();
        double maxHealth = entity.getAttribute(Attribute.MAX_HEALTH).getValue();
        double percent = maxHealth <= 0 ? 0 : health / maxHealth;

        return switch (style) {
            case HEARTS_SCALED -> heartsScaled(health, maxHealth);
            case HEARTS_FIXED_10 -> heartsFixed10(percent);
            case SHORT -> Component.text("❤ " + Math.round(health) + "/" + Math.round(maxHealth), NamedTextColor.RED);
            case PERCENTAGE -> Component.text("❤ " + Math.round(percent * 100) + "%", NamedTextColor.RED);
            case GRADIENT_SHORT -> gradientShort(health, maxHealth, percent);
        };
    }

    private static Component heartsScaled(double health, double maxHealth) {
        int totalHearts = (int) Math.ceil(maxHealth / 2.0);
        int fullHearts = (int) Math.round(health / 2.0);
        return heartsRow(totalHearts, fullHearts);
    }

    private static Component heartsFixed10(double percent) {
        int fullHearts = (int) Math.round(percent * 10);
        return heartsRow(10, fullHearts);
    }

    private static Component heartsRow(int total, int full) {
        Component result = Component.empty();
        for (int i = 0; i < total; i++) {
            NamedTextColor color = (i < full) ? NamedTextColor.RED : NamedTextColor.DARK_GRAY;
            result = result.append(Component.text("❤", color));
        }
        return result;
    }

    private static Component gradientShort(double health, double maxHealth, double percent) {
        TextColor color;
        if (percent > 0.6) color = NamedTextColor.GREEN;
        else if (percent > 0.3) color = NamedTextColor.YELLOW;
        else color = NamedTextColor.RED;

        return Component.text("❤ " + Math.round(health) + "/" + Math.round(maxHealth), color);
    }
}