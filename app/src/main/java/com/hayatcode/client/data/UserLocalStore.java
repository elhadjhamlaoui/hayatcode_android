package com.hayatcode.client.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hayatcode.client.model.Contact;
import com.hayatcode.client.model.MedicalInfo;
import com.hayatcode.client.model.Record;
import com.hayatcode.client.model.User;

import java.util.ArrayList;
import java.util.HashMap;


public class UserLocalStore {

    public static final String SP_NAME = "userDetails";

    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(User user) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();

        userLocalDatabaseEditor.putString("firstName", user.getFirstName());
        userLocalDatabaseEditor.putString("familyName", user.getFamilyName());
        userLocalDatabaseEditor.putString("photo", user.getPhoto());
        userLocalDatabaseEditor.putString("email", user.getEmail());
        userLocalDatabaseEditor.putString("gender", user.getGender());
        userLocalDatabaseEditor.putString("birthDate", user.getBirthDate());
        userLocalDatabaseEditor.putString("phone", user.getPhone());
        userLocalDatabaseEditor.putString("deliveryAddress", user.getDeliveryAddress());
        userLocalDatabaseEditor.putString("items", user.getItems());
        userLocalDatabaseEditor.putString("blood", user.getBlood());
        userLocalDatabaseEditor.putString("nationalId", user.getNationalId());

        userLocalDatabaseEditor.putInt("pin", user.getPin());


        Gson gson = new Gson();

        String contacts = gson.toJson(user.getContacts());
        userLocalDatabaseEditor.putString("contacts", contacts);

        String medInfos = gson.toJson(user.getMedInfos());
        userLocalDatabaseEditor.putString("medInfos", medInfos);

        String records = gson.toJson(user.getRecords());
        userLocalDatabaseEditor.putString("records", records);

        userLocalDatabaseEditor.commit();
    }

    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putBoolean("loggedIn", loggedIn);
        userLocalDatabaseEditor.apply();
    }

    public boolean isLoggedIn() {
        return userLocalDatabase.getBoolean("loggedIn", false);
    }

    public void clearUserData() {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.clear();
        userLocalDatabaseEditor.apply();
    }

    public User getLoggedInUser() {
        if (!isLoggedIn()) {
            return null;
        }
        String firstName = userLocalDatabase.getString("firstName", "");
        String familyName = userLocalDatabase.getString("familyName", "");
        String photo = userLocalDatabase.getString("photo", "");
        String email = userLocalDatabase.getString("email", "");
        String gender = userLocalDatabase.getString("gender", "");
        String birthDate = userLocalDatabase.getString("birthDate", "");
        String phone = userLocalDatabase.getString("phone", "");
        String deliveryAddress = userLocalDatabase.getString("deliveryAddress", "");
        String items = userLocalDatabase.getString("items", "");
        String blood = userLocalDatabase.getString("blood", "");
        String nationalId = userLocalDatabase.getString("nationalId","");

        int pin = userLocalDatabase.getInt("pin",0);



        Gson gson = new Gson();

        String contactsJSON = userLocalDatabase.getString("contacts", "");
        ArrayList<Contact> contacts = gson.fromJson(contactsJSON, new TypeToken<ArrayList<Contact>>() {
        }.getType());

        String medInfosJSON = userLocalDatabase.getString("medInfos", "");
        HashMap<String, MedicalInfo> medInfos = gson.fromJson(medInfosJSON, new TypeToken<HashMap<String, MedicalInfo>>() {
        }.getType());

        String recordsJSON = userLocalDatabase.getString("records", "");
        ArrayList<Record> records = gson.fromJson(recordsJSON, new TypeToken<ArrayList<Record>>() {
        }.getType());

        User user = new User(familyName, firstName, email, birthDate, gender, photo, "",
                deliveryAddress, phone, blood, pin, nationalId, items, contacts, medInfos, records);
        return user;
    }
}
