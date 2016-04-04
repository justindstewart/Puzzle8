package com.google.engedu.puzzle8;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

public class PuzzleBoardView extends View {
    public static final int NUM_SHUFFLE_STEPS = 40;
    private Activity activity;
    private PuzzleBoard puzzleBoard;
    private ArrayList<PuzzleBoard> animation;
    private Random random = new Random();

    public PuzzleBoardView(Context context) {
        super(context);
        activity = (Activity) context;
        animation = null;
    }

    public void initialize(Bitmap imageBitmap, View parent) {
        int width = getWidth();
        puzzleBoard = new PuzzleBoard(imageBitmap, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (puzzleBoard != null) {
            if (animation != null && animation.size() > 0) {
                puzzleBoard = animation.remove(0);
                puzzleBoard.draw(canvas);
                if (animation.size() == 0) {
                    animation = null;
                    puzzleBoard.reset();
                    Toast toast = Toast.makeText(activity, "Solved! ", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    this.postInvalidateDelayed(500);
                }
            } else {
                puzzleBoard.draw(canvas);
            }
        }
    }

    public void shuffle() {
        if (animation == null && puzzleBoard != null) {
            for(int i = 0; i < NUM_SHUFFLE_STEPS; i++) {
                ArrayList<PuzzleBoard> validMoves = new ArrayList<>(puzzleBoard.neighbours());
                puzzleBoard = validMoves.get(random.nextInt(validMoves.size()));
            }
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animation == null && puzzleBoard != null) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (puzzleBoard.click(event.getX(), event.getY())) {
                        invalidate();
                        if (puzzleBoard.resolved()) {
                            Toast toast = Toast.makeText(activity, "Congratulations!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        return true;
                    }
            }
        }
        return super.onTouchEvent(event);
    }

    public void solve() {
        PriorityQueue<PuzzleBoard> pq = new PriorityQueue<PuzzleBoard>(1000, new PriorityComparator());
        puzzleBoard.reset();
        pq.add(puzzleBoard);
        //Log.d("Debug: ", "Enter Solve State");
        while(!pq.isEmpty()) {
            //Log.d("Debug: ", "Enter While Check");
            PuzzleBoard tempBoard = pq.poll();
            if(tempBoard.resolved()) {
                //Log.d("Debug: ", "Possible Solution");
                ArrayList<PuzzleBoard> solvedList = new ArrayList<>();
                PuzzleBoard prev = tempBoard.getPreviousBoard();
                while(prev.getPreviousBoard() != null)
                {
                    solvedList.add(prev);
                }
                Collections.reverse(solvedList);
                animation = solvedList;
            }
            else {
               // Log.d("Debug: ", "No Solution");
                ArrayList<PuzzleBoard> list;
                list = tempBoard.neighbours();
                for(int i = 0; i < list.size(); i++) {
                    //Log.d("Debug: ", "Loop through adding each list.");
                    pq.add(list.get(i));
                }
            }

        }

    }

/*    class PriorityComparator implements Comparator<PuzzleBoard> {
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
    }*/
}
