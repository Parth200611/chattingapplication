package com.mountreachsolution.vibze;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RegisterNow extends AppCompatActivity {
    TextInputEditText etName, etMobileNo, etEmail, etGender, etUsername, etPassword, etConfirmPassword;
    RadioGroup userTypeGroup;
    RadioButton rbSaviour, rbNeedy;
    Button registerButton;


    String name, mobileNo, email, gender, username, password, confirmPassword, userType="user";
    boolean isPasswordVisible = false;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_now);
        etName = findViewById(R.id.et_registration_name);
        etMobileNo = findViewById(R.id.et_registration_mobile_no);
        etEmail = findViewById(R.id.et_registration_emailid);
        etGender = findViewById(R.id.et_registration_gender);
        etUsername = findViewById(R.id.et_registration_username);
        etPassword = findViewById(R.id.et_registration_password);
        etConfirmPassword = findViewById(R.id.et_registration_confirm_password);
        registerButton = findViewById(R.id.register_login_button);

        etPassword.setOnClickListener(v -> togglePasswordVisibility());
        registerButton.setOnClickListener(view -> {
            saveInputData();
            if (validateInputs()) {
                progressDialog=new ProgressDialog(RegisterNow.this);
                progressDialog.setMessage("Please Wait");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                register();

            }
        });

    }



    private void register() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("name",name);
        params.put("emailid",email);
        params.put("mobileno",mobileNo);
        params.put("username",username);
        params.put("password",password);
        params.put("gender",gender);
        params.put("userType",userType);
        client.post(urls.userregister,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    String ststus= response.getString("success");
                    if (ststus.equals("1")){
                        progressDialog.dismiss();
                        Toast.makeText(RegisterNow.this, "Register Done !", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(RegisterNow.this, LoginActivity.class);
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", username);
                        editor.putString("userType", userType);
                        editor.apply();
                        startActivity(i);
                        finish();
                    }else{
                        Toast.makeText(RegisterNow.this, "Server Error", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });


    }
    private  void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        isPasswordVisible = !isPasswordVisible;
    }

    // Save input data into Strings
    private void saveInputData() {
        name = etName.getText().toString().trim();
        mobileNo = etMobileNo.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        gender = etGender.getText().toString().trim();
        username = etUsername.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        confirmPassword = etConfirmPassword.getText().toString().trim();

        // Get selected Radio Button
    }

    // Validate all input fields
    private boolean validateInputs() {
        if (name.isEmpty() || mobileNo.isEmpty() || email.isEmpty() || gender.isEmpty() ||
                username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Register Method
}