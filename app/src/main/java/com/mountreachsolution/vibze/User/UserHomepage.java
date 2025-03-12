package com.mountreachsolution.vibze.User;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mountreachsolution.vibze.AboutUs;
import com.mountreachsolution.vibze.R;

public class UserHomepage extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_homepage);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.lavender)));
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.lavender));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false);
            window.getInsetsController().setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.lavender));
        }

        getWindow().setNavigationBarColor(ContextCompat.getColor(UserHomepage.this,R.color.white));
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        bottomNavigationView = findViewById(R.id.bottomnevigatiomuserhome);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        bottomNavigationView.setSelectedItemId(R.id.UserHome);



    }
    UserHome userHome = new UserHome();
    UserStatus userRequest=new UserStatus();
    UserProfil userProfil = new UserProfil();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.UserHome) {
            getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayoutuserhome, userHome).commit();
        } else if (item.getItemId() == R.id.status) {
            getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayoutuserhome, userRequest).commit();
        } else if (item.getItemId() == R.id.UserProfil) {
            getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayoutuserhome, userProfil).commit();

        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menuuser,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.Request){
            Intent i = new Intent(UserHomepage.this, RequestPage.class);
            startActivity(i);

        }else if (item.getItemId()==R.id.Aboutus){
            Intent i = new Intent(UserHomepage.this, AboutUs.class);
            startActivity(i);

        }else if (item.getItemId()==R.id.AddUser){
            Intent i = new Intent(UserHomepage.this, AddUser.class);
            startActivity(i);

        }else if (item.getItemId()==R.id.caretegroup){
            Intent i = new Intent(UserHomepage.this, CreateGroup.class);
            startActivity(i);

        }else if (item.getItemId()==R.id.ViewGroup){
            Intent i = new Intent(UserHomepage.this, ViewGroup.class);
            startActivity(i);

        }

        return true;
    }
}