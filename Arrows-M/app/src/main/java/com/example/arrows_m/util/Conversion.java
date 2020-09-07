package com.example.arrows_m.util;

import java.util.Locale;

public class Conversion {

    public static String ConvertMilliToString (long totalTime) {
        String timerTextFormat = " ";
        int minutes = (int) (totalTime / 1000) / 60;
        int seconds = (int) (totalTime / 1000) % 60;
        timerTextFormat = String.format(Locale.getDefault(), "Total time: %02d mins %02d secs", minutes, seconds);
        return timerTextFormat;
    }

    public static float CovertMusicVolume() {
        return (float) GameInfo.GAME_MUSIC_VOLUME / GameInfo.GAME_MUSIC_VOLUME_MAX;
    }
}
