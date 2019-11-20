package com.hayatcode.client.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.hayatcode.client.data.UserLocalStore;
import com.hayatcode.client.model.User;

public class Static {
    public static TextView TV_medications, TV_diseases, TV_allergies;
    public static ImageView IV_empty_contacts, IV_empty_emergency, IV_empty_records;

    private static User user;
    private static UserLocalStore userLocalStore;

    public static User getUser(Context context) {
        if (user == null)
            user = getUserLocalStore(context).getLoggedInUser();
        return user;
    }

    public static UserLocalStore getUserLocalStore(Context context) {
        if (userLocalStore == null)
            userLocalStore = new UserLocalStore(context);
        return userLocalStore;
    }

    public static void setUser(User loggedInUser) {
        user = loggedInUser;
    }
}
