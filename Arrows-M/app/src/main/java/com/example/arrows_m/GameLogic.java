package com.example.arrows_m;

import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arrows_m.arrow.ArrowGridDisplay;
import com.example.arrows_m.util.GameInfo;
import com.example.arrows_m.util.SoundEffects;
import com.example.arrows_m.util.Swipe;

public class GameLogic {

    private static String SCORE_STRING;
    private SoundEffects soundEffects;
    private GameActivity gameActivity;

    // Timer
    private GameTimer gameTimer;

    // Arrow Grid
    private GridView arrowGrid;
    private ArrowGridDisplay arrowGridDisplay;

    // Score
    private int score;
    private TextView scoreTextView;

    private int matchedCount = 0;
    private int swipeCount = 0;
    private int defaultScore = 10;

    public GameLogic(GameActivity activity, GameTimer timer, SoundEffects soundEffects) {
        this.gameActivity = activity;
        this.gameTimer = timer;
        this.soundEffects = soundEffects;

        arrowGrid = (GridView) gameActivity.findViewById(R.id.arrows_grid);
        arrowGridDisplay = new ArrowGridDisplay(gameActivity);

        scoreTextView = (TextView) gameActivity.findViewById(R.id.score_textView);
        SCORE_STRING = gameActivity.getString(R.string.score);
        score = 0;
    }

    public void onValidateSwipe(Swipe.SwipeMovement swipeMovement) {
        String swipeType = "Nothing";
        switch (swipeMovement) {
            case MOVE_RIGHT:
                swipeType = "Right";
                break;
            case MOVE_LEFT:
                swipeType = "Left";
                break;
            case MOVE_UP:
                swipeType = "Up";
                break;
            case MOVE_DOWN:
                swipeType = "Down";
                break;
        }
        CompareWithPattern(swipeCount++, swipeType);
    }

    private void CompareWithPattern(int mCount, String buttonType) {

        if (gameTimer.isTimerRunning()) {
            if (arrowGrid.getAdapter().getCount() > mCount) {
                ViewGroup gridChild = (ViewGroup) arrowGrid.getChildAt(mCount);
                int childSize = gridChild.getChildCount();
                for (int k = 0; k < childSize; k++) {
                    if (gridChild.getChildAt(k) instanceof ImageView) {
                        ImageView arrowPattern = (ImageView) gridChild.getChildAt(k);
                        int tag = String.valueOf(arrowPattern.getTag()).toLowerCase().hashCode();
                        int type = buttonType.toLowerCase().hashCode();

                        if (tag == type) {
                            soundEffects.playSwipeEffect();
                            matchedCount++;
                            int nextArrowIndex = mCount + 1;
                            if (nextArrowIndex < arrowGridDisplay.getCurrentNoOfArrowObjects())
                                arrowGridDisplay.highlightArrowObject(nextArrowIndex);
                        } else {
                            // Set the back to initial position (highlight index 0)
                            // Reduce timer 10 seconds
                            // Set matchedCount and swipeCount to 0
                            soundEffects.playWrongEffect();
                            gameTimer.reduceTime();
                            matchedCount = 0;
                            swipeCount = 0;
                            arrowGridDisplay.resetHighlightArrowObject(mCount);
                        }
                    }
                }
            }
            if (matchedCount == arrowGrid.getAdapter().getCount()) {
                updateScore();
                gameTimer.increaseTime();
                arrowGridDisplay.setCurrentNoOfArrowObjects(nextNoOfArrowObjects());
                arrowGridDisplay.refreshArrowGrid();
                swipeCount = 0;
                matchedCount = 0;
            }
        }
    }

    private int nextNoOfArrowObjects() {
        int num = arrowGridDisplay.getCurrentNoOfArrowObjects();
        if (num == GameInfo.GAME_LEVEL_MAX) return num;
        if ((score / defaultScore) / num == defaultScore)
            num++;
        return num;
    }

    private void updateScore() {
        score += arrowGridDisplay.getCurrentNoOfArrowObjects() * defaultScore;
        scoreTextView.setText(String.format("%s: %d", SCORE_STRING, score));
    }

    public int getScore() {
        return score;
    }
}
