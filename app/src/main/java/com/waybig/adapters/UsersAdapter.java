package com.waybig.adapters;

import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.waybig.preferences.CircularImageView;
import java.util.List;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import com.waybig.models.User;
import com.waybig.R;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {
    private List<User> userList;
    private Context context;

    public UsersAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    public void setUsers(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged(); // Notify the adapter that the data has changed.
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_item, parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userName.setText(user.getName());
        holder.userEmail.setText(user.getEmail());

        // Load the profile image using Picasso (you can use any image loading library)
        Glide.with(context).load(user.getImage()).circleCrop().into(holder.userProfileImage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImage;
        TextView userName;
        TextView userEmail;

        public UsersViewHolder(View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.users_image_view);
            userName = itemView.findViewById(R.id.users_name);
            userEmail = itemView.findViewById(R.id.users_email);
        }
    }
}