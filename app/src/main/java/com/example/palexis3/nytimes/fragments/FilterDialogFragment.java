package com.example.palexis3.nytimes.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.example.palexis3.nytimes.R;

import java.util.ArrayList;

public class FilterDialogFragment extends DialogFragment{

    private EditText dateEditText;
    private Spinner sortSpinner;
    private CheckBox artsCheckBox;
    private CheckBox fashionCheckBox;
    private CheckBox sportsCheckBox;

    private onFilterSelectedListener fListener;

    ArrayList<String> newsDeskList = null;




    //empty constructor
    public FilterDialogFragment() {

    }

    public static FilterDialogFragment newInstance(String title) {
        FilterDialogFragment frag = new FilterDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    //activity must implement this interface to receive filter info
    public interface onFilterSelectedListener {
        public abstract void onFilterSelected(String date, String order, ArrayList<String> list);
    }


    //make sure the activity implemented the interface method
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
           this.fListener = (onFilterSelectedListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onFilterSelected");
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //get view to run findViewById
        View view = inflater.inflate(R.layout.fragment_filter, null);

        //get fields from view
        dateEditText = (EditText) view.findViewById(R.id.etBeginDate);
        sortSpinner = (Spinner) view.findViewById(R.id.spSortOrder);
        artsCheckBox = (CheckBox) view.findViewById(R.id.cbArts);
        fashionCheckBox = (CheckBox) view.findViewById(R.id.cbFashionAndStyle);
        sportsCheckBox = (CheckBox) view.findViewById(R.id.cbSports);

        //inflate and set the layout for the dialog
        builder.setView(view)
                //add action buttons
                .setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //run the fetching query
                        String date = getBeginDate();
                        String order = getSortOrder();
                        ArrayList<String> newsDesk = getCheckBoxes();
                        fListener.onFilterSelected(date, order, newsDesk);
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


    public ArrayList<String> getCheckBoxes() {

       newsDeskList = new ArrayList<>();

        artsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && !newsDeskList.contains("Arts")) {
                    newsDeskList.add("Arts");
                }
            }
        });

        fashionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && !newsDeskList.contains("Fashion & Style")) {
                    newsDeskList.add("Fashion & Style");
                }
            }
        });

        sportsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && !newsDeskList.contains("Sports")) {
                    newsDeskList.add("Sports");
                }
            }
        });

        return newsDeskList;
    }


    //this is pretty messy code; see if you can refactor in the client
    public String getBeginDate() {

        String result = dateEditText.getText().toString();

        /*length of item must be 8
        if(result.length() != 8) {
           return  "";
        }*/

        //check if string is a legit number
        try {
            int i = Integer.parseInt(result);
        } catch (NumberFormatException e){
            return "";
        }

        return result;
    }

    //get the selected spinner item
    public String getSortOrder() {

        String result = sortSpinner.getSelectedItem().toString();
        setSpinnerValue(result);
        return result;
    }

    //reset the spinner value to what was chosen
    public void setSpinnerValue(String val) {
        int index = 0;
        SpinnerAdapter adapter = sortSpinner.getAdapter();
        for(int i = 0; i < adapter.getCount(); i++) {
            if(adapter.getItem(i).equals(val)) {
                index = i;
                break;
            }
        }
        sortSpinner.setSelection(index);
    }
}
