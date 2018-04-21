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
import com.sandfoil.virtualclass.virtual_class_model.STUDENT;

public class CreateStudentProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private FirebaseAuth mAuth;
    String uid;
    EditText createProfileStudentNameEditText;
    String createProfileStudentStream;
    EditText createProfileStudentBatchEditText;
    EditText createProfileStudentCollegeRollEditText;
    EditText createProfileStudentMotherEditText;
    EditText createProfileStudentFatherEditText;
    EditText createProfileStudentPasswordEditText;
    Button createProfileStudentCreateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_student_profile);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        /* setting the contents for the stream dropdown menu */

        Spinner createProfileStudentStreamSpinner = findViewById(R.id.create_profile_student_stream_spinner);
        createProfileStudentStreamSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> createProfileStudentStreamAdapter = ArrayAdapter.createFromResource(this,
                R.array.stream_array, android.R.layout.simple_spinner_item);

        createProfileStudentStreamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        createProfileStudentStreamSpinner.setAdapter(createProfileStudentStreamAdapter);

        /* retrieving the student's authenticated phone no. from the authentication activity
        *  and setting it to the textview for database purpose other variable's identification.
        */

        final TextView createProfileStudentPhoneTextView = findViewById(R.id.create_profile_student_phone_textview);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth != null){
            createProfileStudentPhoneTextView.setText(mAuth.getCurrentUser().getPhoneNumber());
        }
        createProfileStudentNameEditText = findViewById(R.id.create_profile_student_name_edittext);
        createProfileStudentBatchEditText = findViewById(R.id.create_profile_student_batch_edittext);
        createProfileStudentCollegeRollEditText = findViewById(R.id.create_profile_student_college_roll_edittext);
        createProfileStudentMotherEditText = findViewById(R.id.create_profile_student_mother_edittext);
        createProfileStudentFatherEditText = findViewById(R.id.create_profile_student_father_edittext);
        createProfileStudentPasswordEditText = findViewById(R.id.create_profile_student_password_edittext);
        createProfileStudentCreateButton = findViewById(R.id.create_profie_student_create_button);

        /* Initialize Firebase instance */

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference student_table = database.getReference("STUDENT");

        createProfileStudentCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog mDialog = new ProgressDialog(CreateStudentProfile.this);
                mDialog.setMessage("Creating your profile...");
                mDialog.show();

                student_table.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(createProfileStudentPhoneTextView.getText().toString()).exists()){
                            mDialog.dismiss();
                            Toast.makeText(CreateStudentProfile.this, "Already Registered.", Toast.LENGTH_SHORT).show();
                        } else{
                            mDialog.dismiss();
                            STUDENT student = new STUDENT(createProfileStudentNameEditText.getText().toString(),
                                    createProfileStudentStream, createProfileStudentBatchEditText.getText().toString(),
                                    createProfileStudentCollegeRollEditText.getText().toString(),
                                    createProfileStudentMotherEditText.getText().toString(),
                                    createProfileStudentFatherEditText.getText().toString(),
                                    createProfileStudentPasswordEditText.getText().toString(),
                                    uid = FirebaseAuth.getInstance().getCurrentUser().getUid());
                            student_table.child(createProfileStudentPhoneTextView.getText().toString()).setValue(student);
                            Toast.makeText(CreateStudentProfile.this, "Welcome On Board :)", Toast.LENGTH_SHORT).show();
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
        // An item was selected from stream spinner. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        Spinner createProfileStudentStreamSpinner = findViewById(R.id.create_profile_student_stream_spinner);
        if(pos == 0){
            TextView errorText = (TextView)createProfileStudentStreamSpinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
        } else {
            createProfileStudentStream = String.valueOf(parent.getItemAtPosition(pos));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
