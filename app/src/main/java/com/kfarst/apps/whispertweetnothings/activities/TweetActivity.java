package com.kfarst.apps.whispertweetnothings.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.apps.whispertweetnothings.R;
import com.kfarst.apps.whispertweetnothings.models.Tweet;
import com.kfarst.apps.whispertweetnothings.support.LinkifiedTextView;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetActivity extends AppCompatActivity {
    @BindView(R.id.ivTweetProfileImage) ImageView ivTweetProfileImage;
    @BindView(R.id.tvTweetUserName) TextView tvTweetUserName;
    @BindView(R.id.tvTweetDetailStatus) LinkifiedTextView tvTweetDetailStatus;
    @BindView(R.id.ivTweetMedia) ImageView ivTweetMedia;

    private Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tweet");

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.twitter_blue), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        ButterKnife.bind(this);

        setupViews();
    }

    private void setupViews() {
        Glide.with(ivTweetProfileImage.getContext())
                .load(tweet.getUser().getProfileImageUrl())
                .bitmapTransform(new jp.wasabeef.glide.transformations.RoundedCornersTransformation(ivTweetProfileImage.getContext(), 10, 0))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivTweetProfileImage);

        tvTweetUserName.setText(tweet.getUser().getScreenName());
        tvTweetDetailStatus.setText(tweet.getStatus());

        if (tweet.getMediaUrl() != null) {
            Glide.with(ivTweetMedia.getContext())
                    .load(tweet.getMediaUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivTweetMedia);

            ivTweetMedia.setVisibility(View.VISIBLE);
        }
    }

}
