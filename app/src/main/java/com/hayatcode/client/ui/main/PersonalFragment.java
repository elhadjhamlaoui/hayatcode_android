package com.hayatcode.client.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.hayatcode.client.ChangePasswordActivity;
import com.hayatcode.client.Constant;
import com.hayatcode.client.LoginActivity;
import com.hayatcode.client.OrderNewCode5;
import com.hayatcode.client.R;
import com.hayatcode.client.Utils;
import com.hayatcode.client.data.UserLocalStore;
import com.hayatcode.client.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class PersonalFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    EditText ET_firstName, ET_lastName, ET_email;
    TextView TV_birthdate, TV_gender, TV_logout, TV_change_pass;
    ImageView IV_userPhoto;
    User user;
    UserLocalStore userLocalStore;
    DatePickerDialog.OnDateSetListener dateSetListener;
    Calendar calendar;
    Uri imageUri;
    ProgressDialog imageUploadDialog;
    private String mCurrentPhotoPath;
    File photoFile = null;
    Button BT_save;

    ImageView IV_edit_firstname, IV_edit_lastname, IV_edit_gender, IV_edit_birthdate;



    public static PersonalFragment newInstance(int index) {
        PersonalFragment fragment = new PersonalFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_personal, container, false);


        ET_email = root.findViewById(R.id.email);
        ET_firstName = root.findViewById(R.id.firstname);
        ET_lastName = root.findViewById(R.id.lastname);
        TV_birthdate = root.findViewById(R.id.birthdate);
        TV_gender = root.findViewById(R.id.sex);
        IV_userPhoto = root.findViewById(R.id.account_picture);
        TV_logout = root.findViewById(R.id.logout);
        BT_save = root.findViewById(R.id.save);
        TV_change_pass = root.findViewById(R.id.change_pass);

        IV_edit_firstname = root.findViewById(R.id.edit_first_name);
        IV_edit_lastname = root.findViewById(R.id.edit_last_name);
        IV_edit_gender = root.findViewById(R.id.edit_gender);
        IV_edit_birthdate = root.findViewById(R.id.edit_birthdate);


        IV_edit_gender.setOnClickListener(this);
        IV_edit_birthdate.setOnClickListener(this);


        userLocalStore = new UserLocalStore(getActivity());
        user = userLocalStore.getLoggedInUser();


        TV_gender.setOnClickListener(this);
        TV_birthdate.setOnClickListener(this);
        TV_logout.setOnClickListener(this);
        IV_userPhoto.setOnClickListener(this);
        BT_save.setOnClickListener(this);
        TV_change_pass.setOnClickListener(this);


        IV_edit_firstname.setOnClickListener(this);
        IV_edit_lastname.setOnClickListener(this);
        IV_edit_gender.setOnClickListener(this);
        IV_edit_birthdate.setOnClickListener(this);


        calendar = Calendar.getInstance();

        dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                Date date = new Date(calendar.getTimeInMillis());

                user.setBirthDate((Utils.getReadableDate(date)));

                TV_birthdate.setText(Utils.getReadableDate(date));
                BT_save.setEnabled(true);

            }

        };

        imageUploadDialog = new ProgressDialog(getActivity());
        imageUploadDialog.setTitle(R.string.uploading_photo);

        //Glide.with(this).load(user.getPhoto()).into(IV_userPhoto);


        TV_gender.setText(user.getGender());
        TV_birthdate.setText(user.getBirthDate());

        ET_firstName.setHint(user.getFirstName());
        ET_lastName.setHint(user.getFamilyName());



        ET_firstName.setHint(user.getFirstName());
        ET_lastName.setHint(user.getFamilyName());
        ET_email.setHint(user.getEmail());


        //ET_firstName.setOnClickListener(this);
        ET_lastName.setOnClickListener(this);


        ET_firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                BT_save.setEnabled(true);
            }
        });

        ET_lastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                BT_save.setEnabled(true);
            }
        });


        return root;
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.change_pass:
                startActivity(new Intent(getActivity(),
                        ChangePasswordActivity.class));
                break;

            case R.id.account_picture:
                showPhotoDialog();
                break;

            case R.id.logout:

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.logout));
                builder.setPositiveButton(getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Utils.logout(getActivity(), userLocalStore);
                            }
                        });
                builder.setNegativeButton(getString(R.string.no),
                        null);
                builder.show();

                break;
            case R.id.save:
                if (validate())
                    saveUserInfos();
                break;



            case R.id.edit_first_name:
                enableEdit(ET_firstName);
                break;

            case R.id.edit_last_name:
                enableEdit(ET_lastName);
                break;


            case R.id.edit_gender:
                showGenderDialog();

                break;

            case R.id.edit_birthdate:
                showDatePicker();

                break;
        }
    }

    private void enableEdit(EditText editText) {
        editText.setEnabled(true);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

    }

    private boolean validate() {
        boolean validate = true;

        String firstname = ET_firstName.getText().toString();
        String lastname = ET_lastName.getText().toString();


        if (firstname.isEmpty()) {
            ET_firstName.setBackground(getResources().getDrawable(R.drawable.bac_edittext_red));
            validate = false;
        } else
            ET_firstName.setBackground(getResources().getDrawable(R.drawable.bac_edittext_white));

        if (lastname.isEmpty()) {
            ET_lastName.setBackground(getResources().getDrawable(R.drawable.bac_edittext_red));
            validate = false;
        } else
            ET_lastName.setBackground(getResources().getDrawable(R.drawable.bac_edittext_white));


        return validate;
    }

    private void saveUserInfos() {

        sendEditRequest();
        userLocalStore.storeUserData(user);
    }

    void showGenderDialog() {
        final String[] genders = {getString(R.string.male), getString(R.string.female)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.gender));
        builder.setItems(genders, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                user.setGender(genders[which]);
                TV_gender.setText(genders[which]);
                BT_save.setEnabled(true);

            }
        });
        builder.show();
    }

    void showPhotoDialog() {
        final String[] providers = {getString(R.string.camera), getString(R.string.gallery)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.change_photo));
        builder.setItems(providers, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {

                    pickFromCamera();
                } else {

                    pickFromGallery();
                }
            }
        });
        builder.show();
    }

    void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.confirm_change_name));
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });
        builder.setNegativeButton(R.string.no, null);

        builder.show();
    }

    void showDatePicker() {
        Calendar maxDate = Calendar.getInstance();
        maxDate.setTimeInMillis(System.currentTimeMillis());

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), dateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, Constant.GALLERY_REQUEST_CODE);
    }

    private void pickFromCamera() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
        // Result code is RESULT_OK only if the user selects an Image
        Bitmap bitmap;
        if (resultCode == Activity.RESULT_OK) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            StorageReference imageRef = storageRef.child("photo")
                    .child(Utils.getUID() + ".jpg");


            switch (requestCode) {
                case Constant.GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();

                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePath,
                            null, null, null);
                    cursor.moveToFirst();
                    String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    bitmap = BitmapFactory.decodeFile(imagePath, options);

                    IV_userPhoto.setImageBitmap(bitmap);
                    imageUploadDialog.show();


                    uploadPhoto(bitmap,imageRef);

                    cursor.close();
                    break;
                case Constant.CAMERA_REQUEST_CODE:

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),
                                Uri.parse(mCurrentPhotoPath));


                        IV_userPhoto.setImageBitmap(bitmap);
                        imageUploadDialog.show();
                        uploadPhoto(bitmap,imageRef);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    break;
            }
        }

    }

    private void uploadPhoto(Bitmap bitmap, StorageReference imageRef) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] dataBytes = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(dataBytes);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //
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
                Toast.makeText(getActivity(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == Constant.STORAGE_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                startCameraIntent();
            } else {
                Toast.makeText(getActivity(), "storage permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }



    private void checkForStoragePermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
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

    private void sendEditRequest() {


    }

}