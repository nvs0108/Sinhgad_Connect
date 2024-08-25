package com.example.sinhgad_connect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MakeSelection extends AppCompatActivity {

    ImageView backBtn;
    Button smsOption, emailOption;
    TextView smsDesc, emailDesc;
    String username, email, phoneNo;
    String verificationCodeBySystem;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_make_selection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();

        // Retrieve the data from the intent
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        email = intent.getStringExtra("email");
        phoneNo = intent.getStringExtra("phoneNo");

        // Hooks
        backBtn = findViewById(R.id.forget_password_back_btn);
        smsOption = findViewById(R.id.sms_option);
        emailOption = findViewById(R.id.email_option);
        smsDesc = findViewById(R.id.mobile_desc);
        emailDesc = findViewById(R.id.mail_desc);

        // Display phone number and email
        smsDesc.setText(phoneNo);
        emailDesc.setText(email);

        // Back button click listener
        backBtn.setOnClickListener(v -> onBackPressed());

        // SMS option click listener
        smsOption.setOnClickListener(v -> {
            // Handle password reset via SMS and navigate to VerifyOTP activity
            sendOtp(phoneNo, "sms");
        });

        // Email option click listener
        emailOption.setOnClickListener(v -> {
            // Handle password reset via Email and navigate to VerifyOTP activity
            sendEmailOtp(email);
        });
    }

    private void sendOtp(String phoneNo, String method) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        String code = phoneAuthCredential.getSmsCode();
                        if (code != null) {
                            navigateToVerifyOtp(method, code);
                        }
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(MakeSelection.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCodeBySystem = s;
                        navigateToVerifyOtp(method, null);
                    }
                });
    }

    private void sendEmailOtp(String email) {
        String otp = generateOtp();
        String subject = "Your OTP Code";
        String messageBody = "Your OTP code is: " + otp;

        // Implement your email sending logic here

        Toast.makeText(MakeSelection.this, "OTP sent via Email", Toast.LENGTH_SHORT).show();
        navigateToVerifyOtp("email", otp);
    }

    private String generateOtp() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    private void navigateToVerifyOtp(String method, String otp) {
        Intent intent = new Intent(MakeSelection.this, VerifyOTP.class);
        intent.putExtra("method", method);
        intent.putExtra("otp", otp);
        intent.putExtra("username", username);
        intent.putExtra("email", email);
        intent.putExtra("phoneNo", phoneNo);
        intent.putExtra("verificationCodeBySystem", verificationCodeBySystem);
        startActivity(intent);
    }
}