package com.mountreachsolution.vibze.User.Adpter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.mountreachsolution.vibze.R;
import com.mountreachsolution.vibze.User.POJO.POJOADDUSER;
import com.mountreachsolution.vibze.urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdpterAddUser extends RecyclerView.Adapter<AdpterAddUser.ViewHolder> {
    Activity activity;
    List<POJOADDUSER> pojoaddusers;

    public AdpterAddUser(Activity activity, List<POJOADDUSER> pojoaddusers) {
        this.activity = activity;
        this.pojoaddusers = pojoaddusers;
    }

    public void updateData(List<POJOADDUSER> newUsers) {
        this.pojoaddusers.clear();
        this.pojoaddusers.addAll(newUsers);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdpterAddUser.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adduser, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdpterAddUser.ViewHolder holder, int position) {
        POJOADDUSER user = pojoaddusers.get(position);

        holder.tvUsername.setText(user.getUsername());
        holder.tvName.setText(user.getName());

        Glide.with(activity)
                .load(urls.address + "images/" + user.getImage())
                .skipMemoryCache(true)
                .error(R.drawable.baseline_person_24)
                .into(holder.profileImage);

        // Handle Add Button Click
        holder.btnAdd.setOnClickListener(v -> {
            holder.btnAdd.setEnabled(false);  // Disable button to prevent duplicate clicks
            sendFriendRequest(user.getUsername(), holder.btnAdd);
        });
    }

    private void sendFriendRequest(String receiverUsername, Button btnAdd) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String senderUsername = sharedPreferences.getString("username", "Guest");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urls.SEND_FRIEND_REQUEST,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int success = jsonObject.getInt("success");
                        String message = jsonObject.getString("message");

                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();

                        if (success == 1) {
                            btnAdd.setText("Requested"); // Change button text after successful request
                            btnAdd.setEnabled(false);
                        } else {
                            btnAdd.setEnabled(true); // Re-enable button if request failed
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        btnAdd.setEnabled(true); // Re-enable button if JSON error occurs
                    }
                },
                error -> {
                    Toast.makeText(activity, "Failed to send request", Toast.LENGTH_SHORT).show();
                    btnAdd.setEnabled(true); // Re-enable button if request fails
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("senderusername", senderUsername);
                params.put("reciverusername", receiverUsername);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return pojoaddusers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvName;
        CircleImageView profileImage;
        Button btnAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvName = itemView.findViewById(R.id.tvName);
            profileImage = itemView.findViewById(R.id.profileImage);
            btnAdd = itemView.findViewById(R.id.btnAddUser);
        }
    }
}
