package com.example.lab3;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static int n = 5;
    private String modelKey = "Model";
    private LightsModel model;
    static String aboutItem = "About";
    static String optionItem = "Options";
    static String TAG = "MainActivity Status";

    static String PREFERENCES = "shared preferences";
    static String KEY = "grid";
    static String NGRID = "nGrid";
    static String SCORE = "Score";
    ArrayList<Integer> localList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // System.out.println("Run Test");
        //ModelTest test = new ModelTest();
        //test.LightTest();
        super.onCreate(savedInstanceState);
        Log.i (TAG, "Create");
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.splash_screen);
        new MyTask().execute(null, null, null);

       /* try {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        System.out.println("MyThreadTest Inner Exception: " + e);
                    }
                }
            };
            thread.start();
        } catch (Exception e) {
            System.out.println("MyThreadTest Outer Exception: " + e);
            e.printStackTrace();
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "Start");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "Restart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "Pause");
        saveData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "Stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Destroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(modelKey,getModel());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        model = (LightsModel) savedInstanceState.getSerializable(modelKey);
    }

    //-- Public functions

    public boolean onCreateOptionsMenu(Menu menu) {
            menu.add(aboutItem);
            menu.add(optionItem);
            return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(aboutItem)) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        else if (item.getTitle().equals(optionItem)) {
            CustomDialogFragment dialogFragment = new CustomDialogFragment();
            dialogFragment.show(getFragmentManager(), TAG);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
       if(lightsView != null) {
           lightsView.resetGrid(this.n);
          // lightsView.model.reset();
       }
        updateScore(0);
        lightsView.postInvalidate();
    }

   AlertDialog alertDialog;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            alertDialog = new AlertDialog.Builder(this).create();
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
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //-- Private Functions

    private LightsModel getModel() {
        LightsView lightsView = (LightsView) findViewById(R.id.lightsView);
        model = lightsView.getModel();
        if(model == null){
            model = lightsView.createModel(n);
        }
        return model;
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        int[][] localGrid = getModel().grid;

        localList = new ArrayList<Integer>();
        for (int i = 0; i < getModel().num; i++) {

            for (int j = 0; j < model.num; j++) {
                localList.add(localGrid[i][j]);
            }
        }

        String json = gson.toJson(localList);
        editor.putString(KEY, json);
        editor.putInt(SCORE, getModel().score);
        editor.putInt(NGRID, n);
        editor.apply();
    }

    private void loadGrid() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        int numGrid  = sharedPreferences.getInt(NGRID,5);
        this.n = numGrid;
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY, null);
        if(json != null) {
            Type type = new TypeToken<ArrayList<Integer>>() {
            }.getType();
            localList = gson.fromJson(json, type);
            getModel().grid = ConvertToArray(localList);
            updateScore(model.getScore());
        }
    }

    private int[][] ConvertToArray(ArrayList<Integer> arrayList) {

        int[][] localGrid = new int[getModel().num][getModel().num];

        if(arrayList != null) {

            int a = 0;
            for (int i = 0; i < localGrid.length; i++) {
                for (int j = 0; j < localGrid.length; j++) {
                    localGrid[i][j] = arrayList.get(a++);
                }
            }
        }

        return localGrid;
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(5000);
            } catch ( Exception e) {}
            return null;
        }

        protected void onPostExecute(Void result) {
            // new Toast(this)
            loadGrid();
            setContentView(R.layout.main);
            loadData();
        }
    }
}
