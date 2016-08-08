package com.kfarst.apps.whispertweetnothings.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.apps.whispertweetnothings.R;
import com.kfarst.apps.whispertweetnothings.api.TwitterApplication;
import com.kfarst.apps.whispertweetnothings.fragments.ComposeTweetFragment;
import com.kfarst.apps.whispertweetnothings.models.Tweet;
import com.kfarst.apps.whispertweetnothings.support.ColoredSnackBar;
import com.kfarst.apps.whispertweetnothings.support.LinkifiedTextView;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class TweetActivity extends AppCompatActivity implements ComposeTweetFragment.PostStatusDialogListener {
    @BindView(R.id.ivTweetProfileImage) ImageView ivTweetProfileImage;
    @BindView(R.id.tvTweetUserName) TextView tvTweetUserName;
    @BindView(R.id.tvTweetUserHandle) TextView tvTweetUserHandle;
    @BindView(R.id.tvTweetDetailStatus) LinkifiedTextView tvTweetDetailStatus;
    @BindView(R.id.ivTweetMedia) ImageView ivTweetMedia;
    @BindView(R.id.tvRetweetCount) TextView tvRetweetCount;
    @BindView(R.id.ivTweetReply) ImageView ivTweetReply;
    @BindView(R.id.divider3) View bottomDivider;

    public static final int REQUEST_CODE = 200;

    private Tweet tweet;
    private View activityView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityView = LayoutInflater.from(getBaseContext()).inflate(R.layout.activity_tweet, null);
        setContentView(activityView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tweet");

        // Color back arrow with Twitter blue
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.twitter_blue), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        ButterKnife.bind(this);

        // If offline hide the tweet reply button
        if (getIntent().getBooleanExtra("isOnline", false)) {
            ivTweetReply.setVisibility(View.VISIBLE);
            bottomDivider.setVisibility(View.VISIBLE);
        } else {
            ivTweetReply.setVisibility(View.GONE);
            bottomDivider.setVisibility(View.GONE);
        }

        setupViews();
    }

    private void setupViews() {
        Glide.with(ivTweetProfileImage.getContext())
                .load(tweet.getUser().getProfileImageUrl())
                .bitmapTransform(new jp.wasabeef.glide.transformations.RoundedCornersTransformation(ivTweetProfileImage.getContext(), 10, 0))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivTweetProfileImage);

        tvTweetUserName.setText(tweet.getUser().getName());
        tvTweetUserHandle.setText("@" + tweet.getUser().getScreenName());
        tvTweetDetailStatus.setText(tweet.getStatus());
        tvRetweetCount.setText(""+tweet.getRetweetCount());

        if (tweet.getMediaUrl() != null) {
            Glide.with(ivTweetMedia.getContext())
                    .load(tweet.getMediaUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivTweetMedia);

            // Hide image view by default, only show if an image was attached to the tweet
            ivTweetMedia.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.ivTweetReply)
    public void openComposeDialog(View view) {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetFragment composeDialog = ComposeTweetFragment.newInstance(tweet);
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        composeDialog.show(fm, "fragment_compose_tweet");
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();

        // Return the tweet to check if it has been posted as a reply
        returnIntent.putExtra("tweet", Parcels.wrap(tweet));
        returnIntent.putExtra("code", REQUEST_CODE);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onFinishEditDialog(Tweet tweetToSave) {
        TwitterApplication.getRestClient().postStatus(tweetToSave, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {;
                tweet = Tweet.findOrCreateFromJSON(response);
                Snackbar snackbar = Snackbar.make(activityView, R.string.tweet_success, Snackbar.LENGTH_SHORT);
                ColoredSnackBar.confirm(snackbar).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String errorMessage = getErrorsFromResponse(errorResponse);

                // If API errors, show them to the user
                if (!TextUtils.isEmpty(errorMessage)) {
                    Snackbar snackbar = Snackbar.make(activityView, errorMessage, Snackbar.LENGTH_SHORT);
                    ColoredSnackBar.alert(snackbar).show();
                }
            }

            public String getErrorsFromResponse(JSONObject errorResponse) {
                ArrayList<String> errorString = new ArrayList<String>();

                try {
                    JSONArray errors = errorResponse.getJSONArray("errors");

                    for (int i = 0; i < errors.length(); i++) {
                        errorString.add(String.valueOf(errors.getJSONObject(i).get("message")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Concatenate errors into one string separated by a space
                return TextUtils.join(" ", errorString);
            }
        });
    }
}
