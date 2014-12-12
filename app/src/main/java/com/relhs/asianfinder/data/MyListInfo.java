package com.relhs.asianfinder.data;

import android.os.Parcel;

import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;

public class MyListInfo implements AsymmetricItem {
    private int colSpan;
    private int rowSpan;
    private int position;

    private int id;
    private String username;
    private String age;
    private String gender;

    private String location;
    private String listtype;
    private String main_photo;
    private String subphoto_1;
    private String subphoto_2;


    public MyListInfo() {
        this(1, 1, 0, 0, "", "", "", "", "", "", "", "");
    }


	public MyListInfo(Parcel in) {
		// TODO Auto-generated constructor stub
		readFromParcel(in);
	}

    public MyListInfo(int colSpan, int rowSpan, int position, int id, String username, String age, String gender, String location, String listtype,
                      String main_photo, String subphoto_1, String subphoto_2) {
        this.colSpan = colSpan;
        this.rowSpan = rowSpan;
        this.position = position;

        this.id = id;
        this.username = username;
        this.age = age;
        this.gender = gender;
        this.location = location;
        this.listtype = listtype;

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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getListType() {
        return listtype;
    }

    public void setListType(String listtype) {
        this.listtype = listtype;
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
        age = in.readString();
        gender = in.readString();
        location = in.readString();
        listtype = in.readString();

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
        dest.writeString(age);
        dest.writeString(gender);
        dest.writeString(location);
        dest.writeString(listtype);
        dest.writeString(main_photo);
        dest.writeString(subphoto_1);
        dest.writeString(subphoto_2);

    }

	/* Parcelable interface implementation */
    public static final Creator<MyListInfo> CREATOR = new Creator<MyListInfo>() {

        @Override
        public MyListInfo createFromParcel(final Parcel in) {
            return new MyListInfo(in);
        }

        @Override
        public MyListInfo[] newArray(final int size) {
            return new MyListInfo[size];
        }
    };
}
