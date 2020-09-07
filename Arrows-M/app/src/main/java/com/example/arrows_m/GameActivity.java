package com.example.arrows_m;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.example.arrows_m.util.FirestoreDatabase;
import com.example.arrows_m.util.GameInfo;
import com.example.arrows_m.util.HomeWatcher;
import com.example.arrows_m.util.MusicService;
import com.example.arrows_m.util.SoundEffects;

public class GameActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

    private static final String TAG = "Game Activity";
    private static final int GAME_PAUSE_REQUEST = 2;
    private static String SCORE_STRING;
    private static FirestoreDatabase firestoreDatabase;

    // Music
    private boolean isBound = false;
    private MusicService musicService;
    private HomeWatcher homeWatcher;

    // Timer
    private GameTimer gameTimer;

    // Logic
    private GameLogic gameLogic;

    // Gesture
    private GestureDetectorCompat gestureDetectorCompat;

    // Swipe View
    private View swipeView;

    // Pause
    private ImageButton pauseButton;
    private AlertDialog alertDialog;

    // Sound Effects
    private SoundEffects soundEffects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        hideSystemUI();

        gameTimer = new GameTimer(this);
        soundEffects = new SoundEffects(this);
        gameLogic = new GameLogic(this, gameTimer, soundEffects);
        DetectGesture detectGesture = new DetectGesture(gameLogic);
        gestureDetectorCompat = new GestureDetectorCompat(this, detectGesture);

        pauseButton = (ImageButton) findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.pause_button:
                        pauseGame();
                        pauseIntent();
                        break;
                }
            }
        });

        swipeView = (View) findViewById(R.id.swipe_view);
        swipeView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Pass activity on touch event to the gesture detector.
                return gestureDetectorCompat.onTouchEvent(event);
            }
        });
        gameTimer.startTimer();

        bindMusicService();
        homeWatcher = new HomeWatcher(GameActivity.this);
        homeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if (musicService != null) {
                    musicService.pauseMusic();
                }
            }

            @Override
            public void onHomeLongPressed() {
                if (musicService != null) {
                    musicService.pauseMusic();
                }
            }
        });
        homeWatcher.startWatch();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!gameTimer.isTimerRunning()) {
            gameTimer.resumeTimer();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseGame();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;
        if (pm != null) {
            isScreenOn = pm.isInteractive();
        }

        if (!isScreenOn) {
            if (musicService != null) {
                musicService.pauseMusic();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();

        if (musicService != null) {
            musicService.resumeMusic();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GAME_PAUSE_REQUEST) {
            if (resultCode == RESULT_OK) {
                resumeGame();
            } else if (resultCode == RESULT_CANCELED) {
                exitGame();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
        Intent music = new Intent(GameActivity.this, MusicService.class);
        stopService(music);
        soundEffects.destroy();
        homeWatcher.stopWatch();
    }

    private void pauseGame() {
        if (gameTimer.isTimerRunning()) {
            gameTimer.pauseTimer();
        }
    }

    private void pauseIntent() {
        Intent pauseGameIntent = new Intent(GameActivity.this, PauseActivity.class);
        startActivityForResult(pauseGameIntent, GAME_PAUSE_REQUEST);
    }

    private void resumeGame() {
        if (!gameTimer.isTimerRunning()) {
            gameTimer.resumeTimer();
        }
    }

    protected void gameOver() {
        soundEffects.playGameOverEffect();
        GameResultDialog gameResultDialog = GameResultDialog.newInstance(gameLogic.getScore(), gameTimer.getTotalGamePlayTime());
        gameResultDialog.show(getSupportFragmentManager(), TAG);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Intent gameResultIntent = new Intent();
        gameResultIntent.putExtra(GameInfo.GAME_SCORE, gameLogic.getScore());
        gameResultIntent.putExtra(GameInfo.TOTAL_GAME_PLAY_TIME, gameTimer.getTotalGamePlayTime());
        setResult(RESULT_OK, gameResultIntent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exitGame();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //*****  Bind OR Unbind music service *****//

    private void bindMusicService() {
        doBindService();
        Intent music = new Intent(GameActivity.this, MusicService.class);
        startService(music);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            musicService = ((MusicService.ServiceBinder) binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
        }
    };

    void doBindService() {
        bindService(new Intent(GameActivity.this, MusicService.class),
                serviceConnection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    void doUnbindService() {
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    public void exitGame() {
        alertDialog = new AlertDialog.Builder(GameActivity.this).create();
        alertDialog.setTitle("Trying to leave in middle of game!");
        alertDialog.setMessage("Are you sure?");

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

}
