package com.hayatcode.client.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hayatcode.client.R;
import com.hayatcode.client.adapter.InfosAdapter;
import com.hayatcode.client.adapter.RecordsAdapter;
import com.hayatcode.client.data.UserLocalStore;
import com.hayatcode.client.model.MedicalInfo;
import com.hayatcode.client.model.Record;
import com.hayatcode.client.model.User;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class RecordsFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    RecyclerView recyclerView;

    RecordsAdapter recordsAdapter;

    ArrayList<Record> records;
    User user;
    UserLocalStore userLocalStore;
    Button BT_add;
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

        return root;
    }


    private void addItems() {

        records.clear();


        records.add(new Record("blood test 10/03/2018", ""));
        records.add(new Record("Physical Exam", ""));
        records.add(new Record("Laboratory Data", ""));
        records.add(new Record("Daily Progress Note", ""));

        recordsAdapter.notifyDataSetChanged();

    }
}