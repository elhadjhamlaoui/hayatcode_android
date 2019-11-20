package com.hayatcode.client.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hayatcode.client.R;
import com.hayatcode.client.data.UserLocalStore;
import com.hayatcode.client.model.User;
import com.hayatcode.client.utils.Constant;
import com.hayatcode.client.utils.Utils;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    TextView TV_logout, TV_change_pass, TV_pin, TV_language;
    UserLocalStore userLocalStore;
    ImageView IV_pin, IV_language;
    Button BT_privacy;
    ImageView IV_back;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();

        TV_change_pass = findViewById(R.id.change_pass);
        TV_logout = findViewById(R.id.logout);
        IV_pin = findViewById(R.id.edit_pin);
        IV_language = findViewById(R.id.edit_language);
        BT_privacy = findViewById(R.id.privacy);
        IV_back = findViewById(R.id.back);
        TV_pin = findViewById(R.id.change_pin);
        TV_language = findViewById(R.id.change_language);

        TV_change_pass.setOnClickListener(this);
        TV_logout.setOnClickListener(this);
        IV_pin.setOnClickListener(this);
        IV_language.setOnClickListener(this);
        BT_privacy.setOnClickListener(this);
        IV_back.setOnClickListener(this);


        TV_pin.setText("Pin: " + user.getPin());



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.change_pass:
                startActivity(new Intent(this,
                        ChangePasswordActivity.class));
                break;

            case R.id.logout:

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.logout));
                builder.setPositiveButton(getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Utils.logout(SettingsActivity.this, userLocalStore);
                            }
                        });
                builder.setNegativeButton(getString(R.string.no),
                        null);
                builder.show();

                break;

            case R.id.privacy:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://firebasestorage.googleapis.com/v0/b/hayatcode-16825.appspot.com/o/privacy_policy.html?alt=media&token=e641ba6d-4087-4625-8f14-3fd4a477765a"));
                startActivity(browserIntent);

                break;

            case R.id.edit_pin:
                startActivityForResult(new Intent(this,
                        ChangePinActivity.class), 16);

                break;

            case R.id.edit_language:
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

                final ArrayAdapter<String> arrayAdapter =
                        new ArrayAdapter<String>(this,
                                android.R.layout.simple_list_item_1);
                arrayAdapter.add("English");

                arrayAdapter.add("Arabic (coming soon)");


                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            //updateLanguage("en");
                        } else {
                            Toast.makeText(SettingsActivity.this, "coming soon", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builderSingle.setTitle(getString(R.string.change_language));

                builderSingle.show();

                break;

            case R.id.back:

                onBackPressed();

                break;

        }
    }

    private void saveLanguage(String language) {
        SharedPreferences languagepref = getSharedPreferences("language",MODE_PRIVATE);
        SharedPreferences.Editor editor = languagepref.edit();
        editor.putString("language",language );
        editor.commit();
    }

    private String getLanguage() {
        SharedPreferences languagepref = getSharedPreferences("language",MODE_PRIVATE);
        return languagepref.getString("language","en");
    }

    private void updateLanguage(String lang) {
        String languageToLoad  = lang;
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 16) {
            if (resultCode == Constant.RESULT_SUCCESS) {
                user = userLocalStore.getLoggedInUser();
                TV_pin.setText("Pin: " + user.getPin());
            }
        }
    }
}
