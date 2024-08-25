package com.example.sinhgad_connect;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.chaos.view.PinView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyOTP extends AppCompatActivity {

    ImageView closeBtn;
    PinView pinView;
    Button verifyBtn, resendBtn;
    String verificationCodeBySystem, method, username, email, phoneNo;
    FirebaseAuth firebaseAuth;
    String verificationId;
    boolean isResending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();

        // Retrieve the data from the intent
        Intent intent = getIntent();
        method = intent.getStringExtra("method");
        username = intent.getStringExtra("username");
        email = intent.getStringExtra("email");
        phoneNo = intent.getStringExtra("phoneNo");
        verificationCodeBySystem = intent.getStringExtra("verificationCodeBySystem");

        // Hooks
        closeBtn = findViewById(R.id.close_btn);
        pinView = findViewById(R.id.pin_view);
        verifyBtn = findViewById(R.id.verify_btn);
        resendBtn = findViewById(R.id.resend_btn);

        // Close button click listener
        closeBtn.setOnClickListener(v -> finish());

        // Verify button click listener
        verifyBtn.setOnClickListener(v -> {
            String otp = pinView.getText().toString();
            if (otp.isEmpty() || otp.length() < 6) {
                Toast.makeText(VerifyOTP.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
            } else {
                // Verify the OTP here
                verifyOTP(otp);
            }
        });

        // Resend button click listener
        resendBtn.setOnClickListener(v -> {
            if (!isResending) {
                isResending = true;
                resendOTP(phoneNo);
            } else {
                Toast.makeText(VerifyOTP.this, "Please wait before resending OTP", Toast.LENGTH_SHORT).show();
            }
        });

        // Send initial OTP
        sendOTP(phoneNo);
    }

    // Method to send OTP
    private void sendOTP(String phoneNo) {
        resendBtn.setEnabled(false);
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                resendBtn.setText("Resend OTP (" + millisUntilFinished / 1000 + "s)");
            }

            public void onFinish() {
                resendBtn.setEnabled(true);
                resendBtn.setText("Resend OTP");
            }
        }.start();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks
        );
    }

    // Method to resend OTP
    private void resendOTP(String phoneNumber) {
        if (mResendToken != null) {
            resendBtn.setEnabled(false);
            new CountDownTimer(60000, 1000) {
                public void onTick(long millisUntilFinished) {
                    resendBtn.setText("Resend OTP (" + millisUntilFinished / 1000 + "s)");
                }

                public void onFinish() {
                    resendBtn.setEnabled(true);
                    resendBtn.setText("Resend OTP");
                }
            }.start();

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,
                    60,
                    TimeUnit.SECONDS,
                    this,
                    mCallbacks,
                    mResendToken
            );
        } else {
            Toast.makeText(this, "Unable to resend OTP at the moment", Toast.LENGTH_SHORT).show();
        }
    }

    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            signInWithCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerifyOTP.this, "Resend OTP failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            isResending = false;
        }

        @Override
        public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(newVerificationId, forceResendingToken);
            verificationId = newVerificationId;
            mResendToken = forceResendingToken;
            Toast.makeText(VerifyOTP.this, "OTP Resent", Toast.LENGTH_SHORT).show();
            isResending = false;
        }
    };

    private void verifyOTP(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, otp);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent;
                        if ("sms".equals(method)) {
                            intent = new Intent(VerifyOTP.this, SetNewPassword.class);
                            intent.putExtra("username", username);
                        } else if ("email".equals(method)) {
                            intent = new Intent(VerifyOTP.this, Login.class);
                        } else {
                            Toast.makeText(VerifyOTP.this, "Unknown verification method", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(VerifyOTP.this, "Verification failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
