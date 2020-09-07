package com.example.lab3;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;

public class AboutActivity extends Activity {

    static String TAG = "AboutActivity Status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Create");
        // the root directory is "assets"
        WebView wb = new WebView(this);
        wb.loadUrl("file:///android_asset/html/About.html");
        setContentView(wb);
        //ActionBar bar = getActionBar();
       // getActionBar().setDisplayHomeAsUpEnabled(true);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                // assuming that the parent activity is
                // on the back stack, it's enough to just
                // finish teh current activity to return to it
                // the alternative way is commented out
                // this.startActivity(upIntent)
                // NavUtils.navigateUpTo(this, upIntent);
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
