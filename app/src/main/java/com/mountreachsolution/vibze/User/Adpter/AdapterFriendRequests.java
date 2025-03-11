package com.mountreachsolution.vibze.User.Adpter;

import android.app.Activity;
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
import com.mountreachsolution.vibze.R;
import com.mountreachsolution.vibze.User.POJO.POJOFriendRequest;
import com.mountreachsolution.vibze.urls;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterFriendRequests extends RecyclerView.Adapter<AdapterFriendRequests.ViewHolder> {
    Activity activity;
    List<POJOFriendRequest> friendRequests;

    public AdapterFriendRequests(Activity activity, List<POJOFriendRequest> friendRequests) {
        this.activity = activity;
        this.friendRequests = friendRequests;
    }

    @NonNull
    @Override
    public AdapterFriendRequests.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.friend_request_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFriendRequests.ViewHolder holder, int position) {
        POJOFriendRequest request = friendRequests.get(position);
        holder.tvSenderUsername.setText(request.getSenderUsername());

        holder.btnAccept.setOnClickListener(v -> acceptRequest(request.getSenderUsername(), position));
        holder.btnReject.setOnClickListener(v -> rejectRequest(request.getSenderUsername(), position));
    }

    private void acceptRequest(String senderUsername, int position) {
        String user2 = activity.getSharedPreferences("UserPrefs", Activity.MODE_PRIVATE).getString("username", "");
        if (user2.isEmpty()) {
            Toast.makeText(activity, "Error: Username not found", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, urls.ACCEPT_FRIEND_REQUEST,
                response -> {
                    Toast.makeText(activity, "Friend Request Accepted!", Toast.LENGTH_SHORT).show();
                    removeItem(position);
                },
                error -> Toast.makeText(activity, "Failed to accept request", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user1", senderUsername);
                params.put("user2", user2);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(request);
    }

    private void rejectRequest(String senderUsername, int position) {
        String user2 = activity.getSharedPreferences("UserPrefs", Activity.MODE_PRIVATE).getString("username", "");
        if (user2.isEmpty()) {
            Toast.makeText(activity, "Error: Username not found", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, urls.REJECT_FRIEND_REQUEST,
                response -> {
                    Toast.makeText(activity, "Friend Request Rejected", Toast.LENGTH_SHORT).show();
                    removeItem(position);
                },
                error -> Toast.makeText(activity, "Failed to reject request", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user1", senderUsername);
                params.put("user2", user2);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(request);
    }

    private void removeItem(int position) {
        if (position >= 0 && position < friendRequests.size()) {
            friendRequests.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSenderUsername;
        Button btnAccept, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSenderUsername = itemView.findViewById(R.id.tvSenderUsername);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
