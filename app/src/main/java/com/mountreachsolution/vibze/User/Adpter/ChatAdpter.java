package com.mountreachsolution.vibze.User.Adpter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mountreachsolution.vibze.R;
import com.mountreachsolution.vibze.User.Message;

import java.util.List;

public class ChatAdpter extends RecyclerView.Adapter<ChatAdpter.ViewHolder> {

    List<Message> messageList;
     String currentUserId;

    public ChatAdpter(List<Message> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ChatAdpter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ChatAdpter.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.tvMessage.setText(message.getMessage());
        holder.tvTimestamp.setText(message.getTimestamp());


        // Align messages based on sender
        if (message.getSenderId().equals(currentUserId)) {
            holder.tvMessage.setBackgroundResource(R.drawable.bg_chat_sent);
        } else {
            holder.tvMessage.setBackgroundResource(R.drawable.bg_chat_received);
        }
    }

    @Override
    public int getItemCount() {
        return  messageList.size();
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
