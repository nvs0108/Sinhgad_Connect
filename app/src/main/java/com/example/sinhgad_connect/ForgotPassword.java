package com.example.sinhgad_connect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ForgotPassword extends AppCompatActivity {

    ImageView backBtn;
    TextInputLayout usernameInput;
    Button nextBtn;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        // Hooks
        backBtn = findViewById(R.id.forget_password_back_btn);
        usernameInput = findViewById(R.id.forget_password_phone_number);
        nextBtn = findViewById(R.id.forget_password_next_btn);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Back button click listener
        backBtn.setOnClickListener(v -> onBackPressed());

        // Next button click listener
        nextBtn.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String enteredUsername = usernameInput.getEditText().getText().toString().trim();

        if (!validateUsername(enteredUsername)) {
            return;
        }

        // Check if the username exists in the database
        Query checkUser = databaseReference.orderByChild("username").equalTo(enteredUsername);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String emailFromDB = snapshot.child(enteredUsername).child("email").getValue(String.class);
                    String phoneNoFromDB = snapshot.child(enteredUsername).child("phoneNo").getValue(String.class);

                    Intent intent = new Intent(ForgotPassword.this, MakeSelection.class);
                    intent.putExtra("username", enteredUsername);
                    intent.putExtra("email", emailFromDB);
                    intent.putExtra("phoneNo", phoneNoFromDB);
                    startActivity(intent);
                } else {
                    usernameInput.setError("No such user exists");
                    usernameInput.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ForgotPassword.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateUsername(String username) {
        String usernamePattern = "^[a-z0-9_]{4,20}$";
        if (username.isEmpty()) {
            usernameInput.setError("Username cannot be empty");
            return false;
        } else if (!username.matches(usernamePattern)) {
            usernameInput.setError("Username not valid");
            return false;
        } else {
            usernameInput.setError(null);
            return true;
        }
    }
}