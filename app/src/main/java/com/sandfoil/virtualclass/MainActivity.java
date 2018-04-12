package com.sandfoil.virtualclass;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getBooleanExtra("EXIT", false))
        {
            startActivity(new Intent(MainActivity.this,MainActivity.class));
            finish();
        }
    }

    public void openDesignationSelectionActivity(View view){
        Intent designationSelectionIntent = new Intent(this, Designation.class);
        startActivity(designationSelectionIntent);
    }

    public void openStudentLoginActivity(View view){
        startActivity(new Intent(this, StudentLogin.class));
    }

    public void openParentLoginActivity(View view){
        startActivity(new Intent(this, ParentLogin.class));
    }

    public void openFacultyLoginActivity (View view){
        startActivity(new Intent(this, FacultyLogin.class));
    }

    public void onBackPressed() {
        AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this)
                .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setMessage("Do you wish to exit ?");
        AlertDialog alert = build.create();
        alert.show();
    }
}
