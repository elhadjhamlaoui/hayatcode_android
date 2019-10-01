package com.hayatcode.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hayatcode.client.model.User;

public class OrderNewCode3 extends AppCompatActivity {
    EditText ET_nationalId, ET_address, ET_postalcode;
    Button BT_next;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_new_code3);
        ET_nationalId = findViewById(R.id.national_id);
        ET_address = findViewById(R.id.address);
        ET_postalcode = findViewById(R.id.postalcode);

        BT_next = findViewById(R.id.next);

        user = getIntent().getParcelableExtra("user");

        BT_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    Intent intent = new Intent(OrderNewCode3.this, OrderNewCode4.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean validate() {
        boolean validate = true;


        int nationalId = Integer.parseInt(ET_nationalId.getText().toString());
        String address = ET_address.getText().toString();
        String postalcode = ET_postalcode.getText().toString();


        user.setNationalId(nationalId);
        user.setDeliveryAddress(address+" "+postalcode);

        if (nationalId == 0) {
            ET_nationalId.setError(getString(R.string.field_required));
            validate = false;
        } else
            ET_nationalId.setError(null);

        if (address.isEmpty()) {
            ET_address.setError(getString(R.string.field_required));
            validate = false;
        } else
            ET_address.setError(null);

        if (postalcode.isEmpty()) {
            ET_postalcode.setError(getString(R.string.field_required));
            validate = false;
        } else
            ET_postalcode.setError(null);


        return validate;
    }
}
