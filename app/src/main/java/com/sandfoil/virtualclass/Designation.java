package com.sandfoil.virtualclass;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;

public class Designation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designation);
    }

    public void openAuthenticationActivity(View view){
        RadioGroup selectDesignationRadioGroup = findViewById(R.id.selectDesignationRadioGroup);
//        int selectedRadioButtonId = selectDesignationRadioGroup.getCheckedRadioButtonId();

        switch (selectDesignationRadioGroup.getCheckedRadioButtonId()){
            case R.id.selectDesignationStudentRadioButton :
                Intent studentAuthenticationIntent = new Intent(this, StudentAuthentication.class);
                startActivity(studentAuthenticationIntent);
                break;
            case R.id.selectDesignationParentRadioButton :
                Intent parentAuthenticationIntent = new Intent(this, ParentAuthentication.class);
                startActivity(parentAuthenticationIntent);
                break;
            case R.id.selectDesignationFacultyRadioButton :
                Intent facultyAuthenticationIntent = new Intent(this, FacultyAuthentication.class);
                startActivity(facultyAuthenticationIntent);
                break;
            default:
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this)
                        .setCancelable(false).setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setTitle("Alert !").setMessage("Please select a suitable designation.");
                AlertDialog alert = alertBuilder.create();
                alert.show();
        }
    }
}
