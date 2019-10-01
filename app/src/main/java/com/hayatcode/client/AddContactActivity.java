package com.hayatcode.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.hayatcode.client.data.UserLocalStore;
import com.hayatcode.client.model.Contact;
import com.hayatcode.client.model.User;

public class AddContactActivity extends AppCompatActivity {
    EditText ET_name, ET_phone;
    Button BT_add;
    User user;
    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        ET_name = findViewById(R.id.name);
        ET_phone = findViewById(R.id.phone);

        BT_add = findViewById(R.id.add);

        userLocalStore = new UserLocalStore(this);

        user = userLocalStore.getLoggedInUser();

        BT_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {

                    if (Utils.getUID() != null) {
                        user.getContacts().add(new Contact(ET_name.getText().toString(),
                                ET_phone.getText().toString()));

                        FirebaseDatabase.getInstance()
                                .getReference()
                                .child("user")
                                .child(Utils.getUID())
                                .child("contacts")
                                .setValue(user.getContacts())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            userLocalStore.storeUserData(user);
                                            setResult(Constant.RESULT_SUCCESS);

                                        } else {
                                            setResult(Constant.RESULT_FAILED);
                                        }
                                        finish();
                                    }
                                });
                    }
                }
            }
        });
    }

    private boolean validate() {
        boolean validate = true;


        String name = ET_name.getText().toString();
        String phone = ET_phone.getText().toString();



        if (name.isEmpty()) {
            ET_name.setError(getString(R.string.field_required));
            validate = false;
        } else
            ET_name.setError(null);

        if (phone.isEmpty()) {
            ET_name.setError(getString(R.string.field_required));
            validate = false;
        } else
            ET_name.setError(null);


        return validate;
    }
}
