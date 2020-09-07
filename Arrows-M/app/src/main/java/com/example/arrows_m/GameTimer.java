package com.example.arrows_m;

import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;

public class GameTimer {

    private static final String TAG = "Game Timer";
    private GameActivity gameActivity;

    // Timer
    private static long COUNTDOWN_TIMER_IN_MILLIS = 60000;
    private static long COUNTDOWN_INTERVAL_IN_MILLIS = 10;
    private static long INCREASE_TIME_IN_MILLIS = 5000;
    private static long REDUCE_TIME_IN_MILLIS = 20000;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = COUNTDOWN_TIMER_IN_MILLIS;

    private ProgressBar timerProgressBar;
    private TextView timerTextView;
    private boolean timerRunning = false;
    private long totalGamePlayTimeinMillis = COUNTDOWN_TIMER_IN_MILLIS;

    public GameTimer(GameActivity activity) {
        this.gameActivity = activity;
        this.timerProgressBar = (ProgressBar) gameActivity.findViewById(R.id.timer_progressBar);
        timerProgressBar.setProgress(100);
        timerTextView = (TextView) gameActivity.findViewById(R.id.timer_textView);
    }

    protected void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, COUNTDOWN_INTERVAL_IN_MILLIS) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerStatus();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                timerProgressBar.setProgress(0);
                timerTextView.setText("00:00");
                gameActivity.gameOver();
            }
        }.start();
        timerRunning = true;
    }

    private void updateTimerStatus() {
        double timeDiff = (double) timeLeftInMillis / COUNTDOWN_TIMER_IN_MILLIS;
        int progress = (int) (timeDiff * 100);
        timerProgressBar.setProgress(progress);
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timerTextFormat = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerTextView.setText(timerTextFormat);
    }

    protected void reduceTime() {
        countDownTimer.cancel();
        timeLeftInMillis -= REDUCE_TIME_IN_MILLIS;
        totalGamePlayTimeinMillis -= REDUCE_TIME_IN_MILLIS;
        startTimer();
    }

    protected void increaseTime() {
    if(timeLeftInMillis < COUNTDOWN_TIMER_IN_MILLIS - INCREASE_TIME_IN_MILLIS) {
            countDownTimer.cancel();
            timeLeftInMillis += INCREASE_TIME_IN_MILLIS;
            totalGamePlayTimeinMillis += INCREASE_TIME_IN_MILLIS;
            startTimer();
        }
    }

    protected  void pauseTimer() {
        timerRunning = false;
        countDownTimer.cancel();
    }


    protected void resumeTimer() {
        timerRunning = true;
        startTimer();
    }

    protected boolean isTimerRunning() {return timerRunning;}

    protected long getTotalGamePlayTime() {return totalGamePlayTimeinMillis;}
}
