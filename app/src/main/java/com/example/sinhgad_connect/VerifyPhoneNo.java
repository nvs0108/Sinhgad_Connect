package com.example.sinhgad_connect;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneNo extends AppCompatActivity {

    public static final int SMS_CONSENT_REQUEST = 2;
    private static final String TAG = "VerifyPhoneNo";
    String verificationCodeBySystem;
    Button verify_btn, resend_btn;
    EditText OTP;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    boolean isResending = false;

    String name, username, email, password, phoneNo;
    boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_no);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        verify_btn = findViewById(R.id.verify_btn);
        OTP = findViewById(R.id.otp);
        progressBar = findViewById(R.id.progress_bar);
        resend_btn = findViewById(R.id.resend_btn);

        progressBar.setVisibility(View.GONE);

        phoneNo = getIntent().getStringExtra("phoneNo");
        name = getIntent().getStringExtra("name");
        username = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        sendOTP(phoneNo);

        verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = OTP.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    OTP.setError("Enter valid code");
                    OTP.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        });

        SmsBroadcastReceiver.startSmsRetriever(this);

        resend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isResending) {
                    isResending = true;
                    sendOTP(phoneNo);
                } else {
                    Toast.makeText(VerifyPhoneNo.this, "Please wait before resending OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendOTP(String phoneNo) {

        // Disable resend button to prevent multiple clicks
        resend_btn.setEnabled(false);

        // Start countdown timer
        new CountDownTimer(60000, 1000) { // Countdown from 60 seconds
            public void onTick(long millisUntilFinished) {
                // Update resend button text with countdown
                resend_btn.setText("Resend OTP (" + millisUntilFinished / 1000 + "s)");
            }

            public void onFinish() {
                // Enable resend button and reset text
                resend_btn.setEnabled(true);
                resend_btn.setText("Resend OTP");
            }
        }.start();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,   // Phone number to verify
                60,                   // Timeout duration
                TimeUnit.SECONDS,     // Unit of timeout
                this,                 // Activity (for callback binding)
                mCallbacks);          // OnVerificationStateChangedCallbacks
    }


    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCodeBySystem = s;
            progressBar.setVisibility(View.GONE);
            Toast.makeText(VerifyPhoneNo.this, "OTP Sent", Toast.LENGTH_SHORT).show();
            isResending = false; // Reset flag after OTP is sent
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerifyPhoneNo.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            isResending = false; // Reset flag on verification failure
        }
    };

    private void verifyCode(String codeByUser) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
            signInTheUserWithCredentials(credential);
        } catch (Exception e) {
            Toast.makeText(VerifyPhoneNo.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void signInTheUserWithCredentials(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyPhoneNo.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Register user in Firebase database
                            registerUserInDatabase();

                            Intent intent = new Intent(VerifyPhoneNo.this, Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(VerifyPhoneNo.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void registerUserInDatabase() {
        UserHelperClass helperClass = new UserHelperClass(name, username, email, phoneNo, password, isAdmin);
        databaseReference.child(username).setValue(helperClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(VerifyPhoneNo.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(VerifyPhoneNo.this, "Failed to register user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SMS_CONSENT_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                // Get SMS message content
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                // Extract one-time code from the message and complete the verification
                Toast.makeText(this, "SMS received: " + message, Toast.LENGTH_SHORT).show();
            } else {
                // Consent was denied
                Toast.makeText(this, "Consent denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}