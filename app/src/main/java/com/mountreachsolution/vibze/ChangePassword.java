package com.mountreachsolution.vibze;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends AppCompatActivity {
    TextInputEditText usernameInput, newPasswordInput;
    AppCompatButton changePasswordButton;
    String username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.lavender));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.white));
        usernameInput = findViewById(R.id.username);
        newPasswordInput = findViewById(R.id.new_password);
        changePasswordButton = findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }
    private void changePassword() {

        username = usernameInput.getText().toString().trim();
        password = newPasswordInput.getText().toString().trim();


        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter all data!", Toast.LENGTH_SHORT).show();
        }else{
            changePassword2();

        }


    }
    private void changePassword2() {

        // Validate input fields
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(ChangePassword.this, "Please enter all data", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urls.updatePassword,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        Toast.makeText(ChangePassword.this, message, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(ChangePassword.this,LoginActivity.class);
                        startActivity(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ChangePassword.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                }, error -> Toast.makeText(ChangePassword.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("new_password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}