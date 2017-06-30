package com.example.vfaugier.pacman.ghost;

import com.example.vfaugier.pacman.Orientation;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by vfaugier on 19/05/17.
 */

public abstract class Ghost {

    protected int position;

    protected Orientation orientation;

    public Ghost(int position) {
        this.position = position;
        orientation = Orientation.NOT_MOVING;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public abstract Orientation getNextOrientation(ArrayList<Orientation> possibleOrientations, int pacManPosition, int mapWidth);

    protected Orientation getOrientationToFollowPacMan(ArrayList<Orientation> possibleOrientations, int pacManPosition, int mapWidth) {
        int pacManX = pacManPosition % mapWidth;
        int pacManY = pacManPosition / mapWidth;
        int ghostX = position % mapWidth;
        int ghostY = position / mapWidth;

        int xDifference = pacManX - ghostX;
        int yDifference = pacManY - ghostY;

        if (Math.abs(xDifference) > Math.abs(yDifference)) {
            if (xDifference < 0) {
                if (possibleOrientations.contains(Orientation.LEFT)) {
                    return Orientation.LEFT;
                }
            } else if (xDifference > 0) {
                if (possibleOrientations.contains(Orientation.RIGHT)) {
                    return Orientation.RIGHT;
                }
            }
        }
        if (yDifference < 0) {
            if (possibleOrientations.contains(Orientation.UP)) {
                return Orientation.UP;
            } else {
                if (xDifference < 0) {
                    if (possibleOrientations.contains(Orientation.LEFT)) {
                        return Orientation.LEFT;
                    }
                } else if (xDifference > 0) {
                    if (possibleOrientations.contains(Orientation.RIGHT)) {
                        return Orientation.RIGHT;
                    }
                }
            }
        } else if (yDifference > 0) {
            if (possibleOrientations.contains(Orientation.DOWN)) {
                return Orientation.DOWN;
            } else {
                if (xDifference < 0) {
                    if (possibleOrientations.contains(Orientation.LEFT)) {
                        return Orientation.LEFT;
                    }
                } else if (xDifference > 0) {
                    if (possibleOrientations.contains(Orientation.RIGHT)) {
                        return Orientation.RIGHT;
                    }
                }
            }
        }
        return getRandomOrientation(possibleOrientations);
    }

    protected Orientation getRandomOrientation(ArrayList<Orientation> possibleOrientations) {
        return possibleOrientations.get(ThreadLocalRandom.current().nextInt(0, possibleOrientations.size()));
    }
}
