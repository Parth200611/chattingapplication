package com.mountreachsolution.vibze.User.Adpter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mountreachsolution.vibze.R;
import com.mountreachsolution.vibze.User.POJO.ChatMessage;
import com.mountreachsolution.vibze.urls;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private final List<ChatMessage> chatList;
    private final String currentUser;
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(List<ChatMessage> chatList, String currentUser) {
        this.chatList = chatList;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = (viewType == VIEW_TYPE_SENT) ? R.layout.item_chat_sent : R.layout.item_chat_received;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        ChatMessage message = chatList.get(position);




        holder.tvMessage.setVisibility(View.GONE);
        holder.ivImage.setVisibility(View.GONE);
        holder.tvMessage.setText("");
        holder.ivImage.setImageDrawable(null);


        if (message.getMessage() != null && !message.getMessage().trim().isEmpty()) {
            holder.tvMessage.setVisibility(View.VISIBLE);
            holder.tvMessage.setText(message.getMessage());
        }

        else if (message.getImageUrl() != null && !message.getImageUrl().trim().isEmpty()) {
            holder.ivImage.setVisibility(View.VISIBLE);


            String imageUrl = urls.address + "chat_images/" + message.getImageUrl();


            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.baseline_person_24)
                    .error(R.drawable.baseline_person_24)
                    .into(holder.ivImage);
        } else {

        }
    }



    @Override
    public int getItemCount() {
        return (chatList != null) ? chatList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return chatList.get(position).getSender().equals(currentUser) ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            ivImage = itemView.findViewById(R.id.ivImage);
        }
    }
}