package com.hayatcode.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.hayatcode.client.data.UserLocalStore;
import com.hayatcode.client.model.User;

public class MainActivity extends AppCompatActivity {

    Button BT_scanner, BT_order;
    FirebaseAuth firebaseAuth;
    User user;
    UserLocalStore userLocalStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userLocalStore = new UserLocalStore(this);

        firebaseAuth = FirebaseAuth.getInstance();

        if (userLocalStore.isLoggedIn() && firebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }
        setContentView(R.layout.activity_main);
        BT_scanner = findViewById(R.id.scan);
        BT_order = findViewById(R.id.order);


        BT_scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, Scanner.class));
            }
        });

        BT_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OrderNewCode.class));

            }
        });
    }
}
