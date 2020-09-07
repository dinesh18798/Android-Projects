package com.example.arrows_m;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.arrows_m.util.Conversion;

public class GameResultDialog extends AppCompatDialogFragment {

    private static final String TAG = "Game Result Dialog";
    private String SCORE_STRING = "";

    private TextView resultScoreTextView;
    private TextView resultTotalGamePlayTextView;
    private Button shareScoreButton;
    private Button backMainMenuButton;

    private int score;
    private long totalGamePlay;

    public static GameResultDialog newInstance(int score, long totalGamePlay) {
        GameResultDialog gameResultDialog= new GameResultDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("score", score);
        args.putLong("totalGamePlay", totalGamePlay);
        gameResultDialog.setArguments(args);
        return gameResultDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.SCORE_STRING = getString(R.string.score);
        this.score  = getArguments().getInt("score");
        this.totalGamePlay  = getArguments().getLong("totalGamePlay");

        int style = AppCompatDialogFragment.STYLE_NO_FRAME;
        int  theme = android.R.style.Theme_Holo_Light_Dialog;
        setStyle(style, theme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_game_result, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        resultScoreTextView = (TextView) view.findViewById(R.id.result_score_textView);
        resultTotalGamePlayTextView = (TextView) view.findViewById(R.id.result_total_gamePlay_textView);
        shareScoreButton = (Button) view.findViewById(R.id.share_score_button);
        backMainMenuButton = (Button) view.findViewById(R.id.back_mainMenu_button);

        resultScoreTextView.setText(String.format("%s: %d", SCORE_STRING, score));
        resultTotalGamePlayTextView.setText(Conversion.ConvertMilliToString(totalGamePlay));

        shareScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.share_score_button:
                        shareScore();
                        break;
                }
            }
        });

        backMainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.back_mainMenu_button:
                       getDialog().dismiss();
                       break;
                }
            }
        });

        return view;
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    public void shareScore() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        String shareBody = String.format("Hey! I got %d in %s. Want to compete with me. " +
                "Install Arrow game in your phone now. Download from " +
                "https://dinesh18798.itch.io/arrows-m", score, Conversion.ConvertMilliToString(totalGamePlay));
        String shareSubject = "Let's Play" ;

        share.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
        share.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(share, "Share Using"));
    }
}
