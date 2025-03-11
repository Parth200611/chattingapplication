package com.mountreachsolution.vibze.User;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mountreachsolution.vibze.Admin.AdpterAllUser;
import com.mountreachsolution.vibze.Admin.POJOALLUSER;
import com.mountreachsolution.vibze.R;

import java.util.ArrayList;
import java.util.List;

public class RequestPage extends AppCompatActivity {
    RecyclerView rvList;
    TextView tvNoRequest;

    SearchView searchView;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_request_page);
        rvList = findViewById(R.id.rvLsit);
        tvNoRequest = findViewById(R.id.tvNoRequest);
        searchView = findViewById(R.id.searchView);

        rvList.setLayoutManager(new LinearLayoutManager(RequestPage.this));




    }
}