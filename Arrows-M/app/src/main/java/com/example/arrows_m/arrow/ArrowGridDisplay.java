package com.example.arrows_m.arrow;

import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;

import com.example.arrows_m.GameActivity;
import com.example.arrows_m.R;

import java.util.ArrayList;
import java.util.Random;

public class ArrowGridDisplay {

    private static final int INITIAL_POSITION = 0;
    private GridView arrowsGrid;
    private GameActivity gameActivity;
    private ArrayList<ArrowObject> arrowObjects;
    private ArrayList<ArrowObject> currentArrowsObjects;
    private Random random;

    protected int currentNoOfArrowObjects = 2;

    public ArrowGridDisplay(GameActivity activity) {
        this.gameActivity = activity;

        this.random = new Random();

        addArrowsInList();
        initView();
        makeCurrentList();
        setAdapter();
    }

    public void refreshArrowGrid() {
        makeCurrentList();
        highlightArrowObject(INITIAL_POSITION);
        arrowsGrid.invalidateViews();
    }

    private void initView() {
        this.arrowsGrid = (GridView) gameActivity.findViewById(R.id.arrows_grid);
        this.arrowsGrid.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });
    }

    private void makeCurrentList() {

        if (currentArrowsObjects == null) {
            currentArrowsObjects = new ArrayList<>();
        } else {
            currentArrowsObjects.clear();
        }

        for (int i = 0; i < currentNoOfArrowObjects; i++) {
            int index = random.nextInt(arrowObjects.size());
            ArrowObject object = new ArrowObject(arrowObjects.get(index));
            object.setHighLight(false);
            currentArrowsObjects.add(object);
        }
    }

    private void setAdapter() {
        ArrowAdapter arrowAdapter = new ArrowAdapter(gameActivity.getApplicationContext(), currentArrowsObjects);
        arrowsGrid.setAdapter(arrowAdapter);
        highlightArrowObject(INITIAL_POSITION);
    }

    public int getCurrentNoOfArrowObjects() {
        return currentNoOfArrowObjects;
    }

    public void setCurrentNoOfArrowObjects(int num) {
        currentNoOfArrowObjects = num;
    }

    private void addArrowsInList() {
        if (arrowObjects == null)
            arrowObjects = new ArrayList<>();

        arrowObjects.add(new ArrowObject("up", R.drawable.arrow_up));
        arrowObjects.add(new ArrowObject("down", R.drawable.arrow_down));
        arrowObjects.add(new ArrowObject("left", R.drawable.arrow_left));
        arrowObjects.add(new ArrowObject("right", R.drawable.arrow_right));
    }

    public void highlightArrowObject(int position) {
        ArrowObject arrowObject = ((ArrowAdapter) arrowsGrid.getAdapter()).getItem(position);
        if (position == INITIAL_POSITION) {
            arrowObject.setHighLight(true);
            arrowObject.setArrowRight(false);
        } else {
            ArrowObject prevObject = ((ArrowAdapter) arrowsGrid.getAdapter()).getItem(position - 1);
            prevObject.setHighLight(false);
            prevObject.setArrowRight(true);
            arrowObject.setHighLight(true);
        }
        ((ArrowAdapter) arrowsGrid.getAdapter()).notifyDataSetChanged();
        arrowsGrid.invalidateViews();
    }

    public void resetHighlightArrowObject(int currentPosition) {
        if (currentPosition != INITIAL_POSITION) {
            for (int index = 1; index <= currentPosition; index++) {
                ArrowObject arrowObject = ((ArrowAdapter) arrowsGrid.getAdapter()).getItem(index);
                arrowObject.setHighLight(false);
                arrowObject.setArrowRight(false);
            }
            highlightArrowObject(INITIAL_POSITION);
        }
    }
}
