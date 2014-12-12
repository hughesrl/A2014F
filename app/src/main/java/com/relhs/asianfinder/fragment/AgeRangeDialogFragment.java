package com.relhs.asianfinder.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.relhs.asianfinder.AsianFinderApplication;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.adapter.SpinnerCustomAdapter;
import com.relhs.asianfinder.data.SpinnerItems;

public class AgeRangeDialogFragment extends DialogFragment {

    private String purposeToOpen;

    public AgeRangeDialogFragment() { }

    /*In order to receive event callback, create a dialog box activity must implement this interface.
     * In case the host need to query the properties dialog box, each method will pass a DialogFragment instance.  */
    public interface AgeRangeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, SpinnerItems from, SpinnerItems to);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Examples of the use of this interface to transmit motion events
    AgeRangeDialogListener mListener;

    public static AgeRangeDialogFragment newInstance() {
        AgeRangeDialogFragment f = new AgeRangeDialogFragment();
        return f;
    }

    // Override the Fragment.onAttach () method to instantiate NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // To verify whether acrivity inherits the callback interface host
        try {
            // Instantiate a NoticeDialogListener so that we can pass the event to the host
            mListener = (AgeRangeDialogListener) activity;
        } catch (ClassCastException e) {
            // Activity no inheritance interface exception is thrown
            throw new ClassCastException(activity.toString()
                    + " must implement AgeRangeDialogFragment");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();

        int width = getResources().getDisplayMetrics().widthPixels - (getResources().getDisplayMetrics().widthPixels/4);
        window.setLayout(width, windowParams.WRAP_CONTENT);
        windowParams.dimAmount = 0.90f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, android.R.style.Theme_Light_Panel);


    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View myFragmentView = inflater.inflate(R.layout.dialog_age_range, container, false);

        final Spinner spinnerFrom = (Spinner) myFragmentView.findViewById(R.id.spinnerFrom);
        final Spinner spinnerTo = (Spinner) myFragmentView.findViewById(R.id.spinnerTo);

        SpinnerCustomAdapter adapterFrom = new SpinnerCustomAdapter(getActivity(), android.R.layout.simple_spinner_item, AsianFinderApplication.populateAgeRange("From", 18));
        spinnerFrom.setAdapter(adapterFrom);

        SpinnerCustomAdapter adapterTo = new SpinnerCustomAdapter(getActivity(), android.R.layout.simple_spinner_item, AsianFinderApplication.populateAgeRange("To", 18));
        spinnerTo.setAdapter(adapterTo);
        spinnerTo.setEnabled(false);

        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final SpinnerItems item = (SpinnerItems) parent.getItemAtPosition(position);
                if(!item.getSpinnerValue().isEmpty()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SpinnerCustomAdapter adapterTo = new SpinnerCustomAdapter(getActivity(), android.R.layout.simple_spinner_item, AsianFinderApplication.populateAgeRange("To", Integer.parseInt(item.getSpinnerValue())));
                            spinnerTo.setAdapter(adapterTo);
                            spinnerTo.setEnabled(true);
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        Button btnCancel = (Button)myFragmentView.findViewById(R.id.btnDialogCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDialogNegativeClick(AgeRangeDialogFragment.this);
            }
        });
        Button btnOkay = (Button)myFragmentView.findViewById(R.id.btnDialogOkay);
        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!spinnerFrom.getSelectedItem().toString().isEmpty() || !spinnerTo.getSelectedItem().toString().isEmpty()) {
                    SpinnerItems ageFromValue = (SpinnerItems) spinnerFrom.getItemAtPosition(spinnerFrom.getSelectedItemPosition());
                    SpinnerItems ageToValue = (SpinnerItems) spinnerTo.getItemAtPosition(spinnerTo.getSelectedItemPosition());

                    mListener.onDialogPositiveClick(AgeRangeDialogFragment.this, ageFromValue, ageToValue);
                } else {
                    Toast.makeText(getActivity(), "Please select Age From.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return myFragmentView;
    }

}
