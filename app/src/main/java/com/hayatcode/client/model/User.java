package com.hayatcode.client.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

public class User implements Parcelable {

    private String familyName, firstName, email,
            birthDate, gender, photo, password ="",
            deliveryAddress, phone, blood;
    private int pin;
    private String items,nationalId;
    private ArrayList<Contact> contacts;
    private HashMap<String, MedicalInfo> medInfos;
    private ArrayList<Record> records;


    public User(String familyName, String firstName, String email,
                String birthDate, String gender, String photo, String password,
                String deliveryAddress, String phone, String blood, int pin,
                String nationalId, String items, ArrayList<Contact> contacts,
                HashMap<String, MedicalInfo> medInfos, ArrayList<Record> records) {

        this.familyName = familyName;
        this.firstName = firstName;
        this.email = email;
        this.birthDate = birthDate;
        this.gender = gender;
        this.photo = photo;
        this.password = password;
        this.deliveryAddress = deliveryAddress;
        this.phone = phone;
        this.blood = blood;
        this.pin = pin;
        this.nationalId = nationalId;
        this.items = items;
        this.contacts = contacts;
        this.medInfos = medInfos;
        this.records = records;
    }

    public User() {

    }

    protected User(Parcel in) {
        familyName = in.readString();
        firstName = in.readString();
        email = in.readString();
        birthDate = in.readString();
        gender = in.readString();
        photo = in.readString();
        password = in.readString();
        deliveryAddress = in.readString();
        phone = in.readString();
        blood = in.readString();
        pin = in.readInt();
        items = in.readString();
        nationalId = in.readString();
        contacts = in.createTypedArrayList(Contact.CREATOR);
        records = in.createTypedArrayList(Record.CREATOR);
        medInfos = (HashMap<String, MedicalInfo>) in.readSerializable();

    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public HashMap<String, MedicalInfo> getMedInfos() {
        return medInfos == null ? new HashMap<String, MedicalInfo>() : medInfos;
    }

    public void setMedInfos(HashMap<String, MedicalInfo> medInfos) {
        this.medInfos = medInfos;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public ArrayList<Record> getRecords() {
        return records != null ? records : new ArrayList<Record>();
    }

    public void setRecords(ArrayList<Record> records) {
        this.records = records;
    }

    public ArrayList<Contact> getContacts() {
        return contacts != null ? contacts : new ArrayList<Contact>();
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }



    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(familyName);
        parcel.writeString(firstName);
        parcel.writeString(email);
        parcel.writeString(birthDate);
        parcel.writeString(gender);
        parcel.writeString(photo);
        parcel.writeString(password);
        parcel.writeString(deliveryAddress);
        parcel.writeString(phone);
        parcel.writeString(blood);
        parcel.writeInt(pin);
        parcel.writeString(items);
        parcel.writeString(nationalId);
        parcel.writeTypedList(contacts);
        parcel.writeTypedList(records);
        parcel.writeSerializable(medInfos);

    }
}
