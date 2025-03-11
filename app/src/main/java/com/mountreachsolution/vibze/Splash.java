package com.mountreachsolution.vibze;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.mountreachsolution.vibze.Admin.AdminHomepage;
import com.mountreachsolution.vibze.User.UserHomepage;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
        new Handler().postDelayed(this::checkLoginStatus, 2000); // 2-second splash delay

    }
    private void checkLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            String userType = sharedPreferences.getString("userType", null);

            if (userType != null) {
                if (userType.equalsIgnoreCase("user")) {
                    // Navigate to User Homepage
                    startActivity(new Intent(this, UserHomepage.class));
                } else if (userType.equalsIgnoreCase("admin")) {
                    // Navigate to Admin Homepage
                    startActivity(new Intent(this, AdminHomepage.class)); // Assuming you have an AdminHomepage activity
                }
            }
            finish();
        } else {
            // Redirect to Login Screen
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}