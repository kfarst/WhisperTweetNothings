package com.kfarst.apps.whispertweetnothings.activities;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.codepath.apps.whispertweetnothings.R;
import com.kfarst.apps.whispertweetnothings.adapters.UsersArrayAdapter;
import com.kfarst.apps.whispertweetnothings.api.TwitterApplication;
import com.kfarst.apps.whispertweetnothings.api.TwitterClient;
import com.kfarst.apps.whispertweetnothings.models.User;
import com.kfarst.apps.whispertweetnothings.support.ColoredSnackBar;
import com.kfarst.apps.whispertweetnothings.support.DividerItemDecoration;
import com.kfarst.apps.whispertweetnothings.support.EndlessRecyclerViewScrollListener;
import com.kfarst.apps.whispertweetnothings.support.SmoothScrollLinearLayoutManager;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by kfarst on 8/14/16.
 */
public class UserConnectionsActivity extends AppCompatActivity {
    @BindView(R.id.lvUserConnections) RecyclerView lvUserConnections;

    private TwitterClient client = TwitterApplication.getRestClient();
    private ArrayList<User> users;
    private UsersArrayAdapter adapter;
    private String screenName;
    private String listType;
    private String nextCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_connections);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarUserConnections);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        screenName =  getIntent().getStringExtra("screen_name");
        listType = getIntent().getStringExtra("list_type");

        getSupportActionBar().setTitle(getToolbarTitle());

        // Color back arrow with Twitter blue
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.twitter_blue), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        ButterKnife.bind(this);

        setupViews();
        populateUserConnections(null);
    }

    private void setupViews() {
        lvUserConnections.setHasFixedSize(true);

        SmoothScrollLinearLayoutManager linearLayoutManager = new SmoothScrollLinearLayoutManager(this);
        lvUserConnections.setLayoutManager(linearLayoutManager);

        lvUserConnections.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                populateUserConnections(nextCursor);

            }
        });

        lvUserConnections.addItemDecoration(new DividerItemDecoration(this));

        users = new ArrayList<User>();
        adapter = new UsersArrayAdapter(users);
        lvUserConnections.setAdapter(adapter);
    }

    private void populateUserConnections(final String cursor) {
        // If not online and no tweets loaded, fetch them from the database
        //if (!isOnline() && tweets.size() == 0) {
        //    tweets.addAll(Tweet.recentItems());
        //    adapter.notifyDataSetChanged();
        //    Snackbar offlineSnackbar = Snackbar.make(lvUserConnections, R.string.offline_warning_message, Snackbar.LENGTH_LONG);
        //    ColoredSnackBar.warning(offlineSnackbar).show();
        //}

        client.getListOf(listType, screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                nextCursor = response.optString("next_cursor_str");
                users.addAll(User.fromJSON(response.optJSONArray("users")));
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Snackbar errorSnackbar = Snackbar.make(lvUserConnections, R.string.offline_error_message, Snackbar.LENGTH_SHORT);
                ColoredSnackBar.alert(errorSnackbar).show();
            }
        });
    }

    private String getToolbarTitle() {
        String title = listType.equalsIgnoreCase("friends") ? "following" : listType;
        return title.substring(0,1).toUpperCase() + title.substring(1).toLowerCase();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
