package com.relhs.asianfinder.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;

public class PeopleProfileInfo implements Parcelable {

    private String profile;

    private String matches;
    private String preferences;
    private String photos;


    // PROFILE
//    private String basic;
//    private String appearance;
//    private String lifestyle;
//    private String culture_values;
//    private String personal;
//    private String interest;
//    private String others;
//    private String preference;


    public PeopleProfileInfo() {
        this("", "", "", "");
    }


	public PeopleProfileInfo(Parcel in) {
		// TODO Auto-generated constructor stub
		readFromParcel(in);
	}

    public PeopleProfileInfo(String profile, String matches, String preferences, String photos) {
        this.profile = profile;
        this.matches = matches;
        this.preferences = preferences;
        this.photos = photos;
    }

    private void readFromParcel(final Parcel in) {
        profile = in.readString();
        matches = in.readString();
        preferences = in.readString();
        photos = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(profile);
        dest.writeString(matches);
        dest.writeString(preferences);
        dest.writeString(photos);

    }

	/* Parcelable interface implementation */
    public static final Creator<PeopleProfileInfo> CREATOR = new Creator<PeopleProfileInfo>() {

        @Override
        public PeopleProfileInfo createFromParcel(final Parcel in) {
            return new PeopleProfileInfo(in);
        }

        @Override
        public PeopleProfileInfo[] newArray(final int size) {
            return new PeopleProfileInfo[size];
        }
    };
}
