package com.hayatcode.client.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Order implements Parcelable {
    long created_at;
    String UID;
    String items;

    public Order(long created_at, String UID, String items) {

        this.created_at = created_at;
        this.UID = UID;
        this.items = items;
    }

    protected Order(Parcel in) {
        created_at = in.readLong();
        UID = in.readString();
        items = in.readString();
    }

    protected Order() {

    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(created_at);
        parcel.writeString(UID);
        parcel.writeString(items);
    }
}
