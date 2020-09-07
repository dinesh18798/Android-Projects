package com.example.lab3;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    static int n = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // System.out.println("Run Test");
        //ModelTest test = new ModelTest();
        //test.LightTest();
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.main);
    }

    public void updateScore(int score) {
        //System.out.println("Score: " + score);
        LightsView lightsView = (LightsView) findViewById(R.id.lightsView);
        if (lightsView.model.isSolved()) {
            updateResult();
            return;
        }

        TextView textView = (TextView) findViewById(R.id.scoreText);
        textView.setText(String.valueOf(score));
    }
    public void updateResult() {
        //System.out.println("Score: " + score);
        TextView textView = (TextView) findViewById(R.id.scoreText);
        textView.setText("You Win !!!");
    }

    public void resetModel(View view)
    {
        LightsView lightsView = (LightsView) findViewById(R.id.lightsView);
        lightsView.model.reset();
        updateScore(0);
        lightsView.postInvalidate();
    }
}
