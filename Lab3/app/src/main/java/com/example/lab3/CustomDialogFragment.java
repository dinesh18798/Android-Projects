package com.example.lab3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.NumberPicker;

public class CustomDialogFragment extends DialogFragment {

    public static int nMaxGrid = 10;
    public static int nMinGrid = 4;
    public static int nGrid = 5;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final NumberPicker numberPicker = new NumberPicker(getActivity());
        numberPicker.setMaxValue(nMaxGrid);
        numberPicker.setMinValue(nMinGrid);
        numberPicker.setValue(nGrid);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        builder.setView(numberPicker);
        builder.setMessage("Choose the grid size (n x n)");
        builder.setPositiveButton("New Game", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                nGrid = numberPicker.getValue();
                MainActivity.n = nGrid;
                ((MainActivity)getActivity()).resetModel(null);

            }
        });
        builder.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                nGrid = numberPicker.getValue();
            }
        });
        return builder.create();
    }
}

