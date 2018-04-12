package com.sandfoil.virtualclass;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sandfoil.virtualclass.virtual_class_model.PARENT;

public class CreateParentProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private FirebaseAuth mAuth;
    EditText createProfileParentNameEditText;
    EditText CreateProfileParentChildEditText;
    String createProfileParentStream;
    EditText createProfileParentBatchEditText;
    EditText createProfileParentCollegeRollEditText;
    EditText createProfileParentPasswordEditText;
    Button createProfileParentCreateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_parent_profile);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        /* setting the contents for the stream dropdown menu */

        Spinner createProfileParentStreamSpinner = findViewById(R.id.create_profile_parent_stream_spinner);
        createProfileParentStreamSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> createProfileParentStreamAdapter = ArrayAdapter.createFromResource(this,
                R.array.stream_array, android.R.layout.simple_spinner_item);

        createProfileParentStreamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        createProfileParentStreamSpinner.setAdapter(createProfileParentStreamAdapter);

        /* retrieving the parent's authenticated phone no. from the authentication activity
         *  and setting it to the textview for database purpose other variable's identification.
         */

        final TextView createProfileParentPhoneTextView = findViewById(R.id.create_profile_parent_phone_textview);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth != null){
            createProfileParentPhoneTextView.setText(mAuth.getCurrentUser().getPhoneNumber());
        }
        createProfileParentNameEditText = findViewById(R.id.create_profile_parent_name_edittext);
        CreateProfileParentChildEditText = findViewById(R.id.create_profile_parent_child_edittext);
        createProfileParentBatchEditText = findViewById(R.id.create_profile_parent_batch_edittext);
        createProfileParentCollegeRollEditText = findViewById(R.id.create_profile_parent_college_roll_edittext);
        createProfileParentPasswordEditText = findViewById(R.id.create_profile_parent_password_edittext);
        createProfileParentCreateButton = findViewById(R.id.create_profile_parent_create_button);

        /* Initialize Firebase instance */

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference parent_table = database.getReference("PARENT");

        createProfileParentCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog mDialog = new ProgressDialog(CreateParentProfile.this);
                mDialog.setMessage("Creating your profile...");
                mDialog.show();

                parent_table.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(createProfileParentPhoneTextView.getText().toString()).exists()){
                             mDialog.dismiss();
                            Toast.makeText(CreateParentProfile.this, "Already Registered.", Toast.LENGTH_SHORT).show();
                        } else {
                            mDialog.dismiss();
                            PARENT parent = new PARENT(createProfileParentNameEditText.getText().toString(),
                                    CreateProfileParentChildEditText.getText().toString(), createProfileParentStream,
                                    createProfileParentBatchEditText.getText().toString(),
                                    createProfileParentCollegeRollEditText.getText().toString(),
                                    createProfileParentPasswordEditText.getText().toString());
                            parent_table.child(createProfileParentPhoneTextView.getText().toString()).setValue(parent);
                            Toast.makeText(CreateParentProfile.this, "Welcome On Board :)", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        Spinner createProfileParentStreamSpinner = findViewById(R.id.create_profile_parent_stream_spinner);
        if(pos == 0){
            TextView errorText = (TextView)createProfileParentStreamSpinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
        } else {
            createProfileParentStream = String.valueOf(parent.getItemAtPosition(pos));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
