package com.mountreachsolution.vibze.User;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mountreachsolution.vibze.R;
import com.mountreachsolution.vibze.User.Adpter.GroupAdapter;
import com.mountreachsolution.vibze.User.POJO.GroupModel;
import com.mountreachsolution.vibze.urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.entity.mime.Header;

public class ViewGroup extends AppCompatActivity {
    RecyclerView recyclerView;
    GroupAdapter groupAdapter;
    List<GroupModel> groupList;
    String username;

    private static final String TAG = "ViewGroup"; // Log tag for debugging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);

        // Set status and navigation bar colors
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.lavender));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));

        // Retrieve logged-in username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "Guest");

        recyclerView = findViewById(R.id.recyclerViewGroups);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        groupList = new ArrayList<>();
        groupAdapter = new GroupAdapter(groupList, ViewGroup.this);
        recyclerView.setAdapter(groupAdapter);

        fetchGroups(username);
    }



    private void fetchGroups(String username) {
        Log.d(TAG, "Fetching groups for username: " + username);

        if (username == null || username.trim().isEmpty()) {
            Log.e(TAG, "Error: Username is null or empty!");
            Toast.makeText(this, "Error: Username is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("username", username); // Ensure correct key

        client.post(urls.groupfetch, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                Log.d(TAG, "Server Response: " + response.toString());

                groupList.clear(); // Clear old data before adding new

                try {
                    if (response.getBoolean("success")) {
                        JSONArray groupsArray = response.getJSONArray("groups");
                        for (int i = 0; i < groupsArray.length(); i++) {
                            JSONObject groupObj = groupsArray.getJSONObject(i);
                            String groupId = groupObj.getString("id");
                            String groupName = groupObj.getString("group_name");

                            Log.d(TAG, "Adding Group: ID=" + groupId + ", Name=" + groupName); // ✅ Debug log

                            groupList.add(new GroupModel(groupId, groupName));
                        }
                        groupAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Total Groups Loaded: " + groupList.size()); // ✅ Check if groups are being added
                    } else {
                        Log.e(TAG, "No groups found: " + response.getString("message"));
                        Toast.makeText(ViewGroup.this, "No groups found", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "Error Response: " + (errorResponse != null ? errorResponse.toString() : "null"));
                Toast.makeText(ViewGroup.this, "Failed to fetch groups! Status: " + statusCode, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
