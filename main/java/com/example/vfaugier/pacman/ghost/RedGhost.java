package com.example.vfaugier.pacman.ghost;

import com.example.vfaugier.pacman.Orientation;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by vfaugier on 09/06/17.
 */

public class RedGhost extends Ghost {

    private static final int OMEGA_CARDINAL = 3;
    private static final int NOT_TURNING_CARDINAL = 2;

    public RedGhost(int position) {
        super(position);
    }

    @Override
    public Orientation getNextOrientation(ArrayList<Orientation> possibleOrientations, int pacManPosition, int mapWidth) {
        if (possibleOrientations.contains(orientation)) {
            if (possibleOrientations.size() < 3) {
                return orientation;
            } else {
                int randomNum = ThreadLocalRandom.current().nextInt(0, OMEGA_CARDINAL);
                if (randomNum + 1 <= NOT_TURNING_CARDINAL) {
                    return orientation;
                } else {
                    orientation = getOrientationToFollowPacMan(possibleOrientations, pacManPosition, mapWidth);
                    return orientation;
                }
            }
        } else {
            orientation = getOrientationToFollowPacMan(possibleOrientations, pacManPosition, mapWidth);
            return orientation;
        }
    }
}
