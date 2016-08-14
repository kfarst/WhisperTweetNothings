package com.kfarst.apps.whispertweetnothings.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.apps.whispertweetnothings.R;
import com.codepath.apps.whispertweetnothings.databinding.ActivityTimelineBinding;
import com.kfarst.apps.whispertweetnothings.adapters.TimelineFragmentPagerAdapter;
import com.kfarst.apps.whispertweetnothings.api.TwitterApplication;
import com.kfarst.apps.whispertweetnothings.api.TwitterClient;
import com.kfarst.apps.whispertweetnothings.fragments.ComposeTweetFragment;
import com.kfarst.apps.whispertweetnothings.models.TimelineViewModel;
import com.kfarst.apps.whispertweetnothings.models.Tweet;
import com.kfarst.apps.whispertweetnothings.models.User;
import com.kfarst.apps.whispertweetnothings.support.ColoredSnackBar;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragment.PostStatusDialogListener {
    public interface PostTweetListener {
        public void postNewTweet(Tweet tweet);
    }

    @BindView(R.id.ivToolbarProfileImage) ImageView ivToolbarProfileImage;
    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.sliding_tabs) TabLayout slidingTabs;

    public static final int REFRESH_DELAY = 2000;
    public static final int REQUEST_CODE = 200;

    private TwitterClient client = TwitterApplication.getRestClient();
    private User currentUser;
    private TimelineViewModel timelineViewModel = new TimelineViewModel();
    private ActivityTimelineBinding binding;
    private PostTweetListener postTweetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        binding.setTimelineViewModel(timelineViewModel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        // Custom toolbar for displaying rounded profile image
        getSupportActionBar().setCustomView(R.layout.actionbar_timeline);

        ButterKnife.bind(this);

        setupViews();
        getCurrentUser();
    }

    // Assign the listener implementing events interface that will receive the events
    public void setPostTweetListener(PostTweetListener listener) {
        postTweetListener = listener;
    }

    private void getCurrentUser() {
        currentUser = User.byCurrentUser();

        // If currentUser is null we do not have the user persisted, fetch from the API
        if (currentUser == null) {
            client.getCurrentUser(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    currentUser = User.findOrCreateFromJSON(response);
                    // Need to set the user to currentUser for next DB query
                    currentUser.setCurrentUser(true);
                    currentUser.save();

                    Glide.with(ivToolbarProfileImage.getContext())
                            .load(currentUser.getProfileImageUrl())
                            .bitmapTransform(new CropCircleTransformation(ivToolbarProfileImage.getContext()))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(ivToolbarProfileImage);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            });
        } else {
            // Fetch the profile image since the user is already persisted at this point
            Glide.with(ivToolbarProfileImage.getContext())
                    .load(currentUser.getProfileImageUrl())
                    .bitmapTransform(new CropCircleTransformation(ivToolbarProfileImage.getContext()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivToolbarProfileImage);
        }
    }

    private void setupViews() {
        viewPager.setAdapter(new TimelineFragmentPagerAdapter(getSupportFragmentManager(),
                TimelineActivity.this));
        slidingTabs.setupWithViewPager(viewPager);
   }

    @OnClick(R.id.fabCompose)
    public void openComposeDialog(View view) {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetFragment composeDialog = ComposeTweetFragment.newInstance(null);
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        composeDialog.show(fm, "fragment_compose_tweet");
    }

    @Override
    public void onFinishEditDialog(Tweet tweet) {
        client.postStatus(tweet, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // New tweet has been composed, add to the timeline
                postTweetListener.postNewTweet(Tweet.fromJSON(response));
                Snackbar snackbar = Snackbar.make(viewPager, R.string.tweet_success_message, Snackbar.LENGTH_SHORT);
                ColoredSnackBar.confirm(snackbar).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                String errorMessage = getErrorsFromResponse(errorResponse);

                // If API returns errors, show them to the user
                if (!TextUtils.isEmpty(errorMessage)) {
                    Snackbar snackbar = Snackbar.make(viewPager, errorMessage, Snackbar.LENGTH_SHORT);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Tweet tweet = Parcels.unwrap(data.getExtras().getParcelable("tweet"));

            // New tweet is always created, so only add to timeline if it has been posted to the API
            if (tweet.getUid() != null) {
                postTweetListener.postNewTweet(tweet);
            }
        }
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            timelineViewModel.isOnline.set(exitValue == 0);
            return timelineViewModel.isOnline.get();
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        timelineViewModel.isOnline.set(false);
        return timelineViewModel.isOnline.get();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
