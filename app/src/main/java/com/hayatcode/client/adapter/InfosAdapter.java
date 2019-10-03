package com.hayatcode.client.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.hayatcode.client.R;
import com.hayatcode.client.Utils;
import com.hayatcode.client.data.UserLocalStore;
import com.hayatcode.client.model.Contact;
import com.hayatcode.client.model.MedicalInfo;
import com.hayatcode.client.model.User;

import java.util.ArrayList;

public class InfosAdapter extends RecyclerView.Adapter<InfosAdapter.viewHolder> {

    Context context;
    ArrayList<MedicalInfo> medicalInfos;
    User user;
    UserLocalStore userLocalStore;

    public InfosAdapter(Context context, ArrayList<MedicalInfo> medicalInfos) {
        this.context = context;
        this.medicalInfos = medicalInfos;

        userLocalStore = new UserLocalStore(context);
        user = userLocalStore.getLoggedInUser();
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

            /*rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);

                    final ArrayAdapter<String> arrayAdapter =
                            new ArrayAdapter<String>(context,
                                    android.R.layout.simple_list_item_1);
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

                                int position;
                                MedicalInfo medicalInfo = medicalInfos.get(getAdapterPosition())
                                medicalInfos.remove(getAdapterPosition());
                                user.setMedInfos(medicalInfos);

                                if (Utils.getUID() != null) {
                                    FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("user")
                                            .child(Utils.getUID())
                                            .child("medInfos")
                                            .setValue(medicalInfos)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        userLocalStore.storeUserData(user);
                                                        notifyDataSetChanged();
                                                    }

                                                }
                                            });

                                }
                            }
                        }
                    });
                    builderSingle.show();
                }
            });*/


        }

    }


}
