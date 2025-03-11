package com.mountreachsolution.vibze.User.Adpter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.mountreachsolution.vibze.R;
import com.mountreachsolution.vibze.User.ChatActivity;
import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {
    private List<String> friendList;
    private Activity context;

    public FriendListAdapter(Activity context, List<String> friendList) {
        this.context = context;
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public FriendListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListAdapter.ViewHolder holder, int position) {
        String username = friendList.get(position);
        holder.tvFriendUsername.setText(username);

        // Open chat page when clicking on a friend
        holder.card.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("username", username);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFriendUsername;
        CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFriendUsername = itemView.findViewById(R.id.tvFriendUsername);
            card = itemView.findViewById(R.id.Card);
        }
    }
}
