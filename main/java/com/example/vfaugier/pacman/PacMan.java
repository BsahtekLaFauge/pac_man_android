package com.example.vfaugier.pacman;

/**
 * Created by vfaugier on 19/05/17.
 */

public class PacMan {

    private int position;
    private Orientation currentOrientation;
    private Orientation nextOrientation;

    public PacMan(int position) {
        this.position = position;
        currentOrientation = Orientation.NOT_MOVING;
        nextOrientation = Orientation.NOT_MOVING;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Orientation getCurrentOrientation() {
        return currentOrientation;
    }

    public void setCurrentOrientation(Orientation orientation) {
        this.currentOrientation = orientation;
    }

    public Orientation getNextOrientation() {
        return nextOrientation;
    }

    public void setNextOrientation(Orientation orientation) {
        nextOrientation = orientation;
    }
}
