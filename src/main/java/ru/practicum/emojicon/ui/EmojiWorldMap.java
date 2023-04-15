package ru.practicum.emojicon.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import org.jetbrains.annotations.NotNull;
import ru.practicum.emojicon.engine.*;
import ru.practicum.emojicon.model.EmojiWorld;
import ru.practicum.emojicon.model.landscape.EmojiWorldLandscape;
import ru.practicum.emojicon.model.landscape.WorldLandscapeType;

import java.util.*;
import java.util.stream.Collectors;

public class EmojiWorldMap implements Drawable, Controller {

    private final Engine engine;
    private final EmojiWorld world;

    private boolean visible = false;

    public EmojiWorldMap(Engine engine, EmojiWorld world) {
        this.engine = engine;
        this.world = world;
    }

    @Override
    public void handleKey(KeyStroke key) {
        if(key.getKeyType() == KeyType.Character && "MmЬь".contains(String.valueOf(key.getCharacter()))){
            visible = !visible;
        }
    }

    @Override
    public void setSelection(UUID... objectId) {
        //not implemented
    }

    @Override
    public @NotNull List<UUID> getSelection() {
        return Collections.emptyList(); //not implemented
    }

    @Override
    public void drawFrame(Frame someFrame) {
        if(!visible){
            return;
        }
        RootFrame frame = RootFrame.extend(someFrame);
        TerminalSize size = frame.getScreen().getTerminalSize();
        //map will be centered on any screen size with border 1
        Integer mapHeight = Math.min(size.getColumns(), size.getRows()) - 3;
        Integer mapWidth = mapHeight * 2; //console "square"
        TextGraphics graphics = frame.getScreen().newTextGraphics();

        Area area = new Area(size.getColumns() / 2 - mapWidth / 2, size.getRows() / 2 - mapHeight / 2,
                size.getColumns() / 2 + mapWidth / 2, size.getRows() / 2 + mapHeight / 2);
        area = area.move(0, -1);
        drawMap(new PixelFrame(frame, area.getLeft() + 1, area.getTop() + 1, area.getRight() - 1, area.getBottom() - 1));
        //draw box frame
        graphics.drawLine(area.getLeft(), area.getTop(), area.getRight(), area.getTop(), '─');
        graphics.drawLine(area.getLeft(), area.getBottom(), area.getRight(), area.getBottom(), '─');
        graphics.drawLine(area.getLeft(), area.getTop() + 1, area.getLeft(), area.getBottom() - 1, '│');
        graphics.drawLine(area.getRight(), area.getTop() + 1, area.getRight(), area.getBottom() - 1, '│');
        graphics.putString(area.getLeft(), area.getTop(), "┌");
        graphics.putString(area.getLeft(), area.getBottom(), "└");
        graphics.putString(area.getRight(), area.getTop(), "┐");
        graphics.putString(area.getRight(), area.getBottom(), "┘");

        String mapTitle = "═[ КАРТА ]═";
        graphics.putString(size.getColumns() / 2 - mapTitle.length() / 2, area.getTop(), mapTitle);
    }

    private void drawMap(PixelFrame frame) {
        frame.setFillColor(TextColor.ANSI.BLACK);
        frame.fill();
        int dc = world.getWidth() / frame.getArea().getWidth();
        for (int x = frame.getLeft(); x <= frame.getRight(); x++){
            for (int y = frame.getTop(); y <= frame.getBottom(); y++) {
                frame.setPosition(x, y);
                TextColor depthColor = getWorldMeanDepthColor(dc * x, dc * y, dc);
                frame.setColor(depthColor);
                frame.paint();
            }
        }
        world.getSelection().stream().findFirst().ifPresent(entityId -> engine.findEntity(entityId).filter(e -> e instanceof Boxed).map(e -> (Boxed) e).ifPresent(entity -> {
            frame.setPosition(entity.getLeft() / dc, entity.getTop() / dc);
            frame.setColor(TextColor.ANSI.GREEN_BRIGHT);
            frame.paint();
        }));
    }

    private TextColor getWorldMeanDepthColor(int x0, int y0, int dc) {
        Map<WorldLandscapeType, Integer> depthMap = new HashMap<>();
        for(int x = x0; x < x0+dc; x++){
            for(int y = y0; y < y0+dc; y++){
                int depth = world.getLandscape().getDepth(x, y);
                WorldLandscapeType typ = EmojiWorldLandscape.getLandscapeType(depth);
                depthMap.put(typ, depthMap.getOrDefault(typ, 0) + 1);
            }
        }
        List<Map.Entry<WorldLandscapeType, Integer>> depthStats = depthMap.entrySet().stream().sorted(Comparator.<Map.Entry<WorldLandscapeType, Integer>>comparingInt(e -> e.getValue()).reversed()).collect(Collectors.toList());
        WorldLandscapeType max = depthStats.get(0).getKey();
        WorldLandscapeType second = depthStats.size() > 1 ? depthStats.get(1).getKey() : null;
        TextColor meanColor = max.getBaseColor();
        if(second != null && max == WorldLandscapeType.EARTH) {
            if(second == WorldLandscapeType.MOUNTAIN){
                meanColor = new TextColor.RGB(0, meanColor.getGreen() + 10, 0);
            } else if (second == WorldLandscapeType.WATER){
                meanColor = new TextColor.RGB(0, meanColor.getGreen() - 10, 0);
            }
        }
        return meanColor;
    }
}
