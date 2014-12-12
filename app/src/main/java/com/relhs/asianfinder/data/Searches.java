package com.relhs.asianfinder.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Searches implements Parcelable {
    private String displayName;
    private String gender;
    private int ageFrom;
    private int ageTo;
    private String locationCounty;
    private String locationState;
    private String locationCity;

    public Searches(String displayName, String gender, int ageFrom, int ageTo,
                    String locationCounty, String locationState, String locationCity) {
        setDisplayName(displayName);
        setGenderSearch(gender);
        setAgeFrom(ageFrom);
        setAgeTo(ageTo);
        setLocationCounty(locationCounty);
        setLocationState(locationState);
        setLocationCity(locationCity);
    }


    private Searches(Parcel in) {
        displayName= in.readString();
        gender= in.readString();
        ageFrom= in.readInt();
        ageTo= in.readInt();
        locationCounty= in.readString();
        locationState= in.readString();
        locationCity= in.readString();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGenderSearch() {
        return gender;
    }

    public void setGenderSearch(String gender) {
        this.gender = gender;
    }

    public int getAgeFrom() {
        return ageFrom;
    }

    public void setAgeFrom(int ageFrom) {
        this.ageFrom = ageFrom;
    }

    public int getAgeTo() {
        return ageTo;
    }

    public void setAgeTo(int ageTo) {
        this.ageTo = ageTo;
    }

    public String getLocationCounty() {
        return locationCounty;
    }

    public void setLocationCounty(String locationCounty) {
        this.locationCounty = locationCounty;
    }

    public String getLocationState() {
        return locationState;
    }

    public void setLocationState(String locationState) {
        this.locationState = locationState;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public void setLocationCity(String locationCity) {
        this.locationCity = locationCity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(displayName);
        out.writeString(gender);
        out.writeInt(ageFrom);
        out.writeInt(ageTo);
        out.writeString(locationCounty);
        out.writeString(locationState);
        out.writeString(locationCity);

    }
    // Just cut and paste this for now
    public static final Parcelable.Creator<Searches> CREATOR = new Parcelable.Creator<Searches>() {
        public Searches createFromParcel(Parcel in) {
            return new Searches(in);
        }

        public Searches[] newArray(int size) {
            return new Searches[size];
        }
    };
}
