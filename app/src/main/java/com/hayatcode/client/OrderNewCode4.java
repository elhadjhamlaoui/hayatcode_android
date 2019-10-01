package com.hayatcode.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.WriterException;
import com.hayatcode.client.model.Order;
import com.hayatcode.client.model.User;

import java.io.ByteArrayOutputStream;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class OrderNewCode4 extends AppCompatActivity {
    CheckBox Bracelet, ShoeTage, KeyChain, WaletCard;
    boolean atleast = false;
    Button BT_next;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    User user;
    int totalPrice = 0;
    TextView TV_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_new_code4);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        Bracelet = findViewById(R.id.bracelet);
        ShoeTage = findViewById(R.id.shoetag);
        KeyChain = findViewById(R.id.keychain);
        WaletCard = findViewById(R.id.wallet);

        TV_price = findViewById(R.id.price);

        BT_next = findViewById(R.id.order);

        user = getIntent().getParcelableExtra("user");

        Bracelet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updatePrice(b);
            }
        });

        ShoeTage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updatePrice(b);
            }
        });

        KeyChain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updatePrice(b);

            }
        });

        WaletCard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updatePrice(b);
            }
        });

        BT_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {

                    firebaseAuth.signInWithEmailAndPassword(user.getEmail(),
                            user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = task.getResult().getUser();

                                makeOrder(firebaseUser);


                            } else {
                                firebaseAuth
                                        .createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    firebaseAuth.
                                                            signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                                    if (task.isSuccessful()) {
                                                                        final FirebaseUser firebaseUser = task.getResult().getUser();

                                                                        user.setPassword("");
                                                                        databaseReference.child("user").child(firebaseUser.getUid())
                                                                                .setValue(user)
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            makeOrder(firebaseUser);

                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            }
                        }
                    });

                } else {

                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(OrderNewCode4.this);
                    builder.setMessage(getString(R.string.atleast_item));
                    builder.setPositiveButton(getString(R.string.retry),
                            null);
                    builder.show();
                }
            }
        });

    }

    private void updatePrice(boolean b) {
        if (b)
            totalPrice += 10;
        else
            totalPrice -= 10;

        TV_price.setText(totalPrice + " $");

    }


    private boolean validate() {

        boolean bracelet = Bracelet.isChecked();
        boolean shoeTage = ShoeTage.isChecked();
        boolean keyChain = KeyChain.isChecked();
        boolean waletCard = WaletCard.isChecked();


        atleast = bracelet || shoeTage || keyChain || waletCard;

        String items = "";

        if (bracelet)
            items += "bracelet/";
        if (shoeTage)
            items += "shoe_tag/";
        if (keyChain)
            items += "key_chain/";
        if (waletCard)
            items += "wallet_card/";

        user.setItems(items);


        return atleast;
    }

    private void makeOrder(final FirebaseUser firebaseUser) {

        String key = databaseReference.child("order")
                .push().getKey();

        Order order = new Order(System.currentTimeMillis(),
                firebaseUser.getUid(), user.getItems());
        databaseReference.child("order").child(key).setValue(order)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseStorage storage = FirebaseStorage.getInstance();


                            Bitmap bitmap;

                            QRGEncoder qrgEncoder =
                                    new QRGEncoder("hayatcode.company/" + user.getEmail(),
                                            null,
                                            QRGContents.Type.TEXT, 500);
                            try {
                                StorageReference storageRef = storage.getReference();

                                StorageReference imageRef = storageRef.child("qrcode")
                                        .child(firebaseUser.getUid() + ".jpg");

                                bitmap = qrgEncoder.encodeAsBitmap();
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 45, baos);
                                byte[] data = baos.toByteArray();

                                UploadTask uploadTask = imageRef.putBytes(data);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Intent intent = new Intent(getApplicationContext(), OrderNewCode5.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                });
                            } catch (WriterException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
    }
}
