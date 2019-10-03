package com.hayatcode.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.hayatcode.client.data.UserLocalStore;
import com.hayatcode.client.model.MedicalInfo;
import com.hayatcode.client.model.User;

public class AddInfoActivity extends AppCompatActivity {
    EditText ET_name;
    TextView TV_type;
    Button BT_add;
    User user;
    UserLocalStore userLocalStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);

        ET_name = findViewById(R.id.name);

        BT_add = findViewById(R.id.add);
        TV_type = findViewById(R.id.type);

        userLocalStore = new UserLocalStore(this);

        user = userLocalStore.getLoggedInUser();

        BT_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {

                    if (Utils.getUID() != null) {
                        final MedicalInfo medicalInfo = new MedicalInfo(TV_type.getText().toString(),
                                ET_name.getText().toString());
                        user.getMedInfos().add(medicalInfo);



                        FirebaseDatabase.getInstance()
                                .getReference()
                                .child("user")
                                .child(Utils.getUID())
                                .child("medInfos")
                                .setValue(user.getMedInfos())
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

        TV_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builderSingle = new android.app.AlertDialog.Builder(AddInfoActivity.this);

                final ArrayAdapter<String> arrayAdapter =
                        new ArrayAdapter<String>(AddInfoActivity.this,
                                android.R.layout.select_dialog_singlechoice);
                arrayAdapter.add("allergy");
                arrayAdapter.add("disease");
                arrayAdapter.add("medication");


                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TV_type.setText(arrayAdapter.getItem(which));
                    }
                });
                builderSingle.show();
            }
        });
    }
    private boolean validate() {
        boolean validate = true;


        String name = ET_name.getText().toString();
        String type = TV_type.getText().toString();



        if (name.isEmpty()) {
            ET_name.setError(getString(R.string.field_required));
            validate = false;
        } else
            ET_name.setError(null);
        if (type.equals("Select the Type")) {
            validate = false;
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(AddInfoActivity.this);
            builder.setMessage(getString(R.string.select_type));
            builder.setPositiveButton(getString(R.string.retry),
                    null);
            builder.show();
        }

        return validate;
    }
}
