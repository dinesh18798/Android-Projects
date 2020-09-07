package com.example.arrows_m;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;

import com.example.arrows_m.util.DatabaseField;
import com.example.arrows_m.util.FirestoreDatabase;
import com.example.arrows_m.util.GameInfo;
import com.example.arrows_m.util.HomeWatcher;
import com.example.arrows_m.util.MusicService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";
    private static final int GAME_PLAY_REQUEST = 1;
    protected FirestoreDatabase firestoreDatabase;
    private AlertDialog alertDialog;

    private boolean isBound = false;
    private MusicService musicService;
    private HomeWatcher homeWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideSystemUI();

        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.main_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        firestoreDatabase = new FirestoreDatabase(this);
        if (GameInfo.USER_SIGNED_IN) {
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                firestoreDatabase.checkPlayer(acct);
            }
        }

        bindMusicService();
        homeWatcher = new HomeWatcher(MainActivity.this);
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

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
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
    protected void onPause() {
        super.onPause();

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
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
        Intent music = new Intent(MainActivity.this, MusicService.class);
        stopService(music);
        homeWatcher.stopWatch();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exitApplication();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GAME_PLAY_REQUEST) {
            if (resultCode == RESULT_OK) {
                int score = data.getIntExtra(GameInfo.GAME_SCORE, 0);
                long totalTime = data.getLongExtra(GameInfo.TOTAL_GAME_PLAY_TIME, 0);

                if (GameInfo.USER_SIGNED_IN)
                    firestoreDatabase.updatePlayerData(score, totalTime);
                else {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(GameInfo.GAME_SCORE, score);
                    editor.putLong(GameInfo.TOTAL_GAME_PLAY_TIME, totalTime);
                    editor.apply();
                }
            }
        }
    }

    public void playGame(View view) {
        Intent playGameIntent = new Intent(MainActivity.this, GameActivity.class);
        startActivityForResult(playGameIntent, GAME_PLAY_REQUEST);
    }

    public void settingsMenu(View view) {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void leaderBoardActivity(View view) {
        Intent leaderBoardIntent = new Intent(MainActivity.this, LeaderBoardActivity.class);
        leaderBoardIntent.putExtra(DatabaseField.SCORE, firestoreDatabase.getExistScore());
        leaderBoardIntent.putExtra(DatabaseField.TOTAL_GAME_PLAY_TIME, firestoreDatabase.getExistTotalGamePlayTime());
        startActivity(leaderBoardIntent);
    }

    public void helpActivity(View view) {
        Intent helpIntent = new Intent(MainActivity.this, HelpActivity.class);
        startActivity(helpIntent);
    }

    public void exitGame(View view) {
        exitApplication();
    }

    public void exitApplication() {
        alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Trying to leave!");
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

    public void shareApplication(View view) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        String shareBody = "Hey! This is new game called Arrow-M. " +
                "Install in your phone and try to play it. Download from " +
                "https://dinesh18798.itch.io/arrows-m";
        String shareSubject = "Try Arrow-M Game";

        share.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
        share.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(share, "Share Using"));
    }


    //*****  Bind OR Unbind music service *****//

    private void bindMusicService() {
        doBindService();
        Intent music = new Intent(MainActivity.this, MusicService.class);
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
        bindService(new Intent(MainActivity.this, MusicService.class),
                serviceConnection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    void doUnbindService() {
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }
}
