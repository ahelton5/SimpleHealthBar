package io.github.ahelton5.simpleHealthBar.tracking;

import me.tofaa.entitylib.wrapper.WrapperEntity;
import net.kyori.adventure.text.Component;
import java.awt.*;
import java.util.UUID;

public class TrackedBar {
    public enum BarType { AUTO }
    private final UUID viewerId;
    private final UUID targetId;
    private final WrapperEntity displayEntity;
    private final BarType type;
    private Component lastSentText;

    public TrackedBar(UUID viewerId, UUID targetId, WrapperEntity displayEntity, BarType type) {
        this.viewerId = viewerId;
        this.targetId = targetId;
        this.displayEntity = displayEntity;
        this.type = type;
    }

    public UUID getViewerId() { return viewerId; }
    public UUID getTargetId() { return targetId; }
    public WrapperEntity getDisplayEntity() { return displayEntity; }
    public BarType getType() { return type; }
    public Component getLastSentText() { return lastSentText; }
    public void setLastSentText(Component text) { this.lastSentText = text; }
}