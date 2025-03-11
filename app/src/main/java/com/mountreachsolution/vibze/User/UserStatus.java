package com.mountreachsolution.vibze.User;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mountreachsolution.vibze.R;
import com.mountreachsolution.vibze.User.Adpter.ADPTERSTATUS;
import com.mountreachsolution.vibze.User.POJO.POJOSTATUS;
import com.mountreachsolution.vibze.VolleyMultipartRequest;
import com.mountreachsolution.vibze.urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;


public class UserStatus extends Fragment {
    CircleImageView ivImage;
     TextView tvAddStatus, tvNoStatus;
     RecyclerView rvList;
     String username;
    Bitmap bitmap;
    Uri filepath;
    private  int pick_image_request=789;

    private static final int PICK_IMAGE_REQUEST_PASS = 1;
    List<POJOSTATUS>pojostatuses;
    ADPTERSTATUS adpterstatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_user_status, container, false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "Guest");
        ivImage = view.findViewById(R.id.ivImage);
        tvNoStatus = view.findViewById(R.id.tvNoStattus);
        rvList = view.findViewById(R.id.rvList);
        pojostatuses=new ArrayList<>();
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));

        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertIT(username);
                SelectUserProfileimage();
            }
        });
        getStatus();
        return view;
    }

    private void getStatus() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urls.getStatus, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject =new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("getStatus");
                    if (jsonArray.length()==0){
                        rvList.setVisibility(View.GONE);
                        tvNoStatus.setVisibility(View.VISIBLE);
                    }
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1 =jsonArray.getJSONObject(i);
                        String id=jsonObject1.getString("id");
                        String username1=jsonObject1.getString("username");
                        String image=jsonObject1.getString("image_url");
                        pojostatuses.add(new POJOSTATUS(id,username1,image));
                    }
                    adpterstatus=new ADPTERSTATUS(pojostatuses,getActivity());
                    rvList.setAdapter(adpterstatus);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

    private void insertIT(String username) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("username",username);
        client.post(urls.insertdatastatus,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    String status=response.getString("success");
                    if (status.equals("1")){
                        Toast.makeText(getActivity(), "Set your status", Toast.LENGTH_SHORT).show();
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

    private void SelectUserProfileimage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image For Profil"),pick_image_request);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==pick_image_request && resultCode==RESULT_OK && data!=null){
            filepath=data.getData();
            try {
                bitmap= MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),filepath);
                ivImage.setImageBitmap(bitmap);
                UserImageSaveTodatabase(bitmap,username);


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void UserImageSaveTodatabase(Bitmap bitmap, String strTitle) {
        VolleyMultipartRequest volleyMultipartRequest =  new VolleyMultipartRequest(Request.Method.POST, urls.uplodestatus, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                Toast.makeText(getActivity(), "Image Save as Status "+strTitle, Toast.LENGTH_SHORT).show();
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.detach(UserStatus.this).attach(UserStatus.this).commit();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                String errorMsg = error.getMessage();
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    errorMsg = new String(error.networkResponse.data);
                }
                Log.e("UploadError", errorMsg);
                Toast.makeText(getActivity(), "Upload Error: " + errorMsg, Toast.LENGTH_LONG).show();

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parms = new HashMap<>();
                parms.put("tags", strTitle); // Adjusted to match PHP parameter name
                return parms;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String,VolleyMultipartRequest.DataPart> parms = new HashMap<>();
                long imagename = System.currentTimeMillis();
                parms.put("pic",new VolleyMultipartRequest.DataPart(imagename+".jpeg",getfiledatafromBitmap(bitmap)));

                return parms;

            }

        };
        Volley.newRequestQueue(getActivity()).add(volleyMultipartRequest);
    }



    private byte[] getfiledatafromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream  = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}