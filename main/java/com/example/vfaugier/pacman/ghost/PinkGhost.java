package com.example.vfaugier.pacman.ghost;

import com.example.vfaugier.pacman.Orientation;

import java.util.ArrayList;

/**
 * Created by vfaugier on 26/06/17.
 */

public class PinkGhost extends Ghost {

    public PinkGhost(int position) {
        super(position);
    }

    @Override
    public Orientation getNextOrientation(ArrayList<Orientation> possibleOrientations, int pacManPosition, int mapWidth) {
        orientation = getRandomOrientation(possibleOrientations);
        return orientation;
    }
}
