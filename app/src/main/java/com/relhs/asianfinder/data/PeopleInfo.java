package com.relhs.asianfinder.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;

public class PeopleInfo implements AsymmetricItem {
    private int columnSpan;
    private int rowSpan;
    private int position;

    private String name;
    private String category;
    private String ownership;
    private String image_filename;
    private String image_width;
    private String image_height;
    private String tile_size;
    private String source;
    private String id;
    private String lat;
    private String lng;
    private String distance;

    public PeopleInfo() {
        this(1, 1, 0, "", "", "", "", "", "", "", "", "", "", "", "");
    }
	 
	public PeopleInfo(int columnSpan, int rowSpan, int position,
                        String name,String category,String ownership,String image_filename,String image_width,
                        String image_height,String tile_size,String source, String id,String lat,String lng,String distance) {
		// TODO Auto-generated constructor stub

		this.columnSpan = columnSpan;
        this.rowSpan = rowSpan;
        this.position = position;


		this.name = name;
		this.category = category;
		this.ownership = ownership;
		this.image_filename = image_filename;
		this.image_width = image_width;
		this.image_height = image_height;
		this.tile_size = tile_size;
		this.source = source;
        this.id = id;
		this.lat = lat;
		this.lng = lng;
		this.distance = distance;
	} 

	public PeopleInfo(Parcel in) {
		// TODO Auto-generated constructor stub
		readFromParcel(in);
	}
    public int getColumnSpan() {
        return columnSpan;
    }

    public void setColumnSpan(int columnSpan) {
        this.columnSpan = columnSpan;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOwnership() {
        return ownership;
    }

    public void setOwnership(String ownership) {
        this.ownership = ownership;
    }

    public String getImage_filename() {
        return image_filename;
    }

    public void setImage_filename(String image_filename) {
        this.image_filename = image_filename;
    }

    public String getImage_width() {
        return image_width;
    }

    public void setImage_width(String image_width) {
        this.image_width = image_width;
    }

    public String getImage_height() {
        return image_height;
    }

    public void setImage_height(String image_height) {
        this.image_height = image_height;
    }

    public String getTile_size() {
        return tile_size;
    }

    public void setTile_size(String tile_size) {
        this.tile_size = tile_size;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

	
	private void readFromParcel(final Parcel in) {
        columnSpan = in.readInt();
        rowSpan = in.readInt();
        position = in.readInt();

        name = in.readString();
        category = in.readString();
        ownership = in.readString();
        image_filename = in.readString();
        image_width = in.readString();
        image_height = in.readString();
        tile_size = in.readString();
        source = in.readString();
        id = in.readString();
        lat = in.readString();
        lng = in.readString();
        distance = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(columnSpan);
        dest.writeInt(rowSpan);
        dest.writeInt(position);

        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(ownership);
        dest.writeString(image_filename);
        dest.writeString(image_width);
        dest.writeString(image_height);
        dest.writeString(tile_size);
        dest.writeString(source);
        dest.writeString(id);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeString(distance);
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
