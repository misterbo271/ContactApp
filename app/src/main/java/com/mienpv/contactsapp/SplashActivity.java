package com.mienpv.contactsapp;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;



import androidx.appcompat.app.AppCompatActivity;


import com.mienpv.contactsapp.R;






public class SplashActivity extends AppCompatActivity {
    public static final int SPLASH_DISPLAY_LENGTH = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
