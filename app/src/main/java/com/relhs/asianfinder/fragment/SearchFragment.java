package com.relhs.asianfinder.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.relhs.asianfinder.AsianFinderApplication;
import com.relhs.asianfinder.FilterActivity;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.SearchResultActivity;
import com.relhs.asianfinder.adapter.SpinnerCustomAdapter;
import com.relhs.asianfinder.data.Searches;
import com.relhs.asianfinder.data.SpinnerItems;
import com.relhs.asianfinder.view.CustomButton;
import com.relhs.asianfinder.view.CustomEditTextView;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements AgeRangeDialogFragment.AgeRangeDialogListener,
        LocationSelectionDialogFragment.LocationSelectionDialogListener{

	private static final String ARG_POSITION = "position";

	private ListView mListView;
    private View myFragmentView;
    private InputMethodManager imm;
    private CustomEditTextView etAgeRange;
    private CustomEditTextView etLocation;

    private int ageFromValue;
    private int ageToValue;

    private String locationCountry;
    private String locationState;
    private String locationCity;


    public static Fragment newInstance(int position) {
		SearchFragment f = new SearchFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        ageFromValue = 0;
        ageToValue = 0;

        locationCountry = "";
        locationState = "";
        locationCity = "";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_search, null);

        final Spinner spinnerGender = (Spinner) myFragmentView.findViewById(R.id.spinnerGender);
        SpinnerCustomAdapter adapter = new SpinnerCustomAdapter(getActivity(), android.R.layout.simple_spinner_item, AsianFinderApplication.populateGender());
        spinnerGender.setAdapter(adapter);

        final CustomEditTextView etDisplayName = (CustomEditTextView) myFragmentView.findViewById(R.id.etDisplayName);
        etAgeRange = (CustomEditTextView) myFragmentView.findViewById(R.id.etAgeRange);
        etLocation = (CustomEditTextView) myFragmentView.findViewById(R.id.etLocation);

        etAgeRange.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    ((FilterActivity)getActivity()).showAgeRangeDialog(SearchFragment.this);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 1);
                }
            }
        });
        etAgeRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FilterActivity)getActivity()).showAgeRangeDialog(SearchFragment.this);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 1);
            }
        });

        etLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    ((FilterActivity)getActivity()).showLocationSelectionDialog(SearchFragment.this);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 1);
                }
            }
        });
        etLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FilterActivity)getActivity()).showLocationSelectionDialog(SearchFragment.this);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 1);
            }
        });

        CustomButton btnBasicSearch = (CustomButton)myFragmentView.findViewById(R.id.btnBasicSearch);
        btnBasicSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList queryList = new ArrayList();

                String displayName = etDisplayName.getText().toString().trim();
                SpinnerItems genderItem = (SpinnerItems) spinnerGender.getItemAtPosition(spinnerGender.getSelectedItemPosition());
                Searches query = new Searches(displayName, genderItem.getSpinnerValue(), ageFromValue, ageToValue, locationCountry, locationState, locationCity);

                queryList.add(query);

                Intent searchResult = new Intent(getActivity(), SearchResultActivity.class);
                searchResult.putParcelableArrayListExtra(SearchResultActivity.INTENT_SEARCHES, queryList);
                startActivity(searchResult);
            }
        });
		return myFragmentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

//		mListView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.list_item, android.R.id.text1, mListItems));
//        mListView.setOnScrollListener(this);

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
                Toast.makeText(getActivity(), "Please select a Country", Toast.LENGTH_LONG).show();
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

    }
}