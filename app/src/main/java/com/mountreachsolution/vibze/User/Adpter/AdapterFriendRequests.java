package com.mountreachsolution.vibze.User.Adpter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mountreachsolution.vibze.R;
import com.mountreachsolution.vibze.User.POJO.POJOFriendRequest;

import java.util.List;

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

        // Accept button functionality
        holder.btnAccept.setOnClickListener(v -> {
            // Code to accept the friend request
        });

        // Reject button functionality
        holder.btnReject.setOnClickListener(v -> {
            // Code to reject the friend request
        });

    }

    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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
