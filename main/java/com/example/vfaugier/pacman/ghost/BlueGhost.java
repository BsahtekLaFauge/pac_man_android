package com.example.vfaugier.pacman.ghost;

import com.example.vfaugier.pacman.Orientation;
import com.example.vfaugier.pacman.ghost.Ghost;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by vfaugier on 26/06/17.
 */

public class BlueGhost extends Ghost {

    public BlueGhost(int position) {
        super(position);
    }

    @Override
    public Orientation getNextOrientation(ArrayList<Orientation> possibleOrientations, int pacManPosition, int mapWidth) {
        orientation = getRandomOrientation(possibleOrientations);
        return orientation;
    }
}
