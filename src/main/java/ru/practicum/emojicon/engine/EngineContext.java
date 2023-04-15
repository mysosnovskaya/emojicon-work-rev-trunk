package ru.practicum.emojicon.engine;

import com.googlecode.lanterna.screen.Screen;

public interface EngineContext {
    Screen getScreen();

    void addRoot(Drawable root);

    void removeRoot(Drawable root);

    void focusRoot();
}
