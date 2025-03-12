package com.mountreachsolution.vibze.User;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mountreachsolution.vibze.R;
import com.mountreachsolution.vibze.User.Adpter.ChatAdapter;
import com.mountreachsolution.vibze.User.Adpter.ChatAdpter;
import com.mountreachsolution.vibze.urls;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupChat extends AppCompatActivity {
    RecyclerView rvChat;
     ChatAdpter chatAdapter;
     List<Message> messageList;
     EditText etMessage;
     ImageButton btnSend;
     String groupname,username;
     TextView tvusername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_group_chat);
       groupname=getIntent().getStringExtra("Groupname");
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "Guest");
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.lavender));
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

                                    messageList.add(new Message(sender, text, timestamp));
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
}