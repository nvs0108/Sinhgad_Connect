package com.example.sinhgad_connect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SetNewPassword extends AppCompatActivity {

    TextInputLayout newPasswordLayout, confirmPasswordLayout;
    TextInputEditText newPasswordEditText, confirmPasswordEditText;
    Button okButton;
    FirebaseAuth firebaseAuth;
    DatabaseReference usersRef;

    String _USERNAME, _PASSWORD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_password);

        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        newPasswordLayout = findViewById(R.id.new_password_layout);
        confirmPasswordLayout = findViewById(R.id.confirm_password_layout);
        newPasswordEditText = findViewById(R.id.new_password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        okButton = findViewById(R.id.ok_button);

        okButton.setOnClickListener(v -> {
            String newPassword = newPasswordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();
            String username = getIntent().getStringExtra("username");

            if (validatePassword(newPassword, confirmPassword)) {
                resetPassword(newPassword);
            }
        });
    }

    private boolean validatePassword(String newPassword, String confirmPassword) {
        if (TextUtils.isEmpty(newPassword)) {
            newPasswordLayout.setError("Please enter new password");
            newPasswordEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordLayout.setError("Please confirm password");
            confirmPasswordEditText.requestFocus();
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordLayout.setError("Passwords do not match");
            confirmPasswordEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void resetPassword(String newPassword) {

        String username = getIntent().getStringExtra("username");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(username).child("password").setValue(newPassword);

        startActivity(new Intent(getApplicationContext(), SuccessMessage.class));
        finish();
    }

}




