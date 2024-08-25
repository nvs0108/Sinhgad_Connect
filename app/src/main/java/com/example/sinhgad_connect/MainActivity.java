package com.example.sinhgad_connect;

import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.view.View;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN = 2000;

    //Variables
    private Animation topAnim, bottomAnim;
    private ImageView image;
    private TextView logo, slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //Hooks
        image = findViewById(R.id.imagelogo);
        logo = findViewById(R.id.textlogo);
        slogan = findViewById(R.id.textslogan);

        //Set animation to elements
        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        new Handler(Looper.getMainLooper()).postDelayed(this::startLoginActivity, SPLASH_SCREEN);
    }


    private void startLoginActivity(){
        Intent intent = new Intent(this, Login.class);

        // Attach all the elements those you want to animate in design
        Pair<View, String>[]pairs=new Pair[2];
        pairs[0]=new Pair<>(image,"logo_image");
        pairs[1]=new Pair<>(logo,"logo_text");

        //wrap the call in API level 21 or higher
        if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.LOLLIPOP)
        {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
            startActivity(intent,options.toBundle());
        } else {
            startActivity(intent);
        }

        // Finish current activity to prevent user from coming back to splash screen using back button
        finish();

    }
}