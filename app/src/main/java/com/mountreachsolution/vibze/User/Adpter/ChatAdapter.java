package com.mountreachsolution.vibze.User.Adpter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mountreachsolution.vibze.R;
import com.mountreachsolution.vibze.User.POJO.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<ChatMessage> chatList;
    private String currentUser;

    public ChatAdapter(List<ChatMessage> chatList, String currentUser) {
        this.chatList = chatList;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_sent, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_received, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        ChatMessage message = chatList.get(position);
        holder.tvMessage.setText(message.getMessage());


    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatList.get(position).getSender().equals(currentUser)) {
            return 1; // Sent message
        } else {
            return 0; // Received message
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTimestamp;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}