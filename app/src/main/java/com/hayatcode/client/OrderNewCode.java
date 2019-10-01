package com.hayatcode.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hayatcode.client.model.User;

public class OrderNewCode extends AppCompatActivity {
    EditText ET_firstName, ET_familyName, ET_phone, ET_email;

    Button BT_next;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_new_code);

        ET_firstName = findViewById(R.id.firstname);
        ET_familyName = findViewById(R.id.familyname);
        ET_phone = findViewById(R.id.phone);
        ET_email = findViewById(R.id.email);

        BT_next = findViewById(R.id.next);

        user = new User();

        BT_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    Intent intent = new Intent(OrderNewCode.this, OrderNewCode2.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean validate() {
        boolean validate = true;

        String firstname = ET_firstName.getText().toString();
        String familyname = ET_familyName.getText().toString();
        String email = ET_email.getText().toString();
        String phone = ET_phone.getText().toString();


        user.setFirstName(firstname);
        user.setFamilyName(familyname);
        user.setEmail(email);
        user.setPhone(phone);

        if (firstname.isEmpty()) {
            ET_firstName.setError(getString(R.string.field_required));
            validate = false;
        } else
            ET_firstName.setError(null);

        if (familyname.isEmpty()) {
            ET_familyName.setError(getString(R.string.field_required));
            validate = false;
        } else
            ET_familyName.setError(null);

        if (phone.isEmpty()) {
            ET_phone.setError(getString(R.string.field_required));
            validate = false;
        } else
            ET_phone.setError(null);

        if (!Utils.isEmailValid(email)) {
            ET_email.setError(getString(R.string.email_error));
            validate = false;
        } else
            ET_email.setError(null);

        return validate;
    }
}
