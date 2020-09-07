package com.example.arrows_m;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.SeekBar;

import com.example.arrows_m.util.GameInfo;
import com.example.arrows_m.util.HomeWatcher;
import com.example.arrows_m.util.MusicService;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "Settings Activity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Button signOutButton;
    private RadioButton musicOnButton;
    private RadioButton musicOffButton;
    private SeekBar volumeSeekBar;
    private ImageButton returnButton;

    private boolean isBound = false;
    private MusicService musicService;
    private HomeWatcher homeWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        hideSystemUI();

        if (GameInfo.USER_SIGNED_IN) {
            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        Intent intent = new Intent(SettingsActivity.this, SignInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);
                        finish();
                    }
                }
            };
        }

        signOutButton = (Button) findViewById(R.id.backToMainMenu_button);
        signOutButton.setEnabled(GameInfo.USER_SIGNED_IN);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });

        returnButton = (ImageButton) findViewById(R.id.settings_return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.settings_return_button:
                        finish();
                        break;
                }
            }
        });

        musicOnButton = (RadioButton) findViewById(R.id.musicOn_radioButton);
        musicOffButton = (RadioButton) findViewById(R.id.musicOff_radioButton);
        volumeSeekBar = (SeekBar) findViewById(R.id.volume_seekBar);

        if(GameInfo.GAME_MUSIC_STATUS) musicOnButton.setChecked(true);
        else musicOffButton.setChecked(true);

        volumeSeekBar.setMax(GameInfo.GAME_MUSIC_VOLUME_MAX);
        volumeSeekBar.setProgress(GameInfo.GAME_MUSIC_VOLUME);
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                GameInfo.GAME_MUSIC_VOLUME = seekBar.getProgress();
                musicService.setVolume();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                return;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                return;
            }
        });

        bindMusicService();
        homeWatcher = new HomeWatcher(SettingsActivity.this);
        homeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if(musicService != null) {
                    musicService.pauseMusic();
                }
            }

            @Override
            public void onHomeLongPressed() {
                if(musicService != null) {
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
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (GameInfo.USER_SIGNED_IN) {
            mAuth.addAuthStateListener(mAuthListener);
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
        Intent music = new Intent(SettingsActivity.this, MusicService.class);
        stopService(music);
        homeWatcher.stopWatch();
    }

    public void onMusicRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.musicOn_radioButton:
                if (checked) {
                    musicService.startMusic();
                    GameInfo.GAME_MUSIC_STATUS = true;
                }
                break;
            case R.id.musicOff_radioButton:
                if (checked) {
                    musicService.stopMusic();
                    GameInfo.GAME_MUSIC_STATUS = false;
                }
                break;
        }
    }

    //*****  Bind OR Unbind music service *****//

    private void bindMusicService() {
        doBindService();
        Intent music = new Intent(SettingsActivity.this, MusicService.class);
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
        bindService(new Intent(SettingsActivity.this, MusicService.class),
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
