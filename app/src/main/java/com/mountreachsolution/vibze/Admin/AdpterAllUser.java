package com.mountreachsolution.vibze.Admin;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mountreachsolution.vibze.R;
import com.mountreachsolution.vibze.urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdpterAllUser extends RecyclerView.Adapter<AdpterAllUser.ViewHolder> {
    List<POJOALLUSER> pojoallusers;
    Activity activity;

    public AdpterAllUser(List<POJOALLUSER> pojoallusers, Activity activity) {
        this.pojoallusers = pojoallusers;
        this.activity = activity;
    }
    @NonNull
    @Override
    public AdpterAllUser.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adminalluser,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdpterAllUser.ViewHolder holder, int position) {
        POJOALLUSER user = pojoallusers.get(position);


        holder.tvUsername.setText(user.getUsername());
        holder.tvName.setText(user.getName());
        holder.tvMobile.setText(user.getMobile());
        holder.tvEmail.setText(user.getEmail());
        holder.tvGender.setText(user.getGender());
        Glide.with(activity)
                .load(urls.address + "images/"+user.getImage())
                .skipMemoryCache(true)
                .error(R.drawable.baseline_person_24)// Resize the image to 800x800 pixels
                .into(holder.cvImage);
        holder.btnRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id=user.getId();
                removeuser(id,position);
            }
        });

    }
    private void removeuser(String id, int position) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.put("id",id);
        client.post(urls.removeuserA,requestParams,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    String status=response.getString("status");
                    if (status.equals("success")){
                        pojoallusers.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, pojoallusers.size());
                        Toast.makeText(activity, "user removed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pojoallusers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvName, tvMobile, tvEmail, tvGender;
        CircleImageView cvImage;
        AppCompatButton btnRemoveUser;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cvImage = itemView.findViewById(R.id.cvImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvName = itemView.findViewById(R.id.tvName);
            tvMobile = itemView.findViewById(R.id.tvMobile);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvGender = itemView.findViewById(R.id.tvgender);
            btnRemoveUser = itemView.findViewById(R.id.btnRemoveuser);
        }
    }
}
