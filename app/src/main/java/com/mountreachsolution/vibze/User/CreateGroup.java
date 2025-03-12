package com.mountreachsolution.vibze.User;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mountreachsolution.vibze.R;
import com.mountreachsolution.vibze.User.Adpter.AdpterAddUser;
import com.mountreachsolution.vibze.User.Adpter.UserAdapter;
import com.mountreachsolution.vibze.User.POJO.POJOADDUSER;
import com.mountreachsolution.vibze.User.POJO.UserModel;
import com.mountreachsolution.vibze.urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class CreateGroup extends AppCompatActivity {
    EditText edtGroupName;
     RecyclerView recyclerViewUsers;
     Button btnCreateGroup;
     UserAdapter userAdapter;
    ArrayList<UserModel> userList = new ArrayList<>();
 ArrayList<String> selectedUsers = new ArrayList<>();
     String loggedInUser;
    String id,name,mobile,email,gender,username1,password,userType,image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_group);
        edtGroupName = findViewById(R.id.edtGroupName);
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        btnCreateGroup = findViewById(R.id.btnCreateGroup);

        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        // Get logged-in username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        loggedInUser = sharedPreferences.getString("username", ""); // Assume you store the username after login

        getData();

        btnCreateGroup.setOnClickListener(v -> createGroup());
    }
    private void getData() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("username",loggedInUser);
        client.post(urls.AddUserRequest,params ,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray jsonArray =response.getJSONArray("getAllUser");

                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        id = jsonObject.getString("id");
                        name = jsonObject.getString("name");
                        mobile = jsonObject.getString("mobile");
                        email = jsonObject.getString("email");
                        gender = jsonObject.getString("gender");
                        username1 = jsonObject.getString("username");
                        password = jsonObject.getString("password");
                        userType = jsonObject.getString("usertype");
                        image = jsonObject.getString("image");
                        userList.add(new UserModel(name,username1,image));

                    }
                    userAdapter =new UserAdapter(CreateGroup.this,userList);
                    recyclerViewUsers.setAdapter(userAdapter);
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

    private void createGroup() {
        if (userAdapter == null) {
            Toast.makeText(this, "User list not loaded yet!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> selectedUsernames = new ArrayList<>();
        for (UserModel user : userAdapter.getSelectedUsers()) {
            if (user.isSelected()) {
                selectedUsernames.add(user.getUsername());
            }
        }

        if (selectedUsernames.isEmpty()) {
            Toast.makeText(this, "Please select at least one user!", Toast.LENGTH_SHORT).show();
            return;
        }

        String groupName = edtGroupName.getText().toString().trim();
        if (groupName.isEmpty()) {
            Toast.makeText(this, "Enter a group name!", Toast.LENGTH_SHORT).show();
            return;
        }

        String createdBy = loggedInUser;
        String members = new JSONArray(selectedUsernames).toString();

        StringRequest request = new StringRequest(Request.Method.POST, urls.cretegroupp,
                response -> Toast.makeText(CreateGroup.this, "Group created successfully!", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(CreateGroup.this, "Error creating group!", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("group_name", groupName);
                params.put("created_by", createdBy);
                params.put("members", members);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }


}