package com.mountreachsolution.vibze.User.Adpter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mountreachsolution.vibze.R;

import java.util.ArrayList;
import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> implements Filterable {
    private List<String> friendList;
    private List<String> friendListFull; // Copy of the full list for filtering

    public FriendListAdapter(List<String> friendList) {
        this.friendList = friendList;
        this.friendListFull = new ArrayList<>(friendList); // Copy for filtering
    }

    @NonNull
    @Override
    public FriendListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListAdapter.ViewHolder holder, int position) {
        holder.tvFriendUsername.setText(friendList.get(position));
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

    // Enable filtering for search functionality
    @Override
    public Filter getFilter() {
        return friendFilter;
    }

    private final Filter friendFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<String> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(friendListFull); // Show full list when search is empty
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (String friend : friendListFull) {
                    if (friend.toLowerCase().contains(filterPattern)) {
                        filteredList.add(friend);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            friendList.clear();
            friendList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
