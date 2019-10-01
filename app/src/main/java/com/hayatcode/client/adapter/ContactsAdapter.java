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

import com.google.firebase.database.FirebaseDatabase;
import com.hayatcode.client.R;
import com.hayatcode.client.Utils;
import com.hayatcode.client.data.UserLocalStore;
import com.hayatcode.client.model.Contact;
import com.hayatcode.client.model.User;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.viewHolder>  {

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



    class viewHolder extends RecyclerView.ViewHolder implements ActivityCompat.OnRequestPermissionsResultCallback{
        TextView label;
        ConstraintLayout rootLayout;

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            boolean permissionGranted = false;
            switch(requestCode){
                case 9:
                    permissionGranted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    break;
            }
            if(permissionGranted){
                makeCall();
            }else {
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
                                    android.R.layout.select_dialog_singlechoice);
                    arrayAdapter.add("Call");
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

                                }else {

                                    if (ActivityCompat.checkSelfPermission(context,
                                            Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                                        makeCall();
                                    }else {
                                        final String[] PERMISSIONS_STORAGE = {Manifest.permission.CALL_PHONE};
                                        //Asking request Permissions
                                        ActivityCompat.requestPermissions((AppCompatActivity)context, PERMISSIONS_STORAGE, 9);
                                    }
                                }



                            } else {
                                contacts.remove(getAdapterPosition());
                                user.setContacts(contacts);
                                userLocalStore.storeUserData(user);
                                if (Utils.getUID() != null) {
                                    FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("user")
                                            .child(Utils.getUID())
                                            .child("contacts")
                                            .setValue(contacts);
                                }
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
