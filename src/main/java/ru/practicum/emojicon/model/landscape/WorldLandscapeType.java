package ru.practicum.emojicon.model.landscape;

import com.googlecode.lanterna.TextColor;

public enum WorldLandscapeType {
    WATER(new TextColor.RGB(0, 0, 255)), EARTH(new TextColor.RGB(0, 100, 0)), MOUNTAIN(new TextColor.RGB(255, 255, 255));

    private final TextColor baseColor;

    public TextColor getBaseColor() {
        return baseColor;
    }

    WorldLandscapeType(TextColor baseColor) {
        this.baseColor = baseColor;
    }
}
