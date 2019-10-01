package com.hayatcode.client;

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
import com.hayatcode.client.data.UserLocalStore;
import com.hayatcode.client.model.User;

public class ChangePasswordActivity extends AppCompatActivity {

    TextInputEditText ET_current_password, ET_password, ET_repeat_password;
    ImageView IV_back;
    Button BT_reset;
    User user;
    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();

        IV_back = findViewById(R.id.back);
        BT_reset = findViewById(R.id.reset);

        ET_password = findViewById(R.id.password);
        ET_repeat_password = findViewById(R.id.repeat_password);
        ET_current_password = findViewById(R.id.current_password);

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

                    resetPassword();
                }
            }
        });
    }

    private void resetPassword() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), ET_current_password.getText().toString());

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(ET_password.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Utils.showSimpleDialogMessage(ChangePasswordActivity.this,
                                                        getString(R.string.change_pass_success),
                                                        getString(R.string.ok),
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                finish();
                                                            }
                                                        },
                                                        false);
                                            } else {
                                                Utils.showSimpleDialogMessage(ChangePasswordActivity.this,
                                                        task.getException().getMessage(),
                                                        getString(R.string.retry),
                                                        null,
                                                        true);
                                            }
                                        }
                                    });
                        } else {

                            Utils.showSimpleDialogMessage(ChangePasswordActivity.this,
                                    getString(R.string.current_pass_incorrect),
                                    getString(R.string.ok),
                                    null,
                                    true);

                        }
                    }
                });
    }

    private boolean validateUserInfo() {
        boolean validate = true;


        String password = ET_password.getText().toString();
        String repeat_password = ET_repeat_password.getText().toString();

        String current_password = ET_current_password.getText().toString();


        if (password.length() < 6) {
            ET_password.setError(getString(R.string.password_required));
            validate = false;
        } else
            ET_password.setError(null);

        if (!repeat_password.equals(password)) {
            ET_repeat_password.setError(getString(R.string.password_error_repeat));
            validate = false;
        } else
            ET_repeat_password.setError(null);


        return validate;
    }

}
