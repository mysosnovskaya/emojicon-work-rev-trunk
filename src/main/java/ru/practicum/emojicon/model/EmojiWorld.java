package ru.practicum.emojicon.model;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.practicum.emojicon.engine.*;
import ru.practicum.emojicon.model.landscape.EmojiWorldLandscape;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EmojiWorld extends EmojiObject implements EntityResolver, EmojiObjectHolder, Controller {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final List<EmojiWorldObject> objects = new ArrayList<>();
    private UUID selection = null;
    private EmojiWorldLandscape landscape;

    public EmojiWorld() {
        this.initEarth(2048, 2048);
        log.info("world created");
    }


    private void initEarth(int width, int height) {
        this.setWidth(width);
        this.setHeight(height);
        this.landscape = new EmojiWorldLandscape(width, height);
    }

    @Override
    public void drawFrame(Frame frame) {
        drawEarth(frame);
        drawObjects(frame);
    }

    private void drawObjects(Frame frame) {
        ((RootFrame) frame.getRoot()).setTransparentColorFn((x, y) -> {
            int depth = landscape.getDepth(x + frame.getLeft(), y + frame.getTop());
            return EmojiWorldLandscape.getLandscapeColor(depth);
        });
        //отсекаем лишние объекты, которые точно не отобразятся
        objects.stream()
                .filter(obj -> frame.getLeft() <= obj.getLeft() && frame.getRight() >= obj.getRight() && frame.getTop() <= obj.getTop() && frame.getBottom() >= obj.getBottom())
                .forEach(obj -> {
                    Point dp = new Point(obj.getX(), obj.getY());
                    TranslatedFrame objFrame = new TranslatedFrame(frame, dp);
                    obj.drawFrame(objFrame);
                });
    }

    private void drawEarth(Frame frame) {
        for (int x = Math.max(0, frame.getLeft()); x <= Math.min(getWidth(), frame.getRight()); x++) {
            for (int y = Math.max(0, frame.getTop()); y <= Math.min(getHeight(), frame.getBottom()); y++) {
                frame.setPosition(x, y);
                int depth = landscape.getDepth(x, y);
                frame.setFillColor(EmojiWorldLandscape.getLandscapeColor(depth));
                frame.paint();
            }
        }
    }

    @Override
    public UUID addObject(EmojiObject obj, Point position) {
        EmojiWorldObject wobj = new EmojiWorldObject(this, obj, position);
        addWorldObject(wobj);
        return wobj.getId();
    }

    @Override
    public boolean isFreeArea(int left, int top, int right, int bottom) {
        Set<Point> landscapeHardPoints = new Area(left, top, right, bottom).getCorners().stream().filter(p -> landscape.isWater(p) || landscape.isMountain(p)).collect(Collectors.toSet());
        return left >= 0 && top >= 0 && right <= getWidth() && bottom <= getHeight() && landscapeHardPoints.isEmpty();
    }

    private void addWorldObject(EmojiWorldObject obj) {
        objects.add(obj);
    }

    private static final Function<KeyStroke, Boolean> ARROW_UP_FN = key -> key.getKeyType() == KeyType.ArrowUp || (key.getKeyType() == KeyType.Character && "wWцЦ".contains(String.valueOf(key.getCharacter())));

    private static final Function<KeyStroke, Boolean> ARROW_DOWN_FN = key -> key.getKeyType() == KeyType.ArrowDown || (key.getKeyType() == KeyType.Character && "sSыЫ".contains(String.valueOf(key.getCharacter())));

    private static final Function<KeyStroke, Boolean> ARROW_LEFT_FN = key -> key.getKeyType() == KeyType.ArrowLeft || (key.getKeyType() == KeyType.Character && "aAфФ".contains(String.valueOf(key.getCharacter())));

    private static final Function<KeyStroke, Boolean> ARROW_RIGHT_FN = key -> key.getKeyType() == KeyType.ArrowRight || (key.getKeyType() == KeyType.Character && "dDвВ".contains(String.valueOf(key.getCharacter())));

    @Override
    public void handleKey(KeyStroke key) {
        objects.stream().filter(obj -> obj.getId().equals(selection)).filter(obj -> obj instanceof Controllable).map(obj -> (Controllable) obj).forEach(obj -> {
            Point pt = null;
            if (ARROW_DOWN_FN.apply(key)) {
                pt = new Point(0, 1);
            } else if (ARROW_LEFT_FN.apply(key)) {
                pt = new Point(-1, 0);
            } else if (ARROW_RIGHT_FN.apply(key)) {
                pt = new Point(1, 0);
            } else if (ARROW_UP_FN.apply(key)) {
                pt = new Point(0, -1);
            }
            if (pt != null) {
                obj.move(pt);
            }
        });
    }

    @Override
    public void setSelection(UUID... objectId) {
        this.selection = objectId[0];
    }

    @Override
    public List<UUID> getSelection() {
        return selection != null ? List.of(selection) : Collections.emptyList();
    }

    @Override
    public Optional<? extends Entity> findEntity(UUID uuid) {
        return objects.stream().filter(obj -> obj.getId().equals(uuid)).findFirst(); //TODO заменить на Map и поиск по ключу
    }

    public Area getFreeArea() {
        Point lt = null;
        Point rb = null;
        while (lt == null) { //но может и зациклиться :)
            int rx = (int) Math.round(Math.random() * getWidth());
            int ry = (int) Math.round(Math.random() * getHeight());
            lt = new Point(rx, ry);
            if (landscape.isGrass(lt)) {
                int square = 100;
                while (lt != null && rb == null && square > 0) {
                    rb = new Point(rx + square, ry + square);
                    if ((landscape.isGrass(rb))) {
                        return new Area(lt, rb);
                    } else if (square > 1) {
                        square--;
                        rb = null;
                    } else {
                        lt = null;
                    }
                }
            } else {
                lt = null;
            }
        }
        return null;
    }
}
