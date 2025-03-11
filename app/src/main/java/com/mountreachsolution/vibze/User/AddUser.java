package com.mountreachsolution.vibze.User;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mountreachsolution.vibze.Admin.AdpterAllUser;
import com.mountreachsolution.vibze.Admin.POJOALLUSER;
import com.mountreachsolution.vibze.R;
import com.mountreachsolution.vibze.User.Adpter.AdpterAddUser;
import com.mountreachsolution.vibze.User.POJO.POJOADDUSER;
import com.mountreachsolution.vibze.urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class AddUser extends AppCompatActivity {
    RecyclerView rvList;
    TextView tvNoRequest;

    SearchView searchView;
    ProgressDialog progressDialog;
    String username;
    List<POJOADDUSER>pojoaddusers,filteredUsers;
    String id,name,mobile,email,gender,username1,password,userType,image;
    AdpterAddUser addUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_user);
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "Guest");
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.lavender));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.white));
        rvList = findViewById(R.id.rvLsit);
        tvNoRequest = findViewById(R.id.tvNoRequest);
        searchView = findViewById(R.id.searchView);

        rvList.setLayoutManager(new LinearLayoutManager(AddUser.this));
        pojoaddusers=new ArrayList<>();
        filteredUsers=new ArrayList<>();


        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        getData();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterUsers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterUsers(newText);
                return false;
            }
        });


    }
    private void getData() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("username",username);
        client.post(urls.AddUserRequest,params ,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray jsonArray =response.getJSONArray("getAllUser");
                    if (jsonArray.length()==0){
                        rvList.setVisibility(View.GONE);
                        tvNoRequest.setVisibility(View.VISIBLE);
                    }
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
                        pojoaddusers.add(new POJOADDUSER(id,name,mobile,email,gender,username1,userType,image));

                    }
                    addUser =new AdpterAddUser(AddUser.this,pojoaddusers);
                    rvList.setAdapter(addUser);
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
    private void filterUsers(String query) {
        if (pojoaddusers == null) {
            return; // Prevent crash if list is null
        }

        List<POJOADDUSER> filteredList = new ArrayList<>();




        for (POJOADDUSER user : pojoaddusers) {
            if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }

        AdpterAddUser adpterAddUser = new AdpterAddUser(this,filteredList);
        rvList.setAdapter(adpterAddUser);
    }
}