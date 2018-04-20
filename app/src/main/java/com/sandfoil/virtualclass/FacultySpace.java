package com.sandfoil.virtualclass;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sandfoil.virtualclass.virtual_class_model.Upload;

import java.text.DecimalFormat;

public class FacultySpace extends AppCompatActivity implements  AdapterView.OnItemSelectedListener, View.OnClickListener{

    final static int PICK_PDF_CODE = 2303;
    private static String STORAGE_UPLOAD_PATH;
    private static String DATABASE_UPLOAD_PATH;
    EditText facultySpaceFileNameEditText;
    EditText facultySpaceBatchEditText;
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;
    String facultySpaceStream;
    RadioGroup facultySpaceRadioGroup;
    Uri fileChoosed;
    Spinner facultySpaceStreamSpinner;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_space);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        /* setting the contents for the stream dropdown menu */

        facultySpaceStreamSpinner = findViewById(R.id.facultySpaceStreamSpinner);
        facultySpaceStreamSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> facultySpaceStreamSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.stream_array, android.R.layout.simple_spinner_item);

        facultySpaceStreamSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        facultySpaceStreamSpinner.setAdapter(facultySpaceStreamSpinnerAdapter);

        facultySpaceFileNameEditText = findViewById(R.id.facultySpaceFileNameEditText);
        facultySpaceBatchEditText = findViewById(R.id.facultySpaceBatchEditText);
        facultySpaceRadioGroup = findViewById(R.id.facultySpaceRadioGroup);
        findViewById(R.id.facultySpaceChooseFileButton).setOnClickListener(this);
        findViewById(R.id.facultySpaceSendButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.facultySpaceChooseFileButton :
                 getFile();
                 break;
            case R.id.facultySpaceSendButton :
                 chooseRecipientAndSendFile(fileChoosed);
                 break;
        }
    }

    private void getFile() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent chooseFileIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(chooseFileIntent);
            return;
        }

        //creating an intent for file chooser
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_PDF_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                fileChoosed = data.getData();
                facultySpaceFileNameEditText.setText(fileChoosed.getLastPathSegment());
                Toast.makeText(this, fileChoosed.getLastPathSegment() + " Choosed.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void chooseRecipientAndSendFile(final Uri fileChoosed){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.show();

        switch (facultySpaceRadioGroup.getCheckedRadioButtonId()){

            case R.id.facultySpaceStudentRadioButton :
                 if(validInputs(fileChoosed)){
                     sendFile("ASSIGNMENT");
                 }
                 break;

            case R.id.facultySpaceParentRadioButton :
                if(validInputs(fileChoosed)){
                    sendFile("PERFORMANCE");
                }
                break;

            case R.id.facultySpaceBothRadioButton :
                if(validInputs(fileChoosed)){
                    sendFile("ASSIGNMENT");
                    sendFile("PERFORMANCE");
                }
                break;

            default :
                if(fileChoosed == null){
                    mProgressDialog.dismiss();
                    fileNotChoosed();
                } else {
                    mProgressDialog.dismiss();
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this)
                            .setCancelable(false).setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).setTitle("Alert !").setMessage("Please choose a receiver !");
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                }
        }

    }

    private void sendFile(String recipient){
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(recipient)
                .child(facultySpaceStream).child(facultySpaceBatchEditText.getText().toString());
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mStorageReference.child(recipient + "/").child(facultySpaceStream + "/")
                .child(facultySpaceBatchEditText.getText().toString() + "/" + fileChoosed.getLastPathSegment())
                .putFile(fileChoosed)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mProgressDialog.dismiss();
                        Upload upload = new Upload(facultySpaceFileNameEditText.getText().toString(), taskSnapshot.getDownloadUrl().toString());
                        mDatabaseReference.child(mDatabaseReference.push().getKey()).setValue(upload);
                        resetFields();
                        Toast.makeText(FacultySpace.this, "File sent successfully.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressDialog.dismiss();
                        facultySpaceFileNameEditText.setText("");
                        Toast.makeText(FacultySpace.this, "Sending Failed.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                        mProgressDialog.setMessage(new DecimalFormat("##.#").format(progress) + "% Sending File...");
                    }
                });
    }

    private void fileNotChoosed(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this)
                .setCancelable(false).setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setTitle("Alert !").setMessage("No file chosen/Invalid field inputs !");
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void resetFields(){
        facultySpaceFileNameEditText.setText("");
        facultySpaceBatchEditText.setText("");
        facultySpaceStreamSpinner.setSelection(0);
    }

    private boolean validInputs(final Uri fileChoosed){
        if (fileChoosed == null || facultySpaceBatchEditText.getText().toString().length() != 9 || facultySpaceStreamSpinner.getSelectedItemPosition() == 0) {
            mProgressDialog.dismiss();
            fileNotChoosed();
            return false;
        }
        return true;
    }








    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected from stream spinner. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        Spinner facultySpaceStreamSpinner = findViewById(R.id.facultySpaceStreamSpinner);
        if(pos == 0){
            TextView errorText = (TextView)facultySpaceStreamSpinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);
        } else {
            facultySpaceStream = String.valueOf(parent.getItemAtPosition(pos));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK :
                moveTaskToBack(true);
                return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                logoutFaculty();
                return true;
        }
        return false;
    }

        private void logoutFaculty(){
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Logged Out..", Toast.LENGTH_SHORT).show();
        Intent logoutFacultyIntent = new Intent(FacultySpace.this,MainActivity.class);
        logoutFacultyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        logoutFacultyIntent.putExtra("EXIT", true);
        startActivity(logoutFacultyIntent);
    }
}