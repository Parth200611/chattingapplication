package com.mountreachsolution.vibze.Admin;

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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mountreachsolution.vibze.R;
import com.mountreachsolution.vibze.urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class AdminHome extends Fragment {
    RecyclerView rvList;
    TextView tvNoRequest;
    List<POJOALLUSER> pojoallusers,filteredList;
    AdpterAllUser adpterAllUser;
    SearchView searchView;
    ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_admin_home, container, false);
        rvList = view.findViewById(R.id.rvLsit);
        tvNoRequest = view.findViewById(R.id.tvNoRequest);
        searchView = view.findViewById(R.id.searchView);

        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));

        pojoallusers = new ArrayList<>(); // Ensure it is initialized
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();

        getData();

        setupSearchView();

        return view;

    }
    private void getData() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        client.post(urls.getAllUser,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray jsonArray = response.getJSONArray("getAllUser");
                    if (jsonArray.length()==0){
                        tvNoRequest.setVisibility(View.VISIBLE);
                        rvList.setVisibility(View.GONE);
                    }
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String name=jsonObject.getString("name");
                        String id=jsonObject.getString("id");
                        String mobileno=jsonObject.getString("mobile");
                        String gender=jsonObject.getString("gender");
                        String email=jsonObject.getString("email");
                        String image=jsonObject.getString("image");
                        String usertype=jsonObject.getString("usertype");
                        String username=jsonObject.getString("username");
                        pojoallusers.add(new POJOALLUSER(id,name,mobileno,email,gender,usertype,username,image));

                    }
                    adpterAllUser=new AdpterAllUser(pojoallusers,getActivity());
                    progressDialog.dismiss();
                    rvList.setAdapter(adpterAllUser);


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
    private void setupSearchView() {
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


    private void filterUsers(String query) {
        if (pojoallusers == null) {
            return; // Prevent crash if list is null
        }

        List<POJOALLUSER> filteredList = new ArrayList<>();

        for (POJOALLUSER user : pojoallusers) {
            if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }

        adpterAllUser = new AdpterAllUser(filteredList, getActivity());
        rvList.setAdapter(adpterAllUser);
    }

}