package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;


public class PuzzleBoard {

    private static final int NUM_TILES = 3;
    private static final int DEAD_TILE = (NUM_TILES*NUM_TILES)-1;
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    private ArrayList<PuzzleTile> tiles;
    private int steps;
    private PuzzleBoard previousBoard;

    PuzzleBoard(Bitmap bitmap, int parentWidth) {
        tiles=new ArrayList<>();
        Log.d("Debug: ", "PuzzleBoard Constructor: Entered Parent Width : " + parentWidth);
        int numTile = 0;
        Bitmap sqBitmap = Bitmap.createScaledBitmap(bitmap, parentWidth, parentWidth, true);

        for(int i = 0; i < NUM_TILES; i++) {
            Log.d("Debug: ", "PuzzleBoard Constructor: Outter For Loop");
            for(int j = 0; j < NUM_TILES; j++) {
                Log.d("Debug: ", "PuzzleBoard Constructor: Inner For Loop");
                Bitmap sclBitmap = Bitmap.createBitmap(sqBitmap, (parentWidth / NUM_TILES) * j, (parentWidth / NUM_TILES) * i, (parentWidth / NUM_TILES),(parentWidth / NUM_TILES));
                Log.d("Debug: ", "PuzzleBoard Constructor: Inner For Loop: Bitmap Subset");
                PuzzleTile puzzleTile = new PuzzleTile(sclBitmap, numTile);
                numTile++;
                tiles.add(puzzleTile);
            }
        }
        tiles.set(DEAD_TILE, null);

        steps = 0;
    }

    PuzzleBoard(PuzzleBoard otherBoard) {
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
        steps = otherBoard.steps + 1;
        previousBoard = otherBoard;
    }

    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public ArrayList<PuzzleBoard> neighbours() {
        int i = 0;
        ArrayList<PuzzleBoard> validBoard = new ArrayList<>();

        while(tiles.get(i) != null) {
            ++i;
        }

        for(int j = 0; j < 4; j++) {
            int validMove = XYtoIndex(NEIGHBOUR_COORDS[j][0], NEIGHBOUR_COORDS[j][1]) + i;

            if(validMove > -1 && validMove < 9){
                PuzzleBoard copy = new PuzzleBoard(this);
                copy.swapTiles(i, validMove);
                validBoard.add(copy);
            }
        }

        return validBoard;
    }

    public int priority() {
        //Log.d("Debug:", "Priority Calculator");
        int manhattanValue = 0;
        int origX;
        int origY;
        int newX;
        int newY;

        for(int i = 0; i < NUM_TILES*NUM_TILES; i++) {
            //Log.d("Debug:", "Priority Calculator: for loop iteration: " + i);

            newX = i / NUM_TILES;
            //Log.d("Debug:", "Priority Calculator: for loop man newX: " + newX);
            newY = i % NUM_TILES;
            //Log.d("Debug:", "Priority Calculator: for loop man newY: " + newY);

            if(tiles.get(i) != null) {
                origX = tiles.get(i).getNumber() / NUM_TILES;
                //Log.d("Debug:", "Priority Calculator: for loop man tilesget: " + tiles.get(i).getNumber());
                //Log.d("Debug:", "Priority Calculator: for loop man origX: " + origX);
                origY = tiles.get(i).getNumber() % NUM_TILES;
                //Log.d("Debug:", "Priority Calculator: for loop man tilesget: " + tiles.get(i).getNumber());
                //Log.d("Debug:", "Priority Calculator: for loop man origY: " + origY);
            } else {
                origX = 8 / NUM_TILES;
                origY = 8 % NUM_TILES;
            }

            manhattanValue += Math.abs(origX - newX);
            //Log.d("Debug:", "Priority Calculator: for loop man value X: " + manhattanValue);
            manhattanValue += Math.abs(origY - newY);
            //Log.d("Debug:", "Priority Calculator: for loop man value Y: " + manhattanValue);
        }

        manhattanValue += steps;

        Log.d("Debug:", "Manhattan Value: " + manhattanValue);

        return manhattanValue;
    }

    public void setSteps (int x) {
        steps = x;
    }

    public void setPrevious () {
        previousBoard = null;
    }

    public PuzzleBoard getPreviousBoard () {
        return previousBoard;
    }

}
