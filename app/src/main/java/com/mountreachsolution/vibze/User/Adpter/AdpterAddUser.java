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

import com.bumptech.glide.Glide;
import com.mountreachsolution.vibze.R;
import com.mountreachsolution.vibze.User.POJO.POJOADDUSER;
import com.mountreachsolution.vibze.urls;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdpterAddUser extends RecyclerView.Adapter<AdpterAddUser.ViewHolder> {
    Activity activity;
    List<POJOADDUSER>pojoaddusers;

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
        View view = LayoutInflater.from(activity).inflate(R.layout.adduser,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdpterAddUser.ViewHolder holder, int position) {
        POJOADDUSER user = pojoaddusers.get(position);

        holder.tvUsername.setText(user.getUsername());
        holder.tvName.setText(user.getName());
        Glide.with(activity)
                .load(urls.address + "images/"+user.getImage())
                .skipMemoryCache(true)
                .error(R.drawable.baseline_person_24)
                .into(holder.profileImage);



        // Handle Add Button Click
        holder.btnAdd.setOnClickListener(v -> {
            Toast.makeText(activity, "Request sent to " + user.getUsername(), Toast.LENGTH_SHORT).show();

        });

    }

    @Override
    public int getItemCount() {
        return pojoaddusers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvName;
        CircleImageView profileImage;
        Button  btnAdd;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvName = itemView.findViewById(R.id.tvName);
            profileImage = itemView.findViewById(R.id.profileImage);
            btnAdd = itemView.findViewById(R.id.btnAddUser);
        }
    }
}
