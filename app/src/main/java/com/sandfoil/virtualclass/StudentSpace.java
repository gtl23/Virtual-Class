package com.sandfoil.virtualclass;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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

    private void logoutStudent(){
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Logged Out..", Toast.LENGTH_SHORT).show();
        Intent logoutFacultyIntent = new Intent(StudentSpace.this,MainActivity.class);
        logoutFacultyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        logoutFacultyIntent.putExtra("EXIT", true);
        startActivity(logoutFacultyIntent);
    }
}
