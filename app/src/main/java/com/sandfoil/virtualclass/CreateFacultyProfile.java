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
import com.sandfoil.virtualclass.virtual_class_model.FACULTY;

public class CreateFacultyProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private FirebaseAuth mAuth;
    EditText createProfileFacultyNameEditText;
    String createProfileFacultyDepartment;
    EditText createProfileFacultyPasswordEditText;
    Button createProfileFacultyCreateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_faculty_profile);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Spinner createProfileFacultyDepartmentSpinner = findViewById(R.id.create_profile_faculty_department_spinner);
        createProfileFacultyDepartmentSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> createProfileFacultyDepartmentAdapter = ArrayAdapter.createFromResource(this,
                R.array.department_array, android.R.layout.simple_spinner_item);

        createProfileFacultyDepartmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        createProfileFacultyDepartmentSpinner.setAdapter(createProfileFacultyDepartmentAdapter);

        /* retrieving the student's authenticated phone no. from the authentication activity
         *  and setting it to the textview for database purpose other variable's identification.
         */

        final TextView createProfileFacultyPhoneTextView = findViewById(R.id.create_profile_faculty_phone_textview);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth != null){
            createProfileFacultyPhoneTextView.setText(mAuth.getCurrentUser().getPhoneNumber());
        }
        createProfileFacultyNameEditText = findViewById(R.id.create_profile_faculty_name_edittext);
        createProfileFacultyPasswordEditText = findViewById(R.id.create_profile_faculty_password_edittext);
        createProfileFacultyCreateButton = findViewById(R.id.create_profile_faculty_create_button);

        /* Initialize Firebase instance */

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference faculty_table = database.getReference("FACULTY");

        createProfileFacultyCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog mDialog = new ProgressDialog(CreateFacultyProfile.this);
                mDialog.setMessage("Creating your profile...");
                mDialog.show();

                faculty_table.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(createProfileFacultyPhoneTextView.getText().toString()).exists()){
                            mDialog.dismiss();
                            Toast.makeText(CreateFacultyProfile.this, "Already Registered.", Toast.LENGTH_SHORT).show();
                        } else {
                            FACULTY faculty = new FACULTY(createProfileFacultyNameEditText.getText().toString(),
                                                          createProfileFacultyDepartment,
                                                          createProfileFacultyPasswordEditText.getText().toString());
                            faculty_table.child(createProfileFacultyPhoneTextView.getText().toString()).setValue(faculty);
                            Toast.makeText(CreateFacultyProfile.this, "Welcome On Board :)", Toast.LENGTH_SHORT).show();
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
        Spinner createProfileFacultyDepartmentSpinner = findViewById(R.id.create_profile_faculty_department_spinner);
        if(pos == 0){
            TextView errorText = (TextView)createProfileFacultyDepartmentSpinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
        } else {
            createProfileFacultyDepartment = String.valueOf(parent.getItemAtPosition(pos));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
