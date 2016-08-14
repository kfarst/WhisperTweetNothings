package com.kfarst.apps.whispertweetnothings.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.apps.whispertweetnothings.R;
import com.kfarst.apps.whispertweetnothings.activities.UserProfileActivity;
import com.kfarst.apps.whispertweetnothings.models.User;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by kfarst on 7/25/16.
 */
public class UsersArrayAdapter extends RecyclerView.Adapter<UsersArrayAdapter.ViewHolder> {
    public List<User> mUsers;

    // Pass in the contact array into the constructor
    public UsersArrayAdapter(List<User> users) {
        mUsers = users;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.ivUserConnectionImage) ImageView ivUserConnectionImage;
        @BindView(R.id.tvUserConnectionName) TextView tvUserConnectionName;
        @BindView(R.id.tvUserConnectionDescription) TextView tvUserConnectionDescription;

        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(view.getContext(), UserProfileActivity.class);
            i.putExtra("user", Parcels.wrap(mUsers.get(getAdapterPosition())));
            view.getContext().startActivity(i);
        }
    }

    @Override
    public UsersArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View listItemView = inflater.inflate(R.layout.item_user_info, parent, false);

        ButterKnife.bind(listItemView);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(listItemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(UsersArrayAdapter.ViewHolder holder, int position) {
        User user = mUsers.get(position);

        holder.ivUserConnectionImage.setImageResource(android.R.color.darker_gray);

        holder.ivUserConnectionImage.setOnClickListener(holder);

        Glide.with(holder.itemView.getContext())
                .load(user.getProfileImageUrl())
                .bitmapTransform(new RoundedCornersTransformation(holder.itemView.getContext(), 10, 0))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivUserConnectionImage);

        holder.tvUserConnectionName.setText(user.getScreenName());
        holder.tvUserConnectionDescription.setText(user.getDescription());
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
