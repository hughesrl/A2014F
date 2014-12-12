package com.relhs.asianfinder;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.relhs.asianfinder.adapter.SpinnerCustomAdapter;
import com.relhs.asianfinder.data.Searches;
import com.relhs.asianfinder.data.SpinnerItems;
import com.relhs.asianfinder.fragment.AgeRangeDialogFragment;
import com.relhs.asianfinder.fragment.LocationSelectionDialogFragment;
import com.relhs.asianfinder.fragment.SampleListFragment;
import com.relhs.asianfinder.fragment.SearchFragment;
import com.relhs.asianfinder.view.CustomButton;
import com.relhs.asianfinder.view.CustomEditTextView;

import java.util.ArrayList;

public class SearchActivity extends FragmentActivity implements
        AgeRangeDialogFragment.AgeRangeDialogListener,
        LocationSelectionDialogFragment.LocationSelectionDialogListener{

    private InputMethodManager imm;
    private CustomEditTextView etAgeRange;
    private CustomEditTextView etLocation;

    private int ageFromValue;
    private int ageToValue;

    private String locationCountry;
    private String locationState;
    private String locationCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        ageFromValue = 0;
        ageToValue = 0;

        locationCountry = "";
        locationState = "";
        locationCity = "";

        final Spinner spinnerGender = (Spinner) findViewById(R.id.spinnerGender);
        SpinnerCustomAdapter adapter = new SpinnerCustomAdapter(this, android.R.layout.simple_spinner_item, AsianFinderApplication.populateGender());
        spinnerGender.setAdapter(adapter);

        final CustomEditTextView etDisplayName = (CustomEditTextView) findViewById(R.id.etDisplayName);
        etAgeRange = (CustomEditTextView) findViewById(R.id.etAgeRange);
        etLocation = (CustomEditTextView) findViewById(R.id.etLocation);

        etAgeRange.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    DialogFragment ageRangeDialogFragment = AgeRangeDialogFragment.newInstance();
                    ageRangeDialogFragment.show(getSupportFragmentManager(), "AgeRangeDialogFragment");
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 1);
                }
            }
        });
        etAgeRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // To create an instance of DialogFragment and displays
                DialogFragment ageRangeDialogFragment = AgeRangeDialogFragment.newInstance();
                ageRangeDialogFragment.show(getSupportFragmentManager(), "AgeRangeDialogFragment");
                imm.hideSoftInputFromWindow(v.getWindowToken(), 1);
            }
        });

        etLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    // To create an instance of DialogFragment and displays
                    DialogFragment locationSelectionDialogFragment = LocationSelectionDialogFragment.newInstance();
                    locationSelectionDialogFragment.show(getSupportFragmentManager(), "LocationSelectionDialogFragment");
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 1);
                }
            }
        });
        etLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // To create an instance of DialogFragment and displays
                DialogFragment locationSelectionDialogFragment = LocationSelectionDialogFragment.newInstance();
                locationSelectionDialogFragment.show(getSupportFragmentManager(), "LocationSelectionDialogFragment");
                imm.hideSoftInputFromWindow(v.getWindowToken(), 1);
            }
        });

        CustomButton btnBasicSearch = (CustomButton)findViewById(R.id.btnBasicSearch);
        btnBasicSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList queryList = new ArrayList();

                String displayName = etDisplayName.getText().toString().trim();
                SpinnerItems genderItem = (SpinnerItems) spinnerGender.getItemAtPosition(spinnerGender.getSelectedItemPosition());
                Searches query = new Searches(displayName, genderItem.getSpinnerValue(), ageFromValue, ageToValue, locationCountry, locationState, locationCity);

                queryList.add(query);

                Intent searchResult = new Intent(SearchActivity.this, SearchResultActivity.class);
                searchResult.putParcelableArrayListExtra(SearchResultActivity.INTENT_SEARCHES, queryList);
                startActivity(searchResult);
            }
        });

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, SpinnerItems from, SpinnerItems to) {
        if(from.getSpinnerValue().isEmpty()) {
            etAgeRange.setText("");
        } else {
            String fromText = from.getSpinnerTitle();
            String toText = "";
            ageFromValue = Integer.parseInt(from.getSpinnerValue());
            if(to.getSpinnerValue().isEmpty()) {
                ageToValue = Integer.parseInt(from.getSpinnerValue());
            } else {
                toText = " - "+to.getSpinnerTitle();
                ageToValue = Integer.parseInt(to.getSpinnerValue());
            }
            etAgeRange.setText(fromText+toText);
        }

        dialog.dismiss();
    }
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onLocationSelectionDialogPositiveClick(DialogFragment dialog, SpinnerItems country, SpinnerItems state, SpinnerItems city) {
        String locationText;
        if(country != null) {
            if (country.getSpinnerValue().isEmpty()) {
                Toast.makeText(SearchActivity.this, "Please select a Country", Toast.LENGTH_LONG).show();
            } else {
                locationText = country.getSpinnerTitle();
                locationCountry = country.getSpinnerValue();

                if (state != null) {
                    if (!state.getSpinnerValue().isEmpty()) {
                        locationText = state.getSpinnerTitle() + ", " + locationText;
                        locationState = state.getSpinnerValue();

                        if (city != null) {
                            if (!city.getSpinnerValue().isEmpty()) {
                                locationText = city.getSpinnerTitle() + ", " + locationText;
                                locationCity = city.getSpinnerValue();
                            }
                        }
                    }
                }
                etLocation.setText(locationText);
                dialog.dismiss();
            }
        }
    }
    @Override
    public void onLocationSelectionDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }



}
