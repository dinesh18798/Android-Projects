package com.example.arrows_m;

import com.example.arrows_m.util.Swipe.SwipeMovement;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class DetectGesture extends GestureDetector.SimpleOnGestureListener {

    private static final String TAG = "Detect Gesture";
    public static final int SWIPE_THRESHOLD = 100;
    public static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private GameLogic gameLogic;

    private SwipeMovement currentSwipe;

    public DetectGesture(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    @Override
    public boolean onFling(MotionEvent downMotion, MotionEvent moveMotion, float velocityX, float velocityY) {
        boolean result = false;
        float diffX = moveMotion.getX() - downMotion.getX();
        float diffY = moveMotion.getY() - downMotion.getY();

        if (Math.abs(diffX) > Math.abs(diffY)) {
            // swipe right of left
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    //right
                    this.currentSwipe = SwipeMovement.MOVE_RIGHT;
                } else {
                    //left
                    this.currentSwipe = SwipeMovement.MOVE_LEFT;
                }
                result = true;
            }
        } else {
            // swipe up or down
            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    //down
                    this.currentSwipe = SwipeMovement.MOVE_DOWN;
                } else {
                    //up
                    this.currentSwipe = SwipeMovement.MOVE_UP;
                }
                result = true;
            }
        }
        gameLogic.onValidateSwipe(this.currentSwipe);
        return result;
    }
}
