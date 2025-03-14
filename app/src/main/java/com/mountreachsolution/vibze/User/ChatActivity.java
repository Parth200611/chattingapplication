package com.mountreachsolution.vibze.User;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mountreachsolution.vibze.R;


import com.mountreachsolution.vibze.User.Adpter.ChatAdapter;
import com.mountreachsolution.vibze.User.POJO.ChatMessage;
import com.mountreachsolution.vibze.VolleyMultipartRequest;
import com.mountreachsolution.vibze.urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private TextView tvUsername;
    private RecyclerView rvChat;
    private EditText etMessage;
    private ImageButton btnSend,btnSendImage;
    private List<ChatMessage> chatList;

    private String senderUsername, receiverUsername;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    Uri filepath;
    private Bitmap bitmap;
    ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tvUsername = findViewById(R.id.tvUsername);
        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnSendImage = findViewById(R.id.btnSendImage);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.lavender));
        }

        // Get the receiver's username from Intent
        receiverUsername = getIntent().getStringExtra("username");
        if (receiverUsername == null || receiverUsername.isEmpty()) {
            Toast.makeText(this, "Error: Receiver username not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get the sender's username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        senderUsername = sharedPreferences.getString("username", "");
        if (senderUsername.isEmpty()) {
            Toast.makeText(this, "Error: Sender username not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set chat header
        tvUsername.setText(receiverUsername);

        // Initialize RecyclerView
        chatList = new ArrayList<>();
        adapter = new ChatAdapter(chatList, senderUsername);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        // Auto-scroll when keyboard opens
        rvChat.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                rvChat.postDelayed(() -> {
                    if (!chatList.isEmpty()) {
                        rvChat.scrollToPosition(chatList.size() - 1);
                    }
                }, 100);
            }
        });

        // Load messages
        fetchMessages();

        // Set click listeners
        btnSend.setOnClickListener(view -> sendMessage());
        btnSendImage.setOnClickListener(view -> SelectUserProfileImage());


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
             SendImageMessage(bitmap,senderUsername,receiverUsername,"image");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void SendImageMessage(Bitmap bitmap, String sender, String receiver, String messageType) {
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, urls.upload_images,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Toast.makeText(ChatActivity.this, "Image sent successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ChatActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        String errorMsg = error.getMessage();
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            errorMsg = new String(error.networkResponse.data);
                        }
                        Log.e("UploadError", errorMsg);
                        Toast.makeText(ChatActivity.this, "Upload Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("sender", senderUsername);
                params.put("receiver", receiverUsername);
                params.put("message_type", messageType); // Image type
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





    private void fetchMessages() {
        StringRequest request = new StringRequest(Request.Method.POST, urls.fetch,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.optJSONArray("messages");

                        if (jsonArray != null) {
                            chatList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                chatList.add(new ChatMessage(
                                        obj.optString("sender", ""),
                                        obj.optString("receiver", ""),
                                        obj.optString("message", ""),
                                        obj.optString("timestamp", ""),
                                        obj.optString("image", "")
                                ));
                            }
                            adapter.notifyDataSetChanged();
                            rvChat.scrollToPosition(chatList.size() - 1);
                        }
                    } catch (JSONException e) {
                        Log.e("FetchMessages", "JSON Error: " + e.getMessage());
                    }
                },
                error -> Log.e("FetchMessages", "Volley Error: " + error.toString())) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sender", senderUsername);
                params.put("receiver", receiverUsername);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }


    private void sendMessage() {
        String message = etMessage.getText().toString().trim();
        if (!message.isEmpty()) {
            StringRequest request = new StringRequest(Request.Method.POST, urls.SEND_MESSAGE,
                    response -> {
                        Log.e("SendMessage", "Server Response: " + response); // Debugging line

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("success")) {
                                chatList.add(new ChatMessage(senderUsername, receiverUsername, message, "Just now",null));

                                adapter.notifyItemInserted(chatList.size() - 1); // Notify adapter of new message
                                rvChat.scrollToPosition(chatList.size() - 1); // Scroll to last message

                                etMessage.setText(""); // Clear input field
                            } else {
                                Toast.makeText(ChatActivity.this, "Message failed", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("SendMessage", "JSON Parsing Error: " + e.getMessage());
                            Toast.makeText(ChatActivity.this, "Error sending message", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Log.e("SendMessage", "Volley Error: " + error.toString());
                        Toast.makeText(ChatActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("sender", senderUsername);
                    params.put("receiver", receiverUsername);
                    params.put("message", message);
                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        }
    }
}
