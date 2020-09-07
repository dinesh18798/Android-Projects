package com.example.arrows_m.util;

import android.media.AudioAttributes;
import android.media.SoundPool;

import com.example.arrows_m.GameActivity;
import com.example.arrows_m.R;

public class SoundEffects {

    private static final int MAX_MUSIC_EFFECTS = 3;
    private static final float PLAYBACK_RATE = 1.0f;
    private static final int PRIORITY = 2;
    private static final int LOOP = 0;

    private GameActivity gameActivity;
    private SoundPool soundPool;
    private float currentVol;
    private int swipe_effect;
    private int wrong_effect;
    private int gameOver_effect;

    private int streamID = 0;

    public SoundEffects(GameActivity gameActivity) {

        this.gameActivity = gameActivity;

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(MAX_MUSIC_EFFECTS)
                .setAudioAttributes(audioAttributes)
                .build();

        swipe_effect = soundPool.load(gameActivity, R.raw.swipe, PRIORITY);
        wrong_effect = soundPool.load(gameActivity, R.raw.wrong, PRIORITY);
        gameOver_effect = soundPool.load(gameActivity, R.raw.game_over, PRIORITY);
    }

    public void playSwipeEffect() {
        if (GameInfo.GAME_MUSIC_STATUS) {
            soundPool.stop(streamID);
            currentVol = Conversion.CovertMusicVolume();
            streamID = soundPool.play(swipe_effect, currentVol, currentVol, PRIORITY, LOOP, PLAYBACK_RATE);
        }
    }

    public void playWrongEffect() {
        if (GameInfo.GAME_MUSIC_STATUS) {
            soundPool.stop(streamID);
            currentVol = Conversion.CovertMusicVolume();
            streamID = soundPool.play(wrong_effect, currentVol, currentVol, PRIORITY, LOOP, PLAYBACK_RATE);
        }
    }

    public void playGameOverEffect() {
        if (GameInfo.GAME_MUSIC_STATUS) {
            soundPool.stop(streamID);
            currentVol = Conversion.CovertMusicVolume();
            streamID = soundPool.play(gameOver_effect, currentVol, currentVol, PRIORITY, LOOP, PLAYBACK_RATE);
        }
    }

    public void destroy() {
        soundPool.release();
    }
}
