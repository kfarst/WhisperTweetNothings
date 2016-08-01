package com.kfarst.apps.whispertweetnothings.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

/*
 * This is a temporary, sample model that demonstrates the basic structure
 * of a SQLite persisted Model object. Check out the ActiveAndroid wiki for more details:
 * https://github.com/pardom/ActiveAndroid/wiki/Creating-your-database-model
 * 
 */
//@Table(name = "tweets")
public class Tweet /*extends Model*/ {
	// Define table fields
	//@Column(name = "body")
	private String text;

	//@Column(name = "uid")
	private Long id;

	//@Column(name = "createdAt")
	private String createdAt;

    //@Column(name = "user")
    private User user;

	public Tweet() {
		super();
	}

	// Getters
	public String getText() {
		return text;
	}

    public Long getId() {
        return id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    // Record Finders
    //public static Tweet byId(long uid) {
    //  return new Select().from(Tweet.class).where("uid = ?", uid).executeSingle();
    //}

    //public static List<Tweet> recentItems() {
    //  return new Select().from(Tweet.class).orderBy("uid DESC").limit("300").execute();
    //}

    // Decodes tweet json into tweet model object
    public static Tweet fromJSON(JSONObject jsonObject) {
       Tweet tweet = new Tweet();

        try {
            tweet.text = jsonObject.getString("text");
            tweet.id = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    // Decodes array of tweet json results into tweet model objects
    public static ArrayList<Tweet> fromJSON(JSONArray jsonArray) {
        JSONObject tweetJson;
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());
        // Process each result in json array, decode and convert to tweet object
        for (int i=0; i < jsonArray.length(); i++) {
            try {
                tweetJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Tweet tweet = Tweet.fromJSON(tweetJson);
            if (tweet != null) {
                tweets.add(tweet);
            }
        }

        return tweets;
    }
}