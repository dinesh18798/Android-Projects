package com.example.arrows_m;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.arrows_m.best_player.BestPlayerAdapter;
import com.example.arrows_m.best_player.BestPlayerObject;
import com.example.arrows_m.util.GameInfo;
import com.example.arrows_m.util.Conversion;
import com.example.arrows_m.util.DatabaseField;
import com.example.arrows_m.util.HomeWatcher;
import com.example.arrows_m.util.MusicService;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class LeaderBoardActivity extends AppCompatActivity {

    private static final String TAG = "Leader Board Activity";
    private static String userID = null;
    private ListView bestWorldScoreListView;
    private TextView bestScoreTextView;
    private TextView bestGamePlayTimeTextView;
    private ImageButton returnButton;

    private FirebaseFirestore firebaseFirestore = null;
    private ArrayList<BestPlayerObject> bestPlayerObjectList;
    private BestPlayerAdapter playerAdapter;

    private boolean isBound = false;
    private MusicService musicService;
    private HomeWatcher homeWatcher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        hideSystemUI();

        bestWorldScoreListView = (ListView) findViewById(R.id.bestWorldScore_listView);
        bestScoreTextView = (TextView) findViewById(R.id.bestScore_textView);
        bestGamePlayTimeTextView = (TextView) findViewById(R.id.bestGamePlayTime_textView);

        returnButton = (ImageButton) findViewById(R.id.leader_return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.leader_return_button:
                        finish();
                        break;
                }
            }
        });

        firebaseFirestore = FirebaseFirestore.getInstance();
        bestPlayerObjectList = new ArrayList<>();

        getPlayerData();

        playerAdapter = new BestPlayerAdapter(this, R.layout.player_listitem, bestPlayerObjectList);
        bestWorldScoreListView.setAdapter(playerAdapter);

        CollectionReference collectionReference = firebaseFirestore.collection(DatabaseField.COLLECTION_NAME);
        collectionReference.orderBy(DatabaseField.SCORE, Query.Direction.DESCENDING).limit(30).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                bestPlayerObjectList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    if (doc.get(DatabaseField.DISPLAY_NAME) != null && doc.get(DatabaseField.SCORE) != null) {
                        String name = doc.getString(DatabaseField.DISPLAY_NAME);
                        int score = doc.getLong(DatabaseField.SCORE).intValue();
                        BestPlayerObject playerObject = new BestPlayerObject(name, score);
                        bestPlayerObjectList.add(playerObject);
                        playerAdapter.notifyDataSetChanged();
                    }
                }

            }
        });

        bindMusicService();
        homeWatcher = new HomeWatcher(LeaderBoardActivity.this);
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

    private void getPlayerData() {
        if (GameInfo.USER_SIGNED_IN) {
            bestScoreTextView.setText(String.valueOf(getIntent().getIntExtra(DatabaseField.SCORE, 0)));
            long time = getIntent().getLongExtra(DatabaseField.TOTAL_GAME_PLAY_TIME, 0);
            bestGamePlayTimeTextView.setText(Conversion.ConvertMilliToString(time));
        } else {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            bestScoreTextView.setText(String.valueOf(preferences.getInt(DatabaseField.SCORE, 0)));
            long time = preferences.getLong(DatabaseField.TOTAL_GAME_PLAY_TIME, 0);
            bestGamePlayTimeTextView.setText(Conversion.ConvertMilliToString(time));
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
        Intent music = new Intent(LeaderBoardActivity.this, MusicService.class);
        stopService(music);
        homeWatcher.stopWatch();
    }


    //*****  Bind OR Unbind music service *****//

    private void bindMusicService() {
        doBindService();
        Intent music = new Intent(LeaderBoardActivity.this, MusicService.class);
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
        bindService(new Intent(LeaderBoardActivity.this, MusicService.class),
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
