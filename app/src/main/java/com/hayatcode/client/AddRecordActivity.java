package com.hayatcode.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hayatcode.client.data.UserLocalStore;
import com.hayatcode.client.model.MedicalInfo;
import com.hayatcode.client.model.Record;
import com.hayatcode.client.model.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class AddRecordActivity extends AppCompatActivity {
    EditText ET_name;
    TextView TV_file;
    Button BT_add;
    User user;
    UserLocalStore userLocalStore;
    ProgressDialog imageUploadDialog;
    File photoFile;
    String mCurrentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        ET_name = findViewById(R.id.name);

        BT_add = findViewById(R.id.add);
        TV_file = findViewById(R.id.record);

        userLocalStore = new UserLocalStore(this);

        user = userLocalStore.getLoggedInUser();

        imageUploadDialog = new ProgressDialog(this);
        imageUploadDialog.setTitle(R.string.uploading_file);

        BT_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    userLocalStore.storeUserData(user);
                    setResult(Constant.RESULT_SUCCESS);
                    finish();
                }
            }
        });

        TV_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ET_name.getText().toString().isEmpty()) {
                    android.app.AlertDialog.Builder builderSingle =
                            new android.app.AlertDialog.Builder(AddRecordActivity.this);

                    final ArrayAdapter<String> arrayAdapter =
                            new ArrayAdapter<String>(AddRecordActivity.this,
                                    android.R.layout.simple_list_item_1);
                    arrayAdapter.add("take a picture");
                    arrayAdapter.add("upload file");



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

                                pickFromCamera();
                            } else {
                                checkForStoragePermissions2();
                            }
                        }
                    });
                    builderSingle.show();
                } else {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(AddRecordActivity.this);
                    builder.setMessage(getString(R.string.select_name));
                    builder.setPositiveButton(getString(R.string.retry),
                            null);
                    builder.show();
                }



            }
        });
    }
    private boolean validate() {
        boolean validate = true;


        String name = ET_name.getText().toString();
        String type = TV_file.getText().toString();



        if (name.isEmpty()) {
            ET_name.setError(getString(R.string.field_required));
            validate = false;
        } else
            ET_name.setError(null);
        if (type.equals("Select the record")) {
            validate = false;
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(AddRecordActivity.this);
            builder.setMessage(getString(R.string.select_record));
            builder.setPositiveButton(getString(R.string.retry),
                    null);
            builder.show();
        }

        return validate;
    }


    private void pickFromCamera() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (AddRecordActivity.this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        Constant.CAMERA_PERMISSION_CODE);
            } else {
                checkForStoragePermissions();
            }
        } else {
            checkForStoragePermissions();
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        // Result code is RESULT_OK only if the user selects an Image
        Bitmap bitmap;
        if (resultCode == Activity.RESULT_OK) {

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            final StorageReference imageRef = storageRef.child("record")
                    .child(Utils.getUID())
                    .child(TV_file.getText().toString() + ".png");


            imageUploadDialog.show();
            switch (requestCode) {
                case Constant.CAMERA_REQUEST_CODE:

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(AddRecordActivity.this.getContentResolver(),
                                Uri.parse(mCurrentPhotoPath));


                        uploadPhoto(bitmap,imageRef);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    break;

                case 99:
                    Uri _uri = data.getData();

                    String filePath = getRealPathFromURI(AddRecordActivity.this,_uri);

                    if (filePath != null) {
                        InputStream stream = null;
                        try {

                            final StorageReference fileRef = storageRef.child("record")
                                    .child(Utils.getUID())
                                    .child(TV_file.getText().toString());
                            stream = new FileInputStream(new File(filePath));
                            UploadTask uploadTask = fileRef.putStream(stream);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            if (Utils.getUID() != null) {

                                                saveRecord(uri);
                                            }
                                        }
                                    });
                                }
                            });
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }


                    }



                    break;
            }
        }

    }

    private void saveRecord(Uri uri) {
        final Record record = new Record(ET_name.getText().toString(),
                uri.toString());

        user.getRecords().add(record);


        FirebaseDatabase.getInstance()
                .getReference()
                .child("user")
                .child(Utils.getUID())
                .child("records")
                .setValue(user.getRecords())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful()) {
                           TV_file.setText(ET_name.getText().toString());
                           imageUploadDialog.dismiss();
                       }
                    }
                });

    }

    private void uploadPhoto(Bitmap bitmap, final StorageReference imageRef) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] dataBytes = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(dataBytes);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(AddRecordActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        if (Utils.getUID() != null) {
                            saveRecord(uri);



                        }
                    }
                });
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constant.CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                checkForStoragePermissions();
            } else {
                Toast.makeText(AddRecordActivity.this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == Constant.STORAGE_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                startCameraIntent();
            } else {
                Toast.makeText(AddRecordActivity.this, "storage permission denied", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == Constant.STORAGE_PERMISSION_CODE2) {
            startUploadIntent();
        }
    }



    private void checkForStoragePermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (AddRecordActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    AddRecordActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        Constant.STORAGE_PERMISSION_CODE);
            } else {
                startCameraIntent();
            }
        } else {
            startCameraIntent();
        }


    }

    private void checkForStoragePermissions2() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (AddRecordActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    AddRecordActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        Constant.STORAGE_PERMISSION_CODE2);
            } else {
                startUploadIntent();
            }
        } else {
            startUploadIntent();
        }


    }



    private void startUploadIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 99);
    }
    private void startCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            photoFile = Utils.createImageFile();
            mCurrentPhotoPath = "file:" + photoFile.getAbsolutePath();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (photoFile != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(cameraIntent, Constant.CAMERA_REQUEST_CODE);
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
