package com.sandfoil.virtualclass;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class FacultyAuthentication extends AppCompatActivity implements View.OnClickListener{

    private EditText mPhoneNumberField, mVerificationField;
    private Button mStartButton, mVerifyButton, mResendButton;
    private ImageView verifiedUser, notVerifiedUser;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;

    private static final String TAG = "FacultyAuthentication";
    private final String countryCode = "+91";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_authentication);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        /* check whether the device is connected to the internet or not */

        if(!isConnected(FacultyAuthentication.this)) buildDialog(FacultyAuthentication.this).show();

        mPhoneNumberField =  findViewById(R.id.faculty_phone_number_edittext);
        mVerificationField =  findViewById(R.id.faculty_phone_otp_edittext);
        mStartButton = findViewById(R.id.faculty_verification_sendotp_button);
        mVerifyButton = findViewById(R.id.faculty_verification_verify_button);
        mResendButton = findViewById(R.id.faculty_verification_resendotp_button);
        notVerifiedUser = findViewById(R.id.not_verified_user);
        verifiedUser = findViewById(R.id.verified_user);

        mResendButton.setVisibility(View.GONE);
        mVerifyButton.setVisibility(View.GONE);
        notVerifiedUser.setVisibility(View.VISIBLE);
        verifiedUser.setVisibility(View.GONE);

        mStartButton.setOnClickListener(this);
        mVerifyButton.setOnClickListener(this);
        mResendButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    mPhoneNumberField.setError("Invalid Phone Number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    mPhoneNumberField.setError("Too Many Requests.");
                }
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
    }

    /* isConnected() function gets the status of connectivity */

    public boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            return ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()));
        } else
            return false;

    }

    /* shows the alert dialog message on the basis of internet connectivity status */

    public AlertDialog.Builder buildDialog(Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection !!");
        builder.setMessage("You need to have mobile data or wifi turned on.");
        builder.setIcon(R.drawable.alert_icon);
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        return builder;
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        final ProgressDialog progressDialog = new ProgressDialog(FacultyAuthentication.this);
        progressDialog.setMessage("Verifying....");
        progressDialog.show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            progressDialog.dismiss();
                            notVerifiedUser.setVisibility(View.GONE);
                            verifiedUser.setVisibility(View.VISIBLE);
                            startActivity(new Intent(FacultyAuthentication.this, CreateFacultyProfile.class));
                            finish();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                progressDialog.dismiss();
                                mVerificationField.setError("Invalid Code.");
                            }
                        }
                    }
                });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, 60,  TimeUnit.SECONDS,   this,  mCallbacks);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, 60,   TimeUnit.SECONDS,  this,  mCallbacks, token);
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = mPhoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() != 10) {
            mPhoneNumberField.setError("Invalid Phone Number.");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.faculty_verification_sendotp_button:
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference faculty_table = database.getReference("FACULTY");
                faculty_table.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!validatePhoneNumber()){
//                            return;
                        } else if (dataSnapshot.child(countryCode + mPhoneNumberField.getText().toString()).exists()) {
                            Toast.makeText(FacultyAuthentication.this, "Already Registered.", Toast.LENGTH_SHORT).show();
//                            return;
                        } else {
                            mStartButton.setVisibility(View.GONE);
                            mVerifyButton.setVisibility(View.VISIBLE);
                            mResendButton.setVisibility(View.VISIBLE);
                            startPhoneNumberVerification(mPhoneNumberField.getText().toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                break;
            case R.id.faculty_verification_verify_button:
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVerificationField.setError("Cannot Be Empty.");
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.faculty_verification_resendotp_button:
                resendVerificationCode(mPhoneNumberField.getText().toString(), mResendToken);
                break;
        }
    }
}
