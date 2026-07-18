package io.github.ahelton5.simpleHealthBar.util; // update to your new package

public enum BarStyle {
    HEARTS_SCALED,   // current behavior — heart count matches actual max health
    HEARTS_FIXED_10, // always exactly 10 hearts, normalized to health percentage
    SHORT,           // single heart + "current/max"
    PERCENTAGE,      // single heart + "NN%"
    GRADIENT_SHORT   // single color-shifting heart + "current/max"
}