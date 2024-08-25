package com.example.sinhgad_connect;

import android.os.Bundle;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    Button callSignUp, login_btn;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    ImageView image;
    TextView logoText, sloganText;
    TextInputLayout username, password;
    TextView passwordError;
    TextView networkStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check for internet connectivity
        networkStatus = findViewById(R.id.network_status); // Initialize networkStatus TextView

        if (!isConnected()) {
            networkStatus.setVisibility(View.VISIBLE);
            return;
        } else {
            networkStatus.setVisibility(View.GONE);
        }

        //Initialize Firebase Auth
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");


        // Check if user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString("username", null);
        boolean savedAdmin = sharedPreferences.getBoolean("admin", false);

        if (savedUsername != null) {
            navigateToDashboard(savedUsername, savedAdmin);
            return;
        }

        // Hooks
        callSignUp = findViewById(R.id.signup_screen);
        image = findViewById(R.id.logo_image);
        logoText = findViewById(R.id.logo_name);
        sloganText = findViewById(R.id.slogan_name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.login_btn);
        passwordError = findViewById(R.id.password_error);


        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open SignUp activity
                navigateToSignUp();
            }
        });

        // Handle login button click
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }


    private void navigateToSignUp() {
        Intent intent = new Intent(Login.this, SignUp.class);

        // Attach all the elements those you want to animate in design
        Pair[] pairs = new Pair[7];
        pairs[0] = new Pair<>(image, "logo_image");
        pairs[1] = new Pair<>(logoText, "logo_text");
        pairs[2] = new Pair<>(sloganText, "logo_desc");
        pairs[3] = new Pair<>(username, "username_tran");
        pairs[4] = new Pair<>(password, "password_tran");
        pairs[5] = new Pair<>(login_btn, "button_tran");
        pairs[6] = new Pair<>(callSignUp, "login_signup_tran");

        //wrap the call in API level 21 or higher
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    public void loginUser() {
        // Validate Login Info
        if (!validateUsername() | !validatePassword()) {
            return;
        } else {
            isUser();
        }
    }

    private void isUser() {

        String userEnteredUsername = username.getEditText().getText().toString().trim();
        String userEnteredPassword = password.getEditText().getText().toString().trim();

        // Check user exists or not
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUser = reference.orderByChild("username").equalTo(userEnteredUsername);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    username.setError(null);
                    username.setErrorEnabled(false);

                    String passwordFromDB = snapshot.child(userEnteredUsername).child("password").getValue(String.class);

                    if (passwordFromDB.equals(userEnteredPassword)) {

                        username.setError(null);
                        username.setErrorEnabled(false);

                        String nameFromDB = snapshot.child(userEnteredUsername).child("name").getValue(String.class);
                        String usernameFromDB = snapshot.child(userEnteredUsername).child("username").getValue(String.class);
                        String phoneNoFromDB = snapshot.child(userEnteredUsername).child("phoneNo").getValue(String.class);
                        String emailFromDB = snapshot.child(userEnteredUsername).child("email").getValue(String.class);
                        Boolean adminFromDB = snapshot.child(userEnteredUsername).child("admin").getValue(Boolean.class);

                        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("name", nameFromDB);
                        editor.putString("username", usernameFromDB);
                        editor.putString("phoneNo", phoneNoFromDB);
                        editor.putString("email", emailFromDB);
                        editor.putString("password", passwordFromDB);
                        editor.putBoolean("admin", adminFromDB);
                        editor.apply();

                        navigateToDashboard(usernameFromDB, adminFromDB);
                        Toast.makeText(Login.this, "Welcome to Sinhgad Connect", Toast.LENGTH_SHORT).show();


                    } else {
                        passwordError.setText("Wrong Password");
                        passwordError.setVisibility(View.VISIBLE);
                    }
                } else {
                    username.setError("No such User exists");
                    username.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Login.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Boolean validateUsername() {
        String val = username.getEditText().getText().toString().trim();
        String Requirement = "^[a-z0-9_]{4,20}$";
        if (val.isEmpty()) {
            username.setError("Username cannot be empty");
            return false;
        } else if (!val.matches(Requirement)) {
            username.setError("Invalid username");
            return false;
        } else {
            username.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = password.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            passwordError.setText("Password cannot be empty");
            passwordError.setVisibility(View.VISIBLE);
            return false;
        } else {
            password.setError(null);
            passwordError.setVisibility(View.GONE);
            return true;
        }
    }

    private void navigateToDashboard(String username, boolean isAdmin) {
        Intent intent = new Intent(Login.this, Dashboard.class);
        intent.putExtra("username", username);
        intent.putExtra("admin", isAdmin);
        startActivity(intent);
        finish();
    }

    public void callForgotPassword(View view) {
        Intent intent = new Intent(Login.this, ForgotPassword.class);
        startActivity(intent);
    }

    // Method to check internet connectivity
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}