package ru.practicum.emojicon.engine;

import com.googlecode.lanterna.TerminalSize;

import java.util.Objects;

public class Point {

    private int x;
    private int y;

    public Point() {
        this(0, 0);
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(TerminalSize terminalSize) {
        this.x = terminalSize.getColumns();
        this.y = terminalSize.getRows();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Point dec(int dx, int dy) {
        return inc(-dx, -dy);
    }

    public Point inc(int dx, int dy) {
        return new Point(this.x + dx, this.y + dy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public Point move(int dx, int dy) {
        return new Point(x + dx, y + dy);
    }
}
