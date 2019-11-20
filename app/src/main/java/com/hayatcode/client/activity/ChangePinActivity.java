package com.hayatcode.client.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.hayatcode.client.R;
import com.hayatcode.client.data.UserLocalStore;
import com.hayatcode.client.model.User;
import com.hayatcode.client.utils.Constant;
import com.hayatcode.client.utils.Utils;

public class ChangePinActivity extends AppCompatActivity {

    TextInputEditText ET_pin, ET_repeat_pin;
    ImageView IV_back;
    Button BT_reset;
    User user;
    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin);

        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();

        IV_back = findViewById(R.id.back);
        BT_reset = findViewById(R.id.reset);

        ET_pin = findViewById(R.id.pin);
        ET_repeat_pin = findViewById(R.id.repeat_pin);

        IV_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        BT_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateUserInfo()) {

                    resetPin();
                }
            }
        });
    }

    private void resetPin() {
        if (validateUserInfo()) {
            final int pin = Integer.parseInt(ET_pin.getText().toString());
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("user")
                    .child(Utils.getUID())
                    .child("pin")
                    .setValue(pin).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.setPin(pin);
                        userLocalStore.storeUserData(user);
                        Utils.showSimpleDialogMessage(ChangePinActivity.this,
                                getString(R.string.change_pin_success),
                                getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        setResult(Constant.RESULT_SUCCESS);
                                        finish();
                                    }
                                },
                                false);
                    }
                }
            });



        }



    }

    private boolean validateUserInfo() {
        boolean validate = true;


        String pin = ET_pin.getText().toString();
        String repeat_pin = ET_repeat_pin.getText().toString();

        if (ET_pin.getText().toString().length() != 5) {
            ET_pin.setError(getString(R.string.pin_required));
            validate = false;
        } else {
            if (ET_pin.getText().toString().startsWith("0")) {
                ET_pin.setError(getString(R.string.pin_starts_with));
                validate = false;
            } else
                ET_pin.setError(null);
        }


        if (!repeat_pin.equals(pin)) {
            ET_repeat_pin.setError(getString(R.string.pin_repeat_error));
            validate = false;
        } else
            ET_repeat_pin.setError(null);


        return validate;
    }

}
