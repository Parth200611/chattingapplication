package com.mountreachsolution.vibze.User;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mountreachsolution.vibze.R;
import com.mountreachsolution.vibze.User.Adpter.FriendListAdapter;
import com.mountreachsolution.vibze.urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserHome extends Fragment {

    RecyclerView rvList;
    TextView tvNoRequest;
    SearchView searchView;
    ProgressDialog progressDialog;
    FriendListAdapter adapter;
    List<String> friendList = new ArrayList<>();

    private static final String URL_FETCH_FRIENDS = "https://yourserver.com/fetch_friends.php"; // Change this to your API URL

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);

        rvList = view.findViewById(R.id.rvLsit);
        tvNoRequest = view.findViewById(R.id.tvNoRequest);
        searchView = view.findViewById(R.id.searchView);

        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));

        fetchFriends(); // Fetch friend list

        // Enable search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (adapter != null) {
                    adapter.getFilter().filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                }
                return false;
            }
        });
return view;
    }

    private void fetchFriends() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading friends...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, urls.Friends,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONArray friendsArray = jsonObject.getJSONArray("friends");
                            friendList.clear();

                            for (int i = 0; i < friendsArray.length(); i++) {
                                friendList.add(friendsArray.getString(i));
                            }

                            // Show or hide "No Friends" message
                            if (friendList.isEmpty()) {
                                tvNoRequest.setVisibility(View.VISIBLE);
                                rvList.setVisibility(View.GONE);
                            } else {
                                tvNoRequest.setVisibility(View.GONE);
                                rvList.setVisibility(View.VISIBLE);
                                adapter = new FriendListAdapter(friendList);
                                rvList.setAdapter(adapter);
                            }

                        } else {
                            tvNoRequest.setVisibility(View.VISIBLE);
                            rvList.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "No friends found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Get the logged-in username from SharedPreferences
                params.put("username", getActivity().getSharedPreferences("UserPrefs", getActivity().MODE_PRIVATE)
                        .getString("username", ""));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }
}
