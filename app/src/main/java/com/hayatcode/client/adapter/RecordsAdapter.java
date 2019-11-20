package com.hayatcode.client.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.hayatcode.client.data.UserLocalStore;
import com.hayatcode.client.model.Record;
import com.hayatcode.client.model.User;

import java.util.ArrayList;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.viewHolder> {

    Context context;
    ArrayList<Record> records;
    User user;
    UserLocalStore userLocalStore;

    public RecordsAdapter(Context context, ArrayList<Record> records) {
        this.context = context;
        this.records = records;

        userLocalStore = new UserLocalStore(context);
        user = userLocalStore.getLoggedInUser();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.record_list_item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, int position) {

        Record record = records.get(position);
        holder.label.setText(record.getName());

    }

    @Override
    public int getItemCount() {
        return records.size();
    }


    class viewHolder extends RecyclerView.ViewHolder {
        TextView label;
        TextView view;
        ConstraintLayout rootLayout;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.label);
            rootLayout = itemView.findViewById(R.id.root);
            view = itemView.findViewById(R.id.view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(records.get(getAdapterPosition()).getUrl()));
                    context.startActivity(browserIntent);
                }
            });

            rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);

                    final ArrayAdapter<String> arrayAdapter =
                            new ArrayAdapter<String>(context,
                                    android.R.layout.simple_list_item_1);
                    arrayAdapter.add("View");
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
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(records.get(getAdapterPosition()).getUrl()));
                                context.startActivity(browserIntent);

                            } else if (which == 1) {
                                android.app.AlertDialog.Builder builderSingle = new android.app.AlertDialog.Builder(context);

                                final ArrayAdapter<String> arrayAdapter =
                                        new ArrayAdapter<>(context,
                                                android.R.layout.select_dialog_singlechoice);

                                arrayAdapter.add("everyone");
                                arrayAdapter.add("everyone with the pin number");
                                arrayAdapter.add("trusted health providers");
                                arrayAdapter.add("only me");

                                int position = records.get(getAdapterPosition()).getPrivacy();


                                builderSingle.setSingleChoiceItems(R.array.privacy, position, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        records.get(getAdapterPosition()).setPrivacy(which);
                                        user.setRecords(records);
                                        if (Utils.getUID() != null) {
                                            FirebaseDatabase.getInstance()
                                                    .getReference()
                                                    .child("user")
                                                    .child(Utils.getUID())
                                                    .child("records")
                                                    .setValue(records).addOnCompleteListener(new OnCompleteListener<Void>() {
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

                                                records.remove(getAdapterPosition());
                                                user.setRecords(records);

                                                if (Utils.getUID() != null) {
                                                    FirebaseDatabase.getInstance()
                                                            .getReference()
                                                            .child("user")
                                                            .child(Utils.getUID())
                                                            .child("records")
                                                            .setValue(records)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        userLocalStore.storeUserData(user);
                                                                        notifyDataSetChanged();
                                                                        if (records.isEmpty())
                                                                            Static.IV_empty_records.setVisibility(View.VISIBLE);
                                                                        else
                                                                            Static.IV_empty_records.setVisibility(View.INVISIBLE);
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
