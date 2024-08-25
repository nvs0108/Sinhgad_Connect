package com.example.sinhgad_connect;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    //Variables
    TextInputLayout regName, regUsername, regEmail, regPhoneNo, regPassword, regAdminCode;
    CheckBox regIsAdmin;
    Button regBtn, regToLoginBtn;
    DatabaseReference databaseReference;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();

        // Hooks to all xml elements in activity_sign_up.xml
        regName = findViewById(R.id.reg_name);
        regUsername = findViewById(R.id.reg_username);
        regEmail = findViewById(R.id.reg_email);
        regPhoneNo = findViewById(R.id.reg_phoneNo);
        regPassword = findViewById(R.id.reg_password);
        regBtn = findViewById(R.id.reg_btn);
        regToLoginBtn = findViewById(R.id.reg_login_btn);
        progressBar = findViewById(R.id.progress_bar_signup);
        regIsAdmin = findViewById(R.id.reg_is_admin);
        regAdminCode = findViewById(R.id.reg_admin_code);


        // Set up listener for admin checkbox
        regIsAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    regAdminCode.setVisibility(View.VISIBLE);
                } else {
                    regAdminCode.setVisibility(View.GONE);
                }
            }
        });

        // Set focus change listeners for real-time validation after focusing out of each field
        regName.getEditText().setOnFocusChangeListener(new ValidationFocusChangeListener(regName));
        regUsername.getEditText().setOnFocusChangeListener(new ValidationFocusChangeListener(regUsername));
        regEmail.getEditText().setOnFocusChangeListener(new ValidationFocusChangeListener(regEmail));
        regPhoneNo.getEditText().setOnFocusChangeListener(new ValidationFocusChangeListener(regPhoneNo));
        regPassword.getEditText().setOnFocusChangeListener(new ValidationFocusChangeListener(regPassword));


        //Save data in FireBase on button click
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //firebaseDatabase = FirebaseDatabase.getInstance();
                //databaseReference = firebaseDatabase.getReference("users");

                if (validateName() && validateUsername() && validateEmail() && validatePhoneNo() && validatePassword()) {
                    if (regIsAdmin.isChecked() && !validateAdminCode()) {
                        return;
                    }
                    //Get all the values
                    String name = regName.getEditText().getText().toString().trim();
                    String username = regUsername.getEditText().getText().toString().trim();
                    String email = regEmail.getEditText().getText().toString().trim();
                    String phoneNo = regPhoneNo.getEditText().getText().toString().trim();
                    String password = regPassword.getEditText().getText().toString().trim();
                    boolean isAdmin = regIsAdmin.isChecked();

                    // Start verification activity with phone number
                    Intent intent = new Intent(getApplicationContext(), VerifyPhoneNo.class);
                    intent.putExtra("name", name);
                    intent.putExtra("username", username);
                    intent.putExtra("email", email);
                    intent.putExtra("phoneNo", phoneNo);
                    intent.putExtra("password", password);
                    intent.putExtra("isAdmin", isAdmin);
                    startActivity(intent);

                    //Storing Data in firebase
                    //UserHelperClass helperClass = new UserHelperClass(name, username, email, phoneNo, password, isAdmin);
                    //databaseReference.child(username).setValue(helperClass);

                    //progressBar.setVisibility(View.VISIBLE);

                    //Toast.makeText(SignUp.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Navigate to login activity
        regToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, Login.class));
            }
        });
    }

    private class ValidationFocusChangeListener implements View.OnFocusChangeListener {
        private TextInputLayout view;

        private ValidationFocusChangeListener(TextInputLayout view) {
            this.view = view;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                if (view == regName) {
                    validateName();
                } else if (view == regUsername) {
                    validateUsername();
                } else if (view == regEmail) {
                    validateEmail();
                } else if (view == regPhoneNo) {
                    validatePhoneNo();
                } else if (view == regPassword) {
                    validatePassword();
                }
                // Add other validations as needed
            }
        }
    }


    private Boolean validateAdminCode() {
        String adminCode = regAdminCode.getEditText().getText().toString().trim();
        // Replace "YOUR_ADMIN_CODE" with your actual admin code check logic
        if (!adminCode.equals("123456")) {
            regAdminCode.setError("Incorrect admin code");
            return false;
        } else {
            regAdminCode.setError(null);
            return true;
        }
    }

    private Boolean validateName() {
        String val = regName.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            regName.setError("Name cannot be empty");
            return false;
        } else if (!val.matches("[a-zA-Z\\s]+")) { // Regular expression to allow only letters and spaces
            regName.setError("Only letters are allowed");
            return false;
        } else {
            regName.setError(null);
            return true;
        }
    }


    private Boolean validateUsername() {
        String val = regUsername.getEditText().getText().toString().trim();
        String Requirement = "^[a-z0-9_]{4,20}$";
        if (val.isEmpty()) {
            regUsername.setError("Username cannot be empty");
            return false;
        } else if (val.length() < 4 || val.length() > 20) {
            regUsername.setError("Username length must be between 4 and 20 characters");
            return false;
        } else if (!val.matches(Requirement)) {
            regUsername.setError("Username can only contain lowercase letters, digits, and underscores");
            return false;
        } else {
            regUsername.setError(null);
            return true;
        }
    }

    private Boolean validateEmail() {
        String val = regEmail.getEditText().getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()) {
            regEmail.setError("Email Address cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            regEmail.setError("Invalid email address");
            return false;
        } else {
            regEmail.setError(null);
            return true;
        }
    }

    private boolean validatePhoneNo() {
        String val = regPhoneNo.getEditText().getText().toString().trim();

        // Remove any existing prefix before checking length
        String phoneNumber = val.replaceAll("\\+91", "").replaceAll(" ", "");

        if (phoneNumber.isEmpty()) {
            regPhoneNo.setError("Phone number cannot be empty");
            return false;
        } else if (phoneNumber.length() != 10) {
            regPhoneNo.setError("Phone number must be 10 digits long");
            return false;
        } else {
            // Add "+91" prefix to the displayed text
            regPhoneNo.getEditText().setText("+91 " + phoneNumber);
            regPhoneNo.setError(null);
            return true;
        }
    }


    private Boolean validatePassword() {
        String val = regPassword.getEditText().getText().toString().trim();
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{6,}" +               //at least 6 characters
                "$";
        if (val.isEmpty()) {
            regPassword.setError("Password cannot be empty");
            regPassword.setEndIconDrawable(null); // Remove icon if password is empty
            return false;
        } else if (!val.matches(passwordVal)) {
            regPassword.setError("Password must contain at least one letter, one special character, and be at least 6 characters long");
            regPassword.setEndIconDrawable(null); // Remove icon if requirements are not met
            return false;
        } else {
            regPassword.setError(null);
            regPassword.setEndIconDrawable(getDrawable(R.drawable.ic_check)); // Set green tick icon
            return true;
        }
    }

}