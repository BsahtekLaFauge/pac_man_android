package com.example.vfaugier.pacman.ghost;

import com.example.vfaugier.pacman.Orientation;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by vfaugier on 26/06/17.
 */

public class YellowGhost extends Ghost {

    public YellowGhost(int position) {
        super(position);
    }

    @Override
    public Orientation getNextOrientation(ArrayList<Orientation> possibleOrientations, int pacManPosition, int mapWidth) {
        orientation = getOrientationToFollowPacMan(possibleOrientations, pacManPosition, mapWidth);
        return orientation;
    }
}
