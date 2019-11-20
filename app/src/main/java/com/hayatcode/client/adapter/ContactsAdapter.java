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
import com.hayatcode.client.data.UserLocalStore;
import com.hayatcode.client.model.Contact;
import com.hayatcode.client.model.User;
import com.hayatcode.client.utils.Static;
import com.hayatcode.client.utils.Utils;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.viewHolder> {

    Context context;
    ArrayList<Contact> contacts;
    User user;
    UserLocalStore userLocalStore;

    public ContactsAdapter(Context context, ArrayList<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;

        userLocalStore = new UserLocalStore(context);
        user = userLocalStore.getLoggedInUser();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.contact_list_item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, int position) {

        Contact contact = contacts.get(position);
        holder.label.setText(contact.getName());

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }


    class viewHolder extends RecyclerView.ViewHolder implements ActivityCompat.OnRequestPermissionsResultCallback {
        TextView label;
        ConstraintLayout rootLayout;

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            boolean permissionGranted = false;
            switch (requestCode) {
                case 9:
                    permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    break;
            }
            if (permissionGranted) {
                makeCall();
            } else {
                Toast.makeText(context, "You don't assign permission.", Toast.LENGTH_SHORT).show();
            }
        }

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
                    arrayAdapter.add("Call");
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

                                if (Build.VERSION.SDK_INT < 23) {
                                    makeCall();

                                } else {

                                    if (ActivityCompat.checkSelfPermission(context,
                                            Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                                        makeCall();
                                    } else {
                                        final String[] PERMISSIONS_STORAGE = {Manifest.permission.CALL_PHONE};
                                        //Asking request Permissions
                                        ActivityCompat.requestPermissions((AppCompatActivity) context, PERMISSIONS_STORAGE, 9);
                                    }
                                }


                            } else if (which == 1) {
                                android.app.AlertDialog.Builder builderSingle = new android.app.AlertDialog.Builder(context);

                                final ArrayAdapter<String> arrayAdapter =
                                        new ArrayAdapter<>(context,
                                                android.R.layout.select_dialog_singlechoice);

                                arrayAdapter.add("everyone");
                                arrayAdapter.add("everyone with the pin number");
                                arrayAdapter.add("trusted health providers");
                                arrayAdapter.add("only me");

                                int position = contacts.get(getAdapterPosition()).getPrivacy();


                                builderSingle.setSingleChoiceItems(R.array.privacy, position, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        contacts.get(getAdapterPosition()).setPrivacy(which);
                                        user.setContacts(contacts);
                                        if (Utils.getUID() != null) {
                                            FirebaseDatabase.getInstance()
                                                    .getReference()
                                                    .child("user")
                                                    .child(Utils.getUID())
                                                    .child("contacts")
                                                    .setValue(contacts).addOnCompleteListener(new OnCompleteListener<Void>() {
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

                                                contacts.remove(getAdapterPosition());
                                                user.setContacts(contacts);
                                                if (Utils.getUID() != null) {
                                                    FirebaseDatabase.getInstance()
                                                            .getReference()
                                                            .child("user")
                                                            .child(Utils.getUID())
                                                            .child("contacts")
                                                            .setValue(contacts).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                userLocalStore.storeUserData(user);

                                                                notifyDataSetChanged();
                                                                if (contacts.isEmpty())
                                                                    Static.IV_empty_contacts.setVisibility(View.VISIBLE);
                                                                else
                                                                    Static.IV_empty_contacts.setVisibility(View.INVISIBLE);
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

        @SuppressLint("MissingPermission")
        void makeCall() {
            String phone = contacts.get(getAdapterPosition()).getPhone();
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
            context.startActivity(intent);
        }
    }


}
