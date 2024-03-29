package com.hayatcode.client.ui.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import com.hayatcode.client.activity.AddInfoActivity;
import com.hayatcode.client.utils.Constant;
import com.hayatcode.client.R;
import com.hayatcode.client.utils.Static;
import com.hayatcode.client.utils.Utils;
import com.hayatcode.client.adapter.InfosAdapter;
import com.hayatcode.client.model.MedicalInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class EmergencyFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    RecyclerView recyclerViewMedications, recyclerViewAllergies, recyclerViewDiseases;

    InfosAdapter MedsAdapter, AllergiesAdapter, DiseasesAdapter;

    TextView TV_allgergies, TV_medications, TV_diseases;

    TextView TV_blood;
    Button BT_blood;
    ArrayList<MedicalInfo> allgergies, diseases, medications;
    FloatingActionButton BT_add;

    ImageView IV_empty;

    public static EmergencyFragment newInstance(int index) {
        EmergencyFragment fragment = new EmergencyFragment();
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
        View root = inflater.inflate(R.layout.fragment_emergency, container, false);

        recyclerViewAllergies = root.findViewById(R.id.recyclerViewAllergies);
        recyclerViewMedications = root.findViewById(R.id.recyclerViewMedications);
        recyclerViewDiseases = root.findViewById(R.id.recyclerViewDiseases);
        BT_add = root.findViewById(R.id.add);

        IV_empty = root.findViewById(R.id.empty);

        Static.IV_empty_emergency = IV_empty;

        TV_blood = root.findViewById(R.id.blood_type);
        BT_blood = root.findViewById(R.id.edit_blood);

        TV_allgergies = root.findViewById(R.id.allergies_label);
        TV_diseases = root.findViewById(R.id.diseases_label);
        TV_medications = root.findViewById(R.id.med_label);

        Static.TV_allergies = TV_allgergies;
        Static.TV_diseases = TV_diseases;
        Static.TV_medications = TV_medications;


        allgergies = new ArrayList<>();
        diseases = new ArrayList<>();
        medications = new ArrayList<>();

        MedsAdapter = new InfosAdapter(getActivity(), medications);
        AllergiesAdapter = new InfosAdapter(getActivity(), allgergies);
        DiseasesAdapter = new InfosAdapter(getActivity(), diseases);

        recyclerViewAllergies.setAdapter(AllergiesAdapter);
        recyclerViewMedications.setAdapter(MedsAdapter);
        recyclerViewDiseases.setAdapter(DiseasesAdapter);

        recyclerViewAllergies.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewMedications.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewDiseases.setLayoutManager(new LinearLayoutManager(getActivity()));


        BT_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), AddInfoActivity.class), 25);
            }
        });
        String blood = Static.getUser(getActivity()).getBlood().isEmpty() ? "O positive" : Static.getUser(getActivity()).getBlood();

        TV_blood.setText(blood);

        addItems();


        BT_blood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builderSingle = new android.app.AlertDialog.Builder(getActivity());

                final ArrayAdapter<String> arrayAdapter =
                        new ArrayAdapter<>(getActivity(),
                                android.R.layout.select_dialog_singlechoice);

                arrayAdapter.add("O positive");
                arrayAdapter.add("O negative");
                arrayAdapter.add("A positive");
                arrayAdapter.add("A negative");
                arrayAdapter.add("B positive");
                arrayAdapter.add("B negative");
                arrayAdapter.add("AB positive");
                arrayAdapter.add("AB negative");

                int position = 0;

                switch (Static.getUser(getActivity()).getBlood()) {
                    case "O positive":
                        position = 0;
                        break;
                    case "O negative":
                        position = 1;

                        break;
                    case "A positive":
                        position = 2;

                        break;
                    case "A negative":
                        position = 3;

                        break;
                    case "B positive":
                        position = 4;

                        break;
                    case "B negative":
                        position = 5;

                        break;
                    case "AB positive":
                        position = 6;

                        break;
                    case "AB negative":
                        position = 7;

                        break;
                }


                builderSingle.setSingleChoiceItems(R.array.blood, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String blood = arrayAdapter.getItem(which);
                        TV_blood.setText(blood);
                        Static.getUser(getActivity()).setBlood(blood);
                        FirebaseDatabase.getInstance().getReference().child("user")
                                .child(Utils.getUID()).child("blood").setValue(blood).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Static.getUserLocalStore(getActivity()).storeUserData(Static.getUser(getActivity()));

                                }
                            }
                        });
                    }
                });
                builderSingle.setPositiveButton(getString(R.string.ok), null);

                builderSingle.show();
            }
        });


        return root;
    }


    private void addItems() {

        HashMap<String, MedicalInfo> medicalInfos = Static.getUser(getActivity())
                .getMedInfos();

        diseases.clear();
        medications.clear();
        allgergies.clear();

        for (MedicalInfo medicalInfo : medicalInfos.values()) {
            if (medicalInfo.getType().equals("disease")) {
                diseases.add(medicalInfo);
            } else if (medicalInfo.getType().equals("medication")) {
                medications.add(medicalInfo);
            } else {
                allgergies.add(medicalInfo);
            }
        }

        if (!diseases.isEmpty())
            TV_diseases.setVisibility(View.VISIBLE);
        if (!allgergies.isEmpty())
            TV_allgergies.setVisibility(View.VISIBLE);
        if (!medications.isEmpty())
            TV_medications.setVisibility(View.VISIBLE);

        if (medicalInfos.isEmpty())
            IV_empty.setVisibility(View.VISIBLE);
        else
            IV_empty.setVisibility(View.INVISIBLE);

        MedsAdapter.notifyDataSetChanged();
        AllergiesAdapter.notifyDataSetChanged();
        DiseasesAdapter.notifyDataSetChanged();

        /*FirebaseDatabase.getInstance().getReference().child("user")
                .child(Utils.getUID())
                .child("medInfos")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        diseases.clear();
                        medications.clear();
                        allgergies.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            MedicalInfo medicalInfo = new MedicalInfo();
                            medicalInfo.setId(data.child("id").getValue().toString());
                            medicalInfo.setName(data.child("name").getValue().toString());
                            medicalInfo.setType(data.child("type").getValue().toString());

                            if (medicalInfo.getType().equals("disease")) {
                                diseases.add(medicalInfo);
                            } else if (medicalInfo.getType().equals("medication")) {
                                medications.add(medicalInfo);
                            } else {
                                allgergies.add(medicalInfo);
                            }
                        }


                        if (!diseases.isEmpty())
                            TV_diseases.setVisibility(View.VISIBLE);
                        if (!allgergies.isEmpty())
                            TV_allgergies.setVisibility(View.VISIBLE);
                        if (!medications.isEmpty())
                            TV_medications.setVisibility(View.VISIBLE);


                        MedsAdapter.notifyDataSetChanged();
                        AllergiesAdapter.notifyDataSetChanged();
                        DiseasesAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

         */

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 25) {
            if (resultCode == Constant.RESULT_SUCCESS) {
                Static.setUser(Static.getUserLocalStore(getActivity()).getLoggedInUser());
                addItems();
            }

        }
    }

}