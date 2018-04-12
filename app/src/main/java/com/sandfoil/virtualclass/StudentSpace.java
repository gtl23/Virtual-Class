package com.sandfoil.virtualclass;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class StudentSpace extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_space);
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

    public void logoutStudent(View view){
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Logged Out..", Toast.LENGTH_SHORT).show();
        Intent logoutStudentIntent = new Intent(StudentSpace.this,MainActivity.class);
        logoutStudentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        logoutStudentIntent.putExtra("EXIT", true);
        startActivity(logoutStudentIntent);
    }
}
