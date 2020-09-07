package com.example.arrows_m.arrow;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.arrows_m.R;

import java.util.ArrayList;

public class ArrowAdapter extends ArrayAdapter<ArrowObject> {


    private Context context;
    private LayoutInflater inflater;
    private ArrayList<ArrowObject> arrowObjects;

    private final int rightColor;
    private final int highlightColor;

    public ArrowAdapter(Context c, ArrayList<ArrowObject> list) {
        super(c, 0, list);
        this.context = c;
        this.arrowObjects = list;
        rightColor = context.getResources().getColor(R.color.colorRight, null);
        highlightColor = context.getResources().getColor(R.color.colorHighlight, null);
    }

    @Override
    public int getCount() {
        return this.arrowObjects.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ArrowItemHolder arrowItemHolder = null;

        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.arrow_item, null);
            arrowItemHolder = new ArrowItemHolder(convertView);
            convertView.setTag(arrowItemHolder);
        } else arrowItemHolder = (ArrowItemHolder) convertView.getTag();

        arrowItemHolder.image.setImageResource(arrowObjects.get(position).getArrowImage());
        arrowItemHolder.image.setTag(arrowObjects.get(position).getArrowType());

        if (arrowObjects.get(position).isArrowRight()) {
            arrowItemHolder.background.setBackgroundColor(rightColor);
        } else {
            if (arrowObjects.get(position).isHighLight()) {
                arrowItemHolder.background.setBackgroundColor(highlightColor);
            } else {
                arrowItemHolder.background.setBackgroundColor(Color.TRANSPARENT);
            }
        }
        return convertView;
    }

    private static class ArrowItemHolder {
        private ImageView image;
        private ViewGroup background;

        public ArrowItemHolder(View v) {
            image = (ImageView) v.findViewById(R.id.arrow_image);
            background = (ViewGroup) v.findViewById(R.id.arrow_image_background);
        }
    }
}
