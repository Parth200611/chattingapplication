package com.mountreachsolution.vibze.User.Adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mountreachsolution.vibze.R;
import com.mountreachsolution.vibze.User.POJO.UserModel;
import com.mountreachsolution.vibze.urls;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<UserModel> userList;

    public UserAdapter(Context context, List<UserModel> userList) {
        this.context = context;
        this.userList = userList;
    }
    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_usergroup, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        UserModel user = userList.get(position);
        holder.txtUserName.setText(user.getName());
        holder.chkSelectUser.setChecked(user.isSelected());

        Glide.with(context)
                .load(urls.address + "images/"+user.getImage())
                .skipMemoryCache(true)
                .error(R.drawable.baseline_person_24)// Resize the image to 800x800 pixels
                .into(holder.imgUser);

        holder.chkSelectUser.setOnCheckedChangeListener((buttonView, isChecked) -> user.setSelected(isChecked));

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public List<UserModel> getSelectedUsers() {
        return userList;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUserName;
        ImageView imgUser;
        CheckBox chkSelectUser;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            imgUser = itemView.findViewById(R.id.imgUser);
            chkSelectUser = itemView.findViewById(R.id.chkSelectUser);
        }
    }
}
