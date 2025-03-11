package com.mountreachsolution.vibze.User;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
    private ChatAdapter adapter;
    private String senderUsername, receiverUsername;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize Views
        tvUsername = findViewById(R.id.tvUsername);
        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnSendImage = findViewById(R.id.btnSendImage);

        // Get the receiver's username from Intent
        receiverUsername = getIntent().getStringExtra("username");

        // Get the sender's username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        senderUsername = sharedPreferences.getString("username", "");

        if (senderUsername.isEmpty()) {
            Toast.makeText(this, "Error: Sender username not found!", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set chat header
        tvUsername.setText(receiverUsername);

        // Initialize RecyclerView
        chatList = new ArrayList<>();
        adapter = new ChatAdapter(chatList, senderUsername);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        // Load messages
        fetchMessages();

        // Send message when button is clicked
        btnSend.setOnClickListener(view -> sendMessage());

    }


    private void fetchMessages() {
        StringRequest request = new StringRequest(Request.Method.POST, urls.FETCH_MESSAGES,
                response -> {
                    try {
                        Log.d("FetchMessages", "Response: " + response); // Debugging

                        // ✅ Fix: Parse the response as an OBJECT first
                        JSONObject jsonResponse = new JSONObject(response);

                        if (jsonResponse.has("error")) {
                            Log.e("FetchMessages", "Server Error: " + jsonResponse.getString("error"));
                            Toast.makeText(ChatActivity.this, "Error: " + jsonResponse.getString("error"), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // ✅ Fix: Handle an empty message list correctly
                        JSONArray jsonArray = jsonResponse.optJSONArray("messages");
                        if (jsonArray == null) {
                            Log.e("FetchMessages", "No messages found");
                            return;
                        }

                        chatList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            ChatMessage message = new ChatMessage(
                                    obj.optString("sender", ""),
                                    obj.optString("receiver", ""),
                                    obj.optString("message", ""),
                                    obj.optString("timestamp", "")
                            );
                            chatList.add(message);
                        }

                        adapter.notifyDataSetChanged();
                        rvChat.scrollToPosition(chatList.size() - 1); // Scroll to last message
                    } catch (JSONException e) {
                        Log.e("FetchMessages", "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(ChatActivity.this, "Error fetching messages", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("FetchMessages", "Volley Error: " + error.toString());
                    Toast.makeText(ChatActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user1", senderUsername);
                params.put("user2", receiverUsername);
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
                                chatList.add(new ChatMessage(senderUsername, receiverUsername, message, "Just now"));

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
