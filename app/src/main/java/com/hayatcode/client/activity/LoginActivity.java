package com.hayatcode.client.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hayatcode.client.R;
import com.hayatcode.client.data.UserLocalStore;
import com.hayatcode.client.model.User;

public class LoginActivity extends AppCompatActivity {

    EditText ET_password;
    Button BT_login;
    FirebaseAuth firebaseAuth;

    UserLocalStore userLocalStore;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ET_password = findViewById(R.id.password);
        BT_login = findViewById(R.id.login);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();

        userLocalStore = new UserLocalStore(this);
        BT_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    String password = ET_password.getText().toString();

                    String email = getIntent().getStringExtra("email");

                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String UID = task.getResult().getUser().getUid();
                                        databaseReference.child("user").child(UID)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                User user = dataSnapshot.getValue(User.class);
                                                userLocalStore.setUserLoggedIn(true);
                                                userLocalStore.storeUserData(user);

                                                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });



                                    } else {

                                        AlertDialog.Builder builder =
                                                new AlertDialog.Builder(LoginActivity.this);
                                        builder.setMessage(getString(R.string.wrong_password));
                                        builder.setPositiveButton(getString(R.string.retry),
                                                null);
                                        builder.show();

                                    }
                                }
                            });
                }

            }
        });


    }

    private boolean validate() {
        boolean validate = true;

        String password = ET_password.getText().toString();


        if (password.length() < 5) {
            ET_password.setError(getString(R.string.password_required));
            validate = false;
        } else
            ET_password.setError(null);


        return validate;
    }
}
