package com.hayatcode.client.ui.main;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hayatcode.client.AddRecordActivity;
import com.hayatcode.client.Constant;
import com.hayatcode.client.R;
import com.hayatcode.client.Utils;
import com.hayatcode.client.adapter.InfosAdapter;
import com.hayatcode.client.adapter.RecordsAdapter;
import com.hayatcode.client.data.UserLocalStore;
import com.hayatcode.client.model.MedicalInfo;
import com.hayatcode.client.model.Record;
import com.hayatcode.client.model.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class RecordsFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    RecyclerView recyclerView;

    RecordsAdapter recordsAdapter;
    File photoFile;
    ArrayList<Record> records;
    User user;
    UserLocalStore userLocalStore;
    Button BT_add;
    String mCurrentPhotoPath;

    public static RecordsFragment newInstance(int index) {
        RecordsFragment fragment = new RecordsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);

        userLocalStore = new UserLocalStore(getActivity());
        user = userLocalStore.getLoggedInUser();

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
        View root = inflater.inflate(R.layout.fragment_records, container, false);

        recyclerView = root.findViewById(R.id.recyclerView);
        BT_add = root.findViewById(R.id.add);

        records = new ArrayList<>();
        recordsAdapter = new RecordsAdapter(getActivity(), records);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setAdapter(recordsAdapter);

        addItems();


        BT_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivityForResult(new Intent(getActivity(), AddRecordActivity.class),45);
            }
        });

        return root;
    }


    private void addItems() {

        records.clear();
        user = userLocalStore.getLoggedInUser();
        records.addAll(user.getRecords());
        recordsAdapter.notifyDataSetChanged();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 45) {
            if (resultCode == Constant.RESULT_SUCCESS) {
                user = userLocalStore.getLoggedInUser();
                addItems();
            }

        }
    }
}