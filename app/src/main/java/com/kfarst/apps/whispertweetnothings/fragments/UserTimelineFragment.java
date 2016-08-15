package com.kfarst.apps.whispertweetnothings.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;

import com.codepath.apps.whispertweetnothings.R;
import com.kfarst.apps.whispertweetnothings.models.Tweet;
import com.kfarst.apps.whispertweetnothings.support.ColoredSnackBar;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kfarst on 8/8/16.
 */
public class UserTimelineFragment extends TweetsListFragment {
    private static String ARG_USER_SCREEN_NAME = "user_screen_name";
    private String userScreenName;

    public static UserTimelineFragment newInstance(String userScreenName) {
        UserTimelineFragment fragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_SCREEN_NAME, userScreenName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        if (getArguments() != null)  {
            userScreenName = getArguments().getString(ARG_USER_SCREEN_NAME);
        }
        super.onCreate(bundle);
    }

    @Override
    protected void populateTimeline(Long maxId) {
        // If not online and no tweets loaded, fetch them from the database
        //if (!isOnline() && tweets.size() == 0) {
        //    tweets.addAll(Tweet.recentItems());
        //    adapter.notifyDataSetChanged();
        //    Snackbar offlineSnackbar = Snackbar.make(lvTweets, R.string.offline_warning_message, Snackbar.LENGTH_LONG);
        //    ColoredSnackBar.warning(offlineSnackbar).show();
        //}

        client.getTimelineFor("user", maxId, userScreenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Tweet> list = new ArrayList<Tweet>();

                // No maxId clears the DB of tweets and adds new tweets from the refreshed timeline
                // so a large number of tweets is not built up over time
                if (maxId == null) {
                    Tweet.deleteAll();
                    tweets.clear();
                    pullToRefreshView.setRefreshing(false);
                }

                appendTweets(Tweet.fromJSON(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Snackbar errorSnackbar = Snackbar.make(lvTweets, R.string.offline_error_message, Snackbar.LENGTH_SHORT);
                ColoredSnackBar.alert(errorSnackbar).show();
            }
        });
    }

    @Override
    protected boolean hideReplyButton() {
        return true;
    }
}
