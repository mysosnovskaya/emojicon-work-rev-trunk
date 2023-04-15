package ru.practicum.emojicon.engine;

import com.googlecode.lanterna.input.KeyStroke;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface Controller {

    void handleKey(KeyStroke key);

    void setSelection(UUID ...objectId);

    @NotNull List<UUID> getSelection();


}
