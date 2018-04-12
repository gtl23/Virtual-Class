package com.sandfoil.virtualclass;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class FacultySpace extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_space);
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

    public void logoutFaculty(View view){
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Logged Out..", Toast.LENGTH_SHORT).show();
        Intent logoutFacultyIntent = new Intent(FacultySpace.this,MainActivity.class);
        logoutFacultyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        logoutFacultyIntent.putExtra("EXIT", true);
        startActivity(logoutFacultyIntent);
    }
}
