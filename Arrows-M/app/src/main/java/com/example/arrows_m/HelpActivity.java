package com.example.arrows_m;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arrows_m.util.HomeWatcher;
import com.example.arrows_m.util.MusicService;

public class HelpActivity extends AppCompatActivity {

    private ImageButton returnButton;

    private boolean isBound = false;
    private MusicService musicService;
    private HomeWatcher homeWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        hideSystemUI();

        returnButton = (ImageButton) findViewById(R.id.help_return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.help_return_button:
                        finish();
                        break;
                }
            }
        });

        bindMusicService();
        homeWatcher = new HomeWatcher(HelpActivity.this);
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
        Intent music = new Intent(HelpActivity.this, MusicService.class);
        stopService(music);
        homeWatcher.stopWatch();
    }


    //*****  Bind OR Unbind music service *****//

    private void bindMusicService() {
        doBindService();
        Intent music = new Intent(HelpActivity.this, MusicService.class);
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
        bindService(new Intent(HelpActivity.this, MusicService.class),
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
