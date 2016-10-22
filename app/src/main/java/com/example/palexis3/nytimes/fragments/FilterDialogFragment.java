package com.example.palexis3.nytimes.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.palexis3.nytimes.R;

public class FilterDialogFragment extends DialogFragment{

    private EditText dateEditText;
    private Spinner sortSpinner;
    private CheckBox artsCheckBox;
    private CheckBox fashionCheckBox;
    private CheckBox sportsCheckBox;


    public FilterDialogFragment() {

    }

    public static FilterDialogFragment newInstance(String title) {
        FilterDialogFragment frag = new FilterDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //inflate and set the layout for the dialog
        builder.setView(inflater.inflate(R.layout.fragment_filter, null))
                //add action buttons
                .setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //run the fetching query
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    /*@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter, container);
    }*/

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get fields from view
        dateEditText = (EditText) view.findViewById(R.id.etBeginDate);
        sortSpinner = (Spinner) view.findViewById(R.id.spSortOrder);
        artsCheckBox = (CheckBox) view.findViewById(R.id.cbArts);
        fashionCheckBox = (CheckBox) view.findViewById(R.id.cbFashionAndStyle);
        sportsCheckBox = (CheckBox) view.findViewById(R.id.cbSports);
    }
}
