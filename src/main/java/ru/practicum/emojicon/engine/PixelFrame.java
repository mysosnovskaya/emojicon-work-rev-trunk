package ru.practicum.emojicon.engine;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.vdurmont.emoji.Emoji;

import java.util.HashMap;
import java.util.Map;

public class PixelFrame implements Frame {
    private final RootFrame rootFrame;
    private final int rootLeft;
    private final int rootTop;
    private final int rootRight;
    private final int rootBottom;

    private final int rootDx;

    private final int rootDy;
    private final int left;
    private final int top;
    private final int right;
    private final int bottom;

    private int posX;

    private int posY;

    private int rootPosX;

    private int rootPosY;

    private int rootSubpixel = 0;

    private TextColor backgroundColor = TextColor.ANSI.BLACK;

    private TextColor color = TextColor.ANSI.WHITE_BRIGHT;

    Map<Point, TextColor> subPixelMap = new HashMap<>();

    @Override
    public int getLeft() {
        return left;
    }

    @Override
    public int getTop() {
        return top;
    }

    @Override
    public int getRight() {
        return right;
    }

    @Override
    public int getBottom() {
        return bottom;
    }

    public PixelFrame(RootFrame rootFrame, int rootLeft, int rootTop, int rootRight, int rootBottom) {
        this.rootFrame = rootFrame;
        this.rootLeft = rootLeft;
        this.rootTop = rootTop;
        this.rootRight = rootRight;
        this.rootBottom = rootBottom;
        this.rootDx = rootLeft;
        this.rootDy = rootTop;
        this.left = 0;
        this.top = 0;
        this.right = rootRight - rootLeft + 1;
        this.bottom = 2 * (rootBottom - rootTop + 1); //console subpixel
        setPosition(0, 0);
    }

    @Override
    public int getPosX() {
        return posX;
    }

    @Override
    public int getPosY() {
        return posY;
    }

    @Override
    public TextColor getTransparentColor() {
        return backgroundColor;
    }

    @Override
    public TextColor getFillColor() {
        return backgroundColor;
    }

    @Override
    public TextColor getColor() {
        return color;
    }

    @Override
    public void setPosition(int x, int y) {
        this.posX = x;
        this.posY = y;
        this.rootPosX = x + rootDx;
        this.rootPosY = y / 2 + rootDy;
        this.rootSubpixel = y % 2;
        rootFrame.setPosition(this.rootPosX, this.rootPosY);
    }

    @Override
    public void setFillColor(TextColor fillColor) {
        rootFrame.setFillColor(backgroundColor);
        this.backgroundColor = fillColor;
    }

    @Override
    public void setColor(TextColor color) {
        rootFrame.setColor(color);
        this.color = color;
    }

    @Override
    public Point paint() {
        TextColor oldForegroundColor = color;
        TextColor oldBackgroundColor = backgroundColor;
        Point subpixelPt = new Point(posX, posY);
        TextColor subpixelUp;
        TextColor subpixelDown;
        char sym;
        if(rootSubpixel == 0){ //upper subpixel
            subpixelUp = subPixelMap.get(subpixelPt);
            subpixelDown = subPixelMap.get(subpixelPt.move(0, 1));
            sym = '▀';
        } else {
            subpixelDown = subPixelMap.get(subpixelPt);
            subpixelUp = subPixelMap.get(subpixelPt.move(0, -1));
            sym = '▄';
        }
        subPixelMap.put(subpixelPt, color);
        if (subpixelUp != null && subpixelDown != null){
            if(rootSubpixel == 0){
                rootFrame.setColor(color);
                rootFrame.setFillColor(subpixelDown);
            } else {
                rootFrame.setColor(color);
                rootFrame.setFillColor(subpixelUp);
            }
        } else if (subpixelUp != null && rootSubpixel == 1){ //bottom part with existing upper part
            rootFrame.setFillColor(subpixelUp);
            rootFrame.setColor(color);
        } else if (subpixelDown != null && rootSubpixel == 0) { //upper part with existing bottom part
            rootFrame.setFillColor(subpixelDown);
            rootFrame.setColor(color);
        }
        rootFrame.draw(sym);
        rootFrame.setFillColor(oldBackgroundColor);
        rootFrame.setColor(oldForegroundColor);
        return new Point(1, 1);
    }

    @Override
    public Point draw(Character character) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public Point draw(Emoji emoji) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public Frame getRoot() {
        return rootFrame;
    }

    public void fill() {
        rootFrame.setColor(backgroundColor);
        for (int x = rootLeft; x <= rootRight; x++){
            for(int y = rootTop; y <= rootBottom; y++){
                rootFrame.setPosition(x, y);
                rootFrame.paint();
            }
        }
        rootFrame.setColor(color);
    }
}
