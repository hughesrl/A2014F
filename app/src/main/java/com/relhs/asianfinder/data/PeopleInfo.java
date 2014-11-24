package com.relhs.asianfinder.data;

import android.os.Parcel;

import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;

public class PeopleInfo implements AsymmetricItem {
    private int colSpan;
    private int rowSpan;
    private int position;

    private int id;
    private String username;
    private String gender;
    private String aged;
    private String country;
    private String state;
    private String city;
    private int is_online;

    private String main_photo;
    private String subphoto_1;
    private String subphoto_2;


    public PeopleInfo() {
        this(1, 1, 0, 0, "", "", "", "", "", "", 0, "", "", "");
    }
	 

	public PeopleInfo(Parcel in) {
		// TODO Auto-generated constructor stub
		readFromParcel(in);
	}

    public PeopleInfo(int colSpan, int rowSpan, int position, int id, String username, String gender, String aged, String country, String state, String city, int is_online,
                      String main_photo, String subphoto_1, String subphoto_2) {
        this.colSpan = colSpan;
        this.rowSpan = rowSpan;
        this.position = position;

        this.id = id;
        this.username = username;
        this.gender = gender;
        this.aged = aged;
        this.country = country;
        this.state = state;
        this.city = city;
        this.is_online = is_online;

        this.main_photo = main_photo;
        this.subphoto_1 = subphoto_1;
        this.subphoto_2 = subphoto_2;
    }

    public int getColumnSpan() {
        return colSpan;
    }

    public void setColumnSpan(int columnSpan) {
        this.colSpan = columnSpan;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAged() {
        return aged;
    }

    public void setAged(String aged) {
        this.aged = aged;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getIs_online() {
        return is_online;
    }

    public void setIs_online(int is_online) {
        this.is_online = is_online;
    }

    public String getMain_photo() {
        return main_photo;
    }

    public void setMain_photo(String main_photo) {
        this.main_photo = main_photo;
    }

    public String getSubphoto_1() {
        return subphoto_1;
    }

    public void setSubphoto_1(String subphoto_1) {
        this.subphoto_1 = subphoto_1;
    }

    public String getSubphoto_2() {
        return subphoto_2;
    }

    public void setSubphoto_2(String subphoto_2) {
        this.subphoto_2 = subphoto_2;
    }

    private void readFromParcel(final Parcel in) {
        colSpan = in.readInt();
        rowSpan = in.readInt();
        position = in.readInt();


        id = in.readInt();
        username = in.readString();
        aged = in.readString();
        country = in.readString();
        state = in.readString();
        city = in.readString();
        is_online = in.readInt();

        main_photo = in.readString();
        subphoto_1 = in.readString();
        subphoto_2 = in.readString();


    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(colSpan);
        dest.writeInt(rowSpan);
        dest.writeInt(position);

        dest.writeInt(id);
        dest.writeString(username);
        dest.writeString(aged);
        dest.writeString(country);
        dest.writeString(state);
        dest.writeString(city);
        dest.writeInt(is_online);

        dest.writeString(main_photo);
        dest.writeString(subphoto_1);
        dest.writeString(subphoto_2);

    }

	/* Parcelable interface implementation */
    public static final Creator<PeopleInfo> CREATOR = new Creator<PeopleInfo>() {

        @Override
        public PeopleInfo createFromParcel(final Parcel in) {
            return new PeopleInfo(in);
        }

        @Override
        public PeopleInfo[] newArray(final int size) {
            return new PeopleInfo[size];
        }
    };
}
