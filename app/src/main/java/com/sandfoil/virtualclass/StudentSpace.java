package com.sandfoil.virtualclass;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sandfoil.virtualclass.virtual_class_model.Upload;

import java.util.ArrayList;
import java.util.List;

public class StudentSpace extends AppCompatActivity{

    ListView studentSpaceListView;
    List<Upload> assignmentList;
    DatabaseReference mDatabaseReference;
    String phone, stream, batch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_space);

        // Retrieving the authenticated phone number from login activity for getting the
        // batch and stream from STUDENT node'

        phone = getIntent().getExtras().getString("PHONE");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("STUDENT").child(phone);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                stream = dataSnapshot.child("stream").getValue(String.class);
                batch = dataSnapshot.child("batch").getValue(String.class);

                // shows assignment in the listview
                showAssignmentList(stream, batch);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Creating arraylist for the list view and setting clickListener to it.

        assignmentList = new ArrayList<>();
        studentSpaceListView = findViewById(R.id.studentSpaceListView);

        //adding a clicklistener on listview
        studentSpaceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //getting the upload
                Upload upload = assignmentList.get(i);

                //Opening the upload file in browser using the upload url
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(upload.getUrl()));
                startActivity(intent);
            }
        });


    }

    // shows assignment in listview

    private void showAssignmentList(String stream, String batch){
        DatabaseReference assignmentReference = FirebaseDatabase.getInstance().getReference("ASSIGNMENT")
                .child(stream).child(batch);

        assignmentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    assignmentList.add(upload);
                }

                String[] uploads = new String[assignmentList.size()];

                for (int i = 0; i < uploads.length; i++) {
                    uploads[i] = assignmentList.get(i).getName();
                }

                //displaying it to list
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, uploads);
                studentSpaceListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // onKeyDown minimises the app when back key is pressed

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK :
                moveTaskToBack(true);
                return true;
        }
        return false;
    }

    // creating and setting the contents for the onCreateOptionsMenu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                logoutStudent();
                return true;
        }
        return false;
    }

    // logout the current user

    private void logoutStudent(){
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Logged Out..", Toast.LENGTH_SHORT).show();
        Intent logoutFacultyIntent = new Intent(StudentSpace.this,MainActivity.class);
        logoutFacultyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        logoutFacultyIntent.putExtra("EXIT", true);
        startActivity(logoutFacultyIntent);
    }
}
