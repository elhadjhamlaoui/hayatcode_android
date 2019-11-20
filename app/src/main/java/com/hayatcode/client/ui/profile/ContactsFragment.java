package com.hayatcode.client.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hayatcode.client.activity.AddContactActivity;
import com.hayatcode.client.utils.Constant;
import com.hayatcode.client.R;
import com.hayatcode.client.adapter.ContactsAdapter;
import com.hayatcode.client.data.UserLocalStore;
import com.hayatcode.client.model.Contact;
import com.hayatcode.client.model.User;
import com.hayatcode.client.utils.Static;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContactsFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    RecyclerView recyclerView;
    ContactsAdapter contactsAdapter;
    ArrayList<Contact> contacts = new ArrayList<>();

    User user;
    UserLocalStore userLocalStore;
    FloatingActionButton BT_add;
    ImageView IV_empty;

    public static ContactsFragment newInstance(int index) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userLocalStore = new UserLocalStore(getActivity());
        user = userLocalStore.getLoggedInUser();

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
        View root = inflater.inflate(R.layout.fragment_contacts, container, false);

        recyclerView = root.findViewById(R.id.recyclerViewDideases);
        BT_add = root.findViewById(R.id.add);
        IV_empty = root.findViewById(R.id.empty);

        Static.IV_empty_contacts = IV_empty;

        contacts = user.getContacts();

        if (contacts.isEmpty())
            IV_empty.setVisibility(View.VISIBLE);
        else
            IV_empty.setVisibility(View.INVISIBLE);

        contactsAdapter = new ContactsAdapter(getActivity(), contacts);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(contactsAdapter);

        BT_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), AddContactActivity.class), 15);
            }
        });



        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==15)
        {
            if (resultCode == Constant.RESULT_SUCCESS) {
                user = userLocalStore.getLoggedInUser();

                addItems();
            }

        }
    }

    private void addItems() {
        contacts.clear();
        contacts.addAll(user.getContacts());
        contactsAdapter.notifyDataSetChanged();

        if (contacts.isEmpty())
            IV_empty.setVisibility(View.VISIBLE);
        else
            IV_empty.setVisibility(View.INVISIBLE);

    }
}