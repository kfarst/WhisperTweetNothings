package com.kfarst.apps.whispertweetnothings.api;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.codepath.apps.whispertweetnothings.R;
import com.kfarst.apps.whispertweetnothings.models.Tweet;
import com.kfarst.apps.whispertweetnothings.models.User;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 *
 *     TwitterClient client = TwitterApplication.getRestClient();
 *     // use client to send requests to API
 *
 */
public class TwitterApplication extends com.activeandroid.app.Application {
	private static Context context;

	@Override
	public void onCreate() {
		deleteDatabase("RestClient.db");

		Configuration dbConfiguration = new Configuration.Builder(this)
		        .setDatabaseName("RestClient.db")
		        .setDatabaseVersion(1)
		        .addModelClasses(Tweet.class)
		        .addModelClasses(User.class)
		        .create();
		ActiveAndroid.initialize(dbConfiguration);

		super.onCreate();

		TwitterApplication.context = this;

		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
				.setDefaultFontPath("fonts/HelveticaNeue-Light.ttf")
				.setFontAttrId(R.attr.fontPath)
				.build()
        );
	}

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

	public static TwitterClient getRestClient() {
		return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, TwitterApplication.context);
	}
}