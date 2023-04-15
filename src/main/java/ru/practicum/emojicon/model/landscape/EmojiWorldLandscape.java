package ru.practicum.emojicon.model.landscape;

import com.googlecode.lanterna.TextColor;
import ru.practicum.emojicon.engine.Point;

import java.util.ArrayList;
import java.util.List;

public class EmojiWorldLandscape {

    private final int width;
    private final int height;
    private final List<List<Integer>> depthMatrix;
    private int minHeight = 255;
    private int maxHeight = 0;

    public EmojiWorldLandscape(int width, int height) {
        this.width = width;
        this.height = height;
        double factorX = Math.random() - 0.5;
        double factorY = Math.random() - 0.5;
        List<List<Integer>> dMap = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            List<Integer> hRow = new ArrayList<>();
            for (int y = 0; y < height; y++) {
                double z = landFunction(x / 1000.0, y / 1000.0, factorX, factorY);
                int zi = (int) Math.round(z * 100.0);
                minHeight = Math.min(minHeight, zi);
                maxHeight = Math.max(maxHeight, zi);
                hRow.add(zi);
            }
            dMap.add(hRow);
        }
        this.depthMatrix = dMap;
    }

    public static boolean isWaterDeep(int depth) {
        return depth < 97;
    }

    public static boolean isMountainHigh(int depth) {
        return depth > 105;
    }

    public static TextColor.RGB getLandscapeColor(int depth) {
        int red = isMountainHigh(depth) ? 255 : 0;
        int green = isWaterDeep(depth) ? 0 : !isMountainHigh(depth) ? depth / 2 : 255;
        int blue = isWaterDeep(depth) ? depth : isMountainHigh(depth) ? 255 : 0;
        return new TextColor.RGB(red, green, blue);
    }

    private static double landFunction(double kx, double ky, double ax, double ay) {
        return 1 - (ky - ay) * Math.cos(12 * kx - ay) / 9;
    }

    public static WorldLandscapeType getLandscapeType(int depth) {
        return isWaterDeep(depth) ? WorldLandscapeType.WATER : isMountainHigh(depth) ? WorldLandscapeType.MOUNTAIN : WorldLandscapeType.EARTH;
    }

    public int getDepth(int x, int y) {
        List<Integer> col = x < depthMatrix.size() ? depthMatrix.get(x) : null;
        return col != null && y < col.size() ? col.get(y) : 0;
    }


    public boolean isWater(Point pt) {
        return isWaterDeep(getDepth(pt.getX(), pt.getY()));
    }

    public boolean isMountain(Point pt) {
        return isMountainHigh(getDepth(pt.getX(), pt.getY()));
    }

    public boolean isGrass(Point pt) {
        return !(isMountain(pt) || isWater(pt));
    }
}
