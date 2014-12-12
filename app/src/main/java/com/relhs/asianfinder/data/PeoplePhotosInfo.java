package com.relhs.asianfinder.data;

import android.os.Parcel;

import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;

public class PeoplePhotosInfo implements AsymmetricItem {
    private int colSpan;
    private int rowSpan;
    private int position;

    private String category;
    private String file;
    private String commentsCount;

    public PeoplePhotosInfo() {
        this(1, 1, 0, "", "", "");
    }

	public PeoplePhotosInfo(Parcel in) {
		// TODO Auto-generated constructor stub
		readFromParcel(in);
	}

    public PeoplePhotosInfo(int colSpan, int rowSpan, int position, String category, String file, String commentsCount) {
        this.colSpan = colSpan;
        this.rowSpan = rowSpan;
        this.position = position;

        this.category = category;
        this.file = file;
        this.commentsCount = commentsCount;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(String commentsCount) {
        this.commentsCount = commentsCount;
    }

    private void readFromParcel(final Parcel in) {
        colSpan = in.readInt();
        rowSpan = in.readInt();
        position = in.readInt();

        category = in.readString();
        file = in.readString();
        commentsCount = in.readString();

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

        dest.writeString(category);
        dest.writeString(file);
        dest.writeString(commentsCount);
    }

	/* Parcelable interface implementation */
    public static final Creator<PeoplePhotosInfo> CREATOR = new Creator<PeoplePhotosInfo>() {

        @Override
        public PeoplePhotosInfo createFromParcel(final Parcel in) {
            return new PeoplePhotosInfo(in);
        }

        @Override
        public PeoplePhotosInfo[] newArray(final int size) {
            return new PeoplePhotosInfo[size];
        }
    };
}
