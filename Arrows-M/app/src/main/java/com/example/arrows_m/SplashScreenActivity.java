package com.example.arrows_m;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 5000;

    private ImageView splashScreenImageView;
    private TextView splashScreenTextView;

    private Animation splashIconAnimation;
    private Animation splashTextAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        hideSystemUI();

        splashScreenImageView = (ImageView) findViewById(R.id.splash_screen_imageView);
        splashScreenTextView = (TextView) findViewById(R.id.splash_screen_textView);

        splashIconAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_icon_animation);
        splashTextAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_text_animation);

        splashScreenImageView.setAnimation(splashIconAnimation);
        splashScreenTextView.setAnimation(splashTextAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent signInIntent = new Intent(SplashScreenActivity.this, SignInActivity.class);
                startActivity(signInIntent);
                finish();
            }
        }, SPLASH_DURATION);
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
