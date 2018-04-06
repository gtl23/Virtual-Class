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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class ParentAuthentication extends AppCompatActivity implements View.OnClickListener{

    EditText mPhoneNumberField, mVerificationField;
    Button mStartButton, mVerifyButton, mResendButton;
    ImageView verifiedUser, notVerifiedUser;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;

    private static final String TAG = "ParentAuthentication";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_authentication);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        /* check whether the device is connected to the internet or not */

        if(!isConnected(ParentAuthentication.this)) buildDialog(ParentAuthentication.this).show();

        mPhoneNumberField =  findViewById(R.id.parent_phone_number_edittext);
        mVerificationField =  findViewById(R.id.parent_phone_otp_edittext);
        mStartButton = findViewById(R.id.parent_verification_sendotp_button);
        mVerifyButton = findViewById(R.id.parent_verification_verify_button);
        mResendButton = findViewById(R.id.parent_verification_resendotp_button);
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
        final ProgressDialog progressDialog = new ProgressDialog(ParentAuthentication.this);
        progressDialog.setMessage("Verifying....");
        progressDialog.show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = task.getResult().getUser();
                            progressDialog.dismiss();
                            notVerifiedUser.setVisibility(View.GONE);
                            verifiedUser.setVisibility(View.VISIBLE);
                            startActivity(new Intent(ParentAuthentication.this, CreateParentProfile.class));
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
            case R.id.parent_verification_sendotp_button:
                if (!validatePhoneNumber()) {
                    return;
                }
                mStartButton.setVisibility(View.GONE);
                mVerifyButton.setVisibility(View.VISIBLE);
                mResendButton.setVisibility(View.VISIBLE);
                startPhoneNumberVerification(mPhoneNumberField.getText().toString());
                break;
            case R.id.parent_verification_verify_button:
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVerificationField.setError("Cannot Be Empty.");
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.parent_verification_resendotp_button:
                resendVerificationCode(mPhoneNumberField.getText().toString(), mResendToken);
                break;
        }
    }
}
