package com.mountreachsolution.vibze.User.Adpter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mountreachsolution.vibze.R;
import com.mountreachsolution.vibze.User.GroupChat;
import com.mountreachsolution.vibze.User.POJO.GroupModel;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private List<GroupModel> groupList;
    Activity activity;

    public GroupAdapter(List<GroupModel> groupList, Activity activity) {
        this.groupList = groupList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.ViewHolder holder, int position) {
        GroupModel group = groupList.get(position);
        holder.groupName.setText(group.getGroupName());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity, GroupChat.class);
                i.putExtra("groupId", group.getGroupId());
                i.putExtra("Groupname",group.getGroupName());
                activity.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.text1);
            card = itemView.findViewById(R.id.card);
        }
    }
}
