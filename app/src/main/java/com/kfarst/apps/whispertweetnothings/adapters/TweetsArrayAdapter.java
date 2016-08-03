package com.kfarst.apps.whispertweetnothings.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.whispertweetnothings.R;
import com.kfarst.apps.whispertweetnothings.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by kfarst on 7/25/16.
 */
public class TweetsArrayAdapter extends RecyclerView.Adapter<TweetsArrayAdapter.ViewHolder> {
    private List<Tweet> mTweets;
    private AdapterView.OnItemClickListener listener;

    // Pass in the contact array into the constructor
    public TweetsArrayAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
        @BindView(R.id.tvUserName) TextView tvUserName;
        @BindView(R.id.tvTweetBody) TextView tvTweetBody;
        @BindView(R.id.tvRelativeTimestamp) TextView tvRelativeTimestamp;

        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void bind(final Tweet article, final AdapterView.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   //Context context = view.getContext();
                   //Intent i = new Intent(context, TimelineActivity.class);
                   //i.putExtra("article", article);
                   //context.startActivity(i);
                }
            });
        }
    }

    @Override
    public TweetsArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View listItemView = inflater.inflate(R.layout.item_tweet, parent, false);

        ButterKnife.bind(listItemView);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(listItemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TweetsArrayAdapter.ViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);

        holder.ivProfileImage.setImageResource(android.R.color.darker_gray);

       Glide.with(holder.itemView.getContext())
               .load(tweet.getUser().getProfileImageUrl())
               .bitmapTransform(new RoundedCornersTransformation(holder.itemView.getContext(), 10, 0))
               .into(holder.ivProfileImage);

       holder.tvUserName.setText(tweet.getUser().getScreenName());
       holder.tvTweetBody.setText(tweet.getStatus());
        holder.tvRelativeTimestamp.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    private String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
