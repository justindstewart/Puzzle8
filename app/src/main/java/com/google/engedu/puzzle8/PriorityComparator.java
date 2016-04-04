package com.google.engedu.puzzle8;

import java.util.Comparator;

/**
 * Created by Justin on 4/1/2016.
 */
public class PriorityComparator implements Comparator<PuzzleBoard> {

    public int compare (PuzzleBoard lhs, PuzzleBoard rhs){
        int lhsPrio = lhs.priority();
        int rhsPrio = rhs.priority();

        if (lhsPrio < rhsPrio) {
            return -1;
        }

        if (rhsPrio > lhsPrio) {
            return 1;
        }

        return 0;
    }
}
