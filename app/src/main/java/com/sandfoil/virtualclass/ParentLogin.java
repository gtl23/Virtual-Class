package com.sandfoil.virtualclass;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sandfoil.virtualclass.virtual_class_model.PARENT;

public class ParentLogin extends AppCompatActivity {

    EditText parentLoginPhoneEditText, parentLoginPasswordEditText;
    final private String countryCode = "+91";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_login);

        /* check whether the device is connected to the internet or not */

        if(!isConnected(ParentLogin.this)) buildDialog(ParentLogin.this).show();

        parentLoginPhoneEditText = findViewById(R.id.parent_login_phone_edittext);
        parentLoginPasswordEditText = findViewById(R.id.parent_login_password_edittext);
    }

    /* isConnected() function gets the status of connectivity */

    public boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            return ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()));
        } else{
            return false;
        }

    }

    /* shows the alert dialog message on the basis of internet connectivity status */

    public AlertDialog.Builder buildDialog(Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection !!");
        builder.setMessage("You need to have mobile data or wifi turned on.");
        builder.setIcon(R.drawable.alert_icon);
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        return builder;
    }

        /* create instance of firebase database */

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference parent_table = database.getReference("PARENT");

        /* initiate ph. no. and password retrieval from firebase, verify and login */

        public void parentLogin(View view){
            if(parentLoginPhoneEditText.getText().toString().length() != 10){
                parentLoginPhoneEditText.setError("Invalid Phone Number.");
            } else {
                final ProgressDialog dialog = new ProgressDialog(ParentLogin.this);
                dialog.setMessage("Logging in...");
                dialog.show();
                parent_table.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        /* check if user exists, if exists() then compare password from database */
                        /* using the instance of STUDENT class, and take necessary action */

                        if(dataSnapshot.child(countryCode + parentLoginPhoneEditText.getText().toString()).exists()){
                            dialog.dismiss();
                            PARENT parent = dataSnapshot.child(countryCode + parentLoginPhoneEditText.getText().toString()).getValue(PARENT.class);
                            if(parent.getPassword().equals(parentLoginPasswordEditText.getText().toString())){
                                Toast.makeText(ParentLogin.this, "Welcome to virtual class.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ParentLogin.this, ParentSpace.class));
                                finish();
                            } else {
                                Toast.makeText(ParentLogin.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            dialog.dismiss();
                            Toast.makeText(ParentLogin.this, "Account doesn't exist !!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
}
