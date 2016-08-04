package com.kfarst.apps.whispertweetnothings.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/*
 * This is a temporary, sample model that demonstrates the basic structure
 * of a SQLite persisted Model object. Check out the ActiveAndroid wiki for more details:
 * https://github.com/pardom/ActiveAndroid/wiki/Creating-your-database-model
 * 
 */
//@Table(name = "tweets")
@Parcel
public class Tweet /*extends Model*/ {
	// Define table fields
	//@Column(name = "body")
	private String status = "";

	//@Column(name = "uid")
	private Long id;

	//@Column(name = "createdAt")
	private String createdAt;

    public void setUser(User user) {
        this.user = user;
    }

    //@Column(name = "user")
    private User user;

    private String mediaUrl;

	public Tweet() {
		super();
	}

	// Getters
	public String getStatus() {
		return status;
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

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
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
            tweet.status = jsonObject.getString("text");
            tweet.id = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));

            JSONObject mediaObj = (JSONObject) jsonObject.getJSONObject("entities").getJSONArray("media").get(0);
            tweet.mediaUrl = mediaObj.getString("media_url");
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

    public void setStatus(String status) {
        this.status = status;
    }
}
