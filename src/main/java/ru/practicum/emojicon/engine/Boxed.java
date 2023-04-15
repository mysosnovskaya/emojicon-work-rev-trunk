package ru.practicum.emojicon.engine;

public interface Boxed {
    int getLeft();
    int getRight();
    int getTop();
    int getBottom();

    default Area getArea(){
        return new Area(getLeft(), getTop(), getRight(), getBottom());
    }
}
