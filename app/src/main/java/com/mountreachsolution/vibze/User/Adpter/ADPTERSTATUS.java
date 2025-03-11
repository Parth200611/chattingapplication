package com.mountreachsolution.vibze.User.Adpter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mountreachsolution.vibze.R;
import com.mountreachsolution.vibze.User.POJO.POJOSTATUS;
import com.mountreachsolution.vibze.User.StatusView;
import com.mountreachsolution.vibze.urls;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ADPTERSTATUS extends RecyclerView.Adapter<ADPTERSTATUS.ViewHolder> {
    List<POJOSTATUS>pojostatuses;
    Activity activity;

    public ADPTERSTATUS(List<POJOSTATUS> pojostatuses, Activity activity) {
        this.pojostatuses = pojostatuses;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ADPTERSTATUS.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_status,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ADPTERSTATUS.ViewHolder holder, int position) {
        POJOSTATUS obj=pojostatuses.get(position);
        holder.tvFriendUsername.setText(obj.getUsername());
        Glide.with(activity)
                .load(urls.address + "images/"+obj.getImage())
                .skipMemoryCache(true)
                .error(R.drawable.baseline_person_24)// Resize the image to 800x800 pixels
                .into(holder.ivImage);
        holder.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity, StatusView.class);
                i.putExtra("image",obj.getImage());
                activity.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return pojostatuses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivImage;
        TextView tvFriendUsername;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvFriendUsername = itemView.findViewById(R.id.tvFriendUsername);
        }
    }
}
