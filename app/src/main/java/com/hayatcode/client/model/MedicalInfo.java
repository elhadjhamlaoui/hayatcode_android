package com.hayatcode.client.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MedicalInfo implements Parcelable {
    private String type;
    private  String name;

    public MedicalInfo(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public MedicalInfo() {

    }
    protected MedicalInfo(Parcel in) {
        type = in.readString();
        name = in.readString();
    }

    public static final Creator<MedicalInfo> CREATOR = new Creator<MedicalInfo>() {
        @Override
        public MedicalInfo createFromParcel(Parcel in) {
            return new MedicalInfo(in);
        }

        @Override
        public MedicalInfo[] newArray(int size) {
            return new MedicalInfo[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(type);
        parcel.writeString(name);
    }
}
