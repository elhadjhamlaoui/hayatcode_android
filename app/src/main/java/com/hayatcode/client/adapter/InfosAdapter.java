package com.hayatcode.client.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.hayatcode.client.R;
import com.hayatcode.client.utils.Static;
import com.hayatcode.client.utils.Utils;
import com.hayatcode.client.model.MedicalInfo;

import java.util.ArrayList;

public class InfosAdapter extends RecyclerView.Adapter<InfosAdapter.viewHolder> {

    Context context;
    ArrayList<MedicalInfo> medicalInfos;

    public InfosAdapter(Context context, ArrayList<MedicalInfo> medicalInfos) {
        this.context = context;
        this.medicalInfos = medicalInfos;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(context).inflate(R.layout.info_list_item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, int position) {

        MedicalInfo medicalInfo = medicalInfos.get(position);
        holder.label.setText(medicalInfo.getName());

    }

    @Override
    public int getItemCount() {
        return medicalInfos.size();
    }


    class viewHolder extends RecyclerView.ViewHolder {
        TextView label;
        ConstraintLayout rootLayout;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.label);
            rootLayout = itemView.findViewById(R.id.root);

            rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);

                    final ArrayAdapter<String> arrayAdapter =
                            new ArrayAdapter<String>(context,
                                    android.R.layout.simple_list_item_1);
                    arrayAdapter.add("Privacy level");

                    arrayAdapter.add("Delete");


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
                                android.app.AlertDialog.Builder builderSingle = new android.app.AlertDialog.Builder(context);

                                final ArrayAdapter<String> arrayAdapter =
                                        new ArrayAdapter<>(context,
                                                android.R.layout.select_dialog_singlechoice);

                                arrayAdapter.add("everyone");
                                arrayAdapter.add("everyone with the pin number");
                                arrayAdapter.add("trusted health providers");
                                arrayAdapter.add("only me");

                                final MedicalInfo medicalInfo = medicalInfos.get(getAdapterPosition());

                                int position = medicalInfo.getPrivacy();


                                builderSingle.setSingleChoiceItems(R.array.privacy,
                                        position, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Static.getUser(context).getMedInfos().get(medicalInfo.getId())
                                                        .setPrivacy(which);
                                                medicalInfo.setPrivacy(which);
                                                if (Utils.getUID() != null) {
                                                    FirebaseDatabase.getInstance()
                                                            .getReference()
                                                            .child("user")
                                                            .child(Utils.getUID())
                                                            .child("medInfos")
                                                            .child(medicalInfo.getId())
                                                            .setValue(medicalInfo)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Static.getUserLocalStore(context).storeUserData(Static.getUser(context));
                                                                        notifyDataSetChanged();

                                                                    }

                                                                }
                                                            });
                                                }
                                            }
                                        });
                                builderSingle.setPositiveButton(context.getString(R.string.ok), null);

                                builderSingle.show();
                            } else {
                                androidx.appcompat.app.AlertDialog.Builder builder =
                                        new androidx.appcompat.app.AlertDialog.Builder(context);
                                builder.setMessage(context.getString(R.string.delete_allert));
                                builder.setPositiveButton(context.getString(R.string.yes),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                final MedicalInfo medicalInfo = medicalInfos.get(getAdapterPosition());

                                                if (Utils.getUID() != null) {
                                                    FirebaseDatabase.getInstance()
                                                            .getReference()
                                                            .child("user")
                                                            .child(Utils.getUID())
                                                            .child("medInfos")
                                                            .child(medicalInfo.getId())
                                                            .setValue(null)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        medicalInfos.remove(getAdapterPosition());
                                                                        Static.getUser(context).getMedInfos().remove(medicalInfo.getId());
                                                                        Static.getUserLocalStore(context).storeUserData(Static.getUser(context));
                                                                        notifyDataSetChanged();
                                                                        if (medicalInfos.isEmpty()) {
                                                                            switch (medicalInfo.getType()) {
                                                                                case "disease":
                                                                                    Static.TV_diseases.setVisibility(View.GONE);
                                                                                    break;
                                                                                case "allergy":
                                                                                    Static.TV_allergies.setVisibility(View.GONE);
                                                                                    break;

                                                                                case "medication":
                                                                                    Static.TV_medications.setVisibility(View.GONE);
                                                                                    break;

                                                                            }

                                                                        }

                                                                        if (Static.getUser(context).getMedInfos().isEmpty())
                                                                            Static.IV_empty_emergency.setVisibility(View.VISIBLE);
                                                                        else
                                                                            Static.IV_empty_emergency.setVisibility(View.INVISIBLE);
                                                                    }

                                                                }
                                                            });

                                                }
                                            }
                                        });
                                builder.setNegativeButton(context.getString(R.string.no),
                                        null);
                                builder.show();

                            }
                        }
                    });
                    builderSingle.show();
                }
            });


        }

    }


}
