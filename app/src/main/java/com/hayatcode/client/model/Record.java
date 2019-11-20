package com.hayatcode.client.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Record implements Parcelable {
    private String name, url;
    private int privacy;


    public Record(String name, String url, int privacy) {
        this.name = name;
        this.url = url;
        this.privacy = privacy;
    }

    protected Record() {

    }

    protected Record(Parcel in) {
        name = in.readString();
        url = in.readString();
        privacy = in.readInt();
    }

    public static final Creator<Record> CREATOR = new Creator<Record>() {
        @Override
        public Record createFromParcel(Parcel in) {
            return new Record(in);
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };

    public int getPrivacy() {
        return privacy;
    }

    public void setPrivacy(int privacy) {
        this.privacy = privacy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(url);
        parcel.writeInt(privacy);
    }
}
