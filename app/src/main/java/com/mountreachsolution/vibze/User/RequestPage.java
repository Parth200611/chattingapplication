package com.mountreachsolution.vibze.User;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mountreachsolution.vibze.Admin.AdpterAllUser;
import com.mountreachsolution.vibze.Admin.POJOALLUSER;
import com.mountreachsolution.vibze.R;

import com.mountreachsolution.vibze.User.Adpter.AdapterFriendRequests;
import com.mountreachsolution.vibze.User.POJO.POJOFriendRequest;
import com.mountreachsolution.vibze.urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestPage extends AppCompatActivity {
    RecyclerView rvList;
    TextView tvNoRequest;
    SearchView searchView;
    List<POJOFriendRequest> requestList;
    AdapterFriendRequests adapter;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_request_page);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.lavender));
        }
        getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.white));

        rvList = findViewById(R.id.rvLsit);
        tvNoRequest = findViewById(R.id.tvNoRequest);
        searchView = findViewById(R.id.searchView);

        rvList.setLayoutManager(new LinearLayoutManager(RequestPage.this));
        requestList = new ArrayList<>();
        adapter = new AdapterFriendRequests(RequestPage.this, requestList);
        rvList.setAdapter(adapter);

        // Fetch friend requests
        fetchFriendRequests();
        enableSearch();
    }

    private void enableSearch() {
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

    private void fetchFriendRequests() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading friend requests...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String receiverUsername = sharedPreferences.getString("username", "Guest");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urls.GET_FRIEND_REQUESTS,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int success = jsonObject.getInt("success");

                        if (success == 1) {
                            JSONArray jsonArray = jsonObject.getJSONArray("getRequest");
                            requestList.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                String senderUsername = obj.getString("senderusername");

                                requestList.add(new POJOFriendRequest(senderUsername));
                            }
                            adapter.notifyDataSetChanged();
                            tvNoRequest.setVisibility(requestList.isEmpty() ? TextView.VISIBLE : TextView.GONE);
                        } else {
                            tvNoRequest.setVisibility(TextView.VISIBLE);
                            Toast.makeText(RequestPage.this, "No friend requests found.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(RequestPage.this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(RequestPage.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("receiver_username", receiverUsername);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void filterUsers(String query) {
        if (requestList == null) {
            return; // Prevent crash if list is null
        }

        List<POJOFriendRequest> filteredList = new ArrayList<>();

        for (POJOFriendRequest user : requestList) {
            if (user.getSenderUsername().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }

        adapter = new AdapterFriendRequests(RequestPage.this,filteredList);
        rvList.setAdapter(adapter);
    }

}
