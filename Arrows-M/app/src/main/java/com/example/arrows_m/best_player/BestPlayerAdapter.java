package com.example.arrows_m.best_player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.arrows_m.R;

import java.util.ArrayList;

public class BestPlayerAdapter extends ArrayAdapter<BestPlayerObject> {

    private Context context;
    private int resource;
    private LayoutInflater inflater;

    public BestPlayerAdapter(@NonNull Context context, int resource, @NonNull ArrayList<BestPlayerObject> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BestPlayerObjectHolder bestPlayerObjectHolder = null;
        if (inflater == null)
            inflater = LayoutInflater.from(context);

        if (convertView == null) {
            convertView = inflater.inflate(resource, null);
            bestPlayerObjectHolder = new BestPlayerObjectHolder(convertView);
            convertView.setTag(bestPlayerObjectHolder);
        } else bestPlayerObjectHolder = (BestPlayerObjectHolder) convertView.getTag();

        bestPlayerObjectHolder.name.setText(getItem(position).getName());
        bestPlayerObjectHolder.score.setText(String.valueOf(getItem(position).getScore()));

        return convertView;
    }

    private static class BestPlayerObjectHolder {
        private TextView name;
        private TextView score;

        public BestPlayerObjectHolder(View v) {
            name = (TextView) v.findViewById(R.id.bestWorldName_textView);
            score = (TextView) v.findViewById(R.id.bestWorldScoreTitle_textView);
        }
    }
}
