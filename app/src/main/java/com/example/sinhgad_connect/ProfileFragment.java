package com.example.sinhgad_connect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    TextInputLayout fullName, email, phoneNo, password;
    TextView fullNameLable, usernameLable, statusLabel;
    Button profileUpdateBtn, logoutBtn;
    FirebaseAuth firebaseAuth;

    String _EMAIL, _PHONE,_NAME, _USERNAME, _PASSWORD;

    DatabaseReference reference;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        reference = FirebaseDatabase.getInstance().getReference("users");
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize TextInputEditText views
        fullName = view.findViewById(R.id.full_name_profile);
        email = view.findViewById(R.id.email_profile);
        phoneNo = view.findViewById(R.id.phone_no_profile);
        password = view.findViewById(R.id.password_profile);
        fullNameLable = view.findViewById(R.id.fullname_field);
        usernameLable = view.findViewById(R.id.username_field);
        profileUpdateBtn = view.findViewById(R.id.profile_update_btn);
        logoutBtn = view.findViewById(R.id.logout_btn);
        statusLabel = view.findViewById(R.id.status_label);

        //show all user data
        showAllUserData();

        profileUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserDetails();
            }
        });


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutUser();
            }
        });

        return view;
    }

    private void showAllUserData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_prefs", getActivity().MODE_PRIVATE);

        _USERNAME = sharedPreferences.getString("username", "");
        _NAME = sharedPreferences.getString("name", "");
        _EMAIL = sharedPreferences.getString("email", "");
        _PHONE = sharedPreferences.getString("phoneNo", "");
        _PASSWORD = sharedPreferences.getString("password", "");

        fullNameLable.setText(_NAME);
        usernameLable.setText(_USERNAME);
        fullName.getEditText().setText(_NAME);
        email.getEditText().setText(_EMAIL);
        phoneNo.getEditText().setText(_PHONE);
        password.getEditText().setText(_PASSWORD);

        // Fetch admin status from Firebase
        reference.child(_USERNAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    boolean isAdmin = snapshot.child("admin").getValue(Boolean.class);
                    if (isAdmin) {
                        // User is an admin
                        statusLabel.setText("(admin)");
                    } else {
                        // User is not an admin
                        statusLabel.setText("(user)");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(getActivity(), "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signOutUser() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_prefs", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(getActivity(), Login.class);
        startActivity(intent);
        Toast.makeText(getActivity(), "Logged Out", Toast.LENGTH_SHORT).show();
        getActivity().finish(); // Close the ProfileFragment activity
    }

    public void updateUserDetails() {
        if (isNameChanged() || isPasswordChanged()) {
            Toast.makeText(getActivity(), "Profile Updated", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isPasswordChanged() {
        if (!_PASSWORD.equals(password.getEditText().getText().toString())) {
            reference.child(_USERNAME).child("password").setValue(password.getEditText().getText().toString());
            _PASSWORD = password.getEditText().getText().toString();
            return true;
        } else {
            return false;
        }
    }

    private boolean isNameChanged() {
        if (!_NAME.equals(fullName.getEditText().getText().toString())) {
            reference.child(_USERNAME).child("name").setValue(fullName.getEditText().getText().toString());
            _NAME = fullName.getEditText().getText().toString();
            return true;
        } else {
            return false;
        }
    }
}