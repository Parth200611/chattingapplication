package com.mountreachsolution.vibze;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mountreachsolution.vibze.Admin.AdminHomepage;
import com.mountreachsolution.vibze.User.UserHomepage;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {
    TextView title, subtitle, loginLabel, orDivider;
    TextInputEditText username, password;
    CheckBox showPassword;
    Button loginButton, signupButton;
    TextView tvPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.lavender));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.white));
        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.subtitle);
        loginLabel = findViewById(R.id.login_label);
        tvPassword = findViewById(R.id.tvPassword);


        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        showPassword = findViewById(R.id.show_password);

        loginButton = findViewById(R.id.login_button_needy);
        signupButton = findViewById(R.id.signup_button);

        tvPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
Intent i = new Intent(LoginActivity.this,ChangePassword.class);
startActivity(i);
            }
        });


        showPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                password.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {

                password.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            password.setSelection(password.getText().length());
        });


        loginButton.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Enter All Data For Log In", Toast.LENGTH_SHORT).show();
            } else {

                Login(user,pass);
            }
        });

        // Signup Button Click Listener
        signupButton.setOnClickListener(v -> {
            // Redirect to signup activity (Implement intent later)
            Intent i = new Intent(this,RegisterNow.class);
            startActivity(i);
        });

    }

    private void Login(String user, String pass) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("username", user);
        params.put("password", pass);

        // Check if the user is Admin
        if (user.equalsIgnoreCase("Admin") && pass.equalsIgnoreCase("Admin")) {
            // Save login details in SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", user);
            editor.putString("userType", "admin"); // Set userType as admin
            editor.putBoolean("isLoggedIn", true); // Mark user as logged in
            editor.apply();

            Toast.makeText(LoginActivity.this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
            checkUserType(user);
            return;
        }

        client.post(urls.login, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Log.d("LoginResponse", response.toString()); // Log JSON response

                    String success = response.getString("success");

                    if (success.equals("1")) {
                        String userType = response.optString("usertype", "user"); // Use optString to avoid errors

                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", user);
                        editor.putString("userType", "user");
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();

                        Toast.makeText(LoginActivity.this, "Welcome Back!", Toast.LENGTH_SHORT).show();
                        checkUserType(user);
                    } else {
                        Toast.makeText(LoginActivity.this, "Incorrect Username and Password", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void checkUserType(String username) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString("username", null);
        String userType = sharedPreferences.getString("userType", null);

        if (savedUsername != null && savedUsername.equals(username)) {
            if (userType != null) {
                if (userType.equalsIgnoreCase("user")) {
                    // Navigate to User Homepage
                    Intent intent = new Intent(this, UserHomepage.class);
                    startActivity(intent);
                    finish();
                } else if (userType.equalsIgnoreCase("admin")) {
                    // Navigate to Admin Homepage
                    Intent intent = new Intent(this, AdminHomepage.class); // Assuming you have an AdminHomepage activity
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Invalid User Type", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "User Type not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Username not found in SharedPreferences", Toast.LENGTH_SHORT).show();
        }
    }

}