package com.mountreachsolution.vibze.User;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mountreachsolution.vibze.R;

import com.mountreachsolution.vibze.User.Adpter.ChatAdpter;
import com.mountreachsolution.vibze.VolleyMultipartRequest;
import com.mountreachsolution.vibze.urls;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupChat extends AppCompatActivity {
    RecyclerView rvChat;
     ChatAdpter chatAdapter;
     List<Message> messageList;
     EditText etMessage;
     ImageButton btnSend,btnimmage;
     String groupname,username;
     TextView tvusername;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    Uri filepath;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_chat);
       groupname=getIntent().getStringExtra("Groupname");
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "Guest");
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.lavender));
        }
        getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.white));
        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);


        messageList = new ArrayList<>();
        chatAdapter = new ChatAdpter(messageList, username);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(chatAdapter);
        tvusername=findViewById(R.id.tvUsername);
        tvusername.setText(groupname);

        // Load messages
        fetchMessages();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });



    }

    private void fetchMessages() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urls.grouprecive,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("success")) {
                                messageList.clear();
                                JSONArray messagesArray = jsonObject.getJSONArray("messages");

                                for (int i = 0; i < messagesArray.length(); i++) {
                                    JSONObject messageObj = messagesArray.getJSONObject(i);
                                    String sender = messageObj.getString("sender_id");
                                    String text = messageObj.getString("message");
                                    String timestamp = messageObj.getString("timestamp");
                                    String image = messageObj.getString("image");

                                    messageList.add(new Message(sender, text, timestamp,image));
                                }
                                chatAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(GroupChat.this, "No messages found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(GroupChat.this, "Error parsing messages", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GroupChat.this, "Error fetching messages", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("group_name", groupname);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void sendMessage() {
        final String messageText = etMessage.getText().toString().trim();
        if (messageText.isEmpty()) {
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urls.groupsend,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("success")) {
                                etMessage.setText("");
                                fetchMessages(); // Refresh chat
                            } else {
                                Toast.makeText(GroupChat.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(GroupChat.this, "Error sending message", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GroupChat.this, "Error sending message", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("group_name", groupname);
                params.put("sender_id", username);
                params.put("message", messageText);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
    private void SelectUserProfileImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image For Profile"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            filepath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                SendImageMessage(bitmap,username,groupname);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void SendImageMessage(Bitmap bitmap, String sender, String receiver) {
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, urls.groupimage,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Toast.makeText(GroupChat.this, "Image sent successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GroupChat.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        String errorMsg = error.getMessage();
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            errorMsg = new String(error.networkResponse.data);
                        }
                        Log.e("UploadError", errorMsg);
                        Toast.makeText(GroupChat.this, "Upload Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("group_name", groupname);
                params.put("sender_id", username);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("pic", new DataPart(imagename + ".jpeg", getFileDataFromBitmap(bitmap)));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }


    private byte[] getFileDataFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


}