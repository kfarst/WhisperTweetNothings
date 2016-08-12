package com.kfarst.apps.whispertweetnothings.models;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.github.underscore.$;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/*
 * This is a temporary, sample model that demonstrates the basic structure
 * of a SQLite persisted Model object. Check out the ActiveAndroid wiki for more details:
 * https://github.com/pardom/ActiveAndroid/wiki/Creating-your-database-model
 * 
 */
@Table(name = "tweets")
@Parcel(analyze={Tweet.class})
public class Tweet extends Model {
	// Define table fields
	@Column(name = "status")
	private String status = "";

	@Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private Long uid;

	@Column(name = "createdAt")
	private String createdAt;

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "user")
    private User user;

    @Column(name = "mediaUrl")
    private String mediaUrl;

    @Column(name = "retweetCount")
    private int retweetCount;

	public Tweet() {
		super();
	}

	// Setters/Getters
	public String getStatus() {
		return status;
	}

    public Long getUid() {
        return uid;
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

    public int getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }


    // Record Finders
    public static Tweet byId(long uid) {
      return new Select().from(Tweet.class).where("uid = ?", uid).executeSingle();
    }

    public static List<Tweet> recentItems() {
      return new Select().from(Tweet.class).orderBy("uid DESC").limit("300").execute();
    }

    public static Tweet findOrCreateFromJSON(JSONObject json) {
        Tweet existingTweet = null;
        long uId = 0;

        try {
            uId = json.getLong("id");
            existingTweet = byId(uId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (existingTweet != null) {
            // found and return existing
            return existingTweet;
        } else {
            // create and return new tweet
            Tweet tweet = Tweet.fromJSON(json);
            tweet.save();
            return tweet;
        }
    }

    public static void deleteById(long uid) {
        new Delete().from(Tweet.class).where("uid = ?", uid).execute();
    }

    // Decodes tweet json into tweet model object
    public static Tweet fromJSON(JSONObject jsonObject) {
       Tweet tweet = new Tweet();

        try {
            tweet.status = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.retweetCount = jsonObject.getInt("retweet_count");
            tweet.user = User.findOrCreateFromJSON(jsonObject.getJSONObject("user"));

            JSONObject entitiesObj = (JSONObject) jsonObject.optJSONObject("entities");
            JSONArray mediaObj;

            if (entitiesObj != null) {
                mediaObj = entitiesObj.optJSONArray("media");

                if (mediaObj != null) {
                    JSONObject media = (JSONObject) mediaObj.get(0);
                    tweet.mediaUrl = media.getString("media_url");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    // Decodes array of tweet json results into tweet model objects
    public static ArrayList<Tweet> fromJSON(JSONArray jsonArray) {
        JSONObject tweetJson;
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

       ActiveAndroid.beginTransaction();
       try {
            // Process each result in json array, decode and convert to tweet object
            for (int i=0; i < jsonArray.length(); i++) {
                try {
                    tweetJson = jsonArray.getJSONObject(i);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }

                Tweet tweet = findOrCreateFromJSON(tweetJson);
                if (tweet != null) {
                    tweets.add(tweet);
                }
            }

            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
           return tweets;
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static void deleteAll() {
        new Delete().from(Tweet.class).execute();
    }

    public static void deleteAllById(ArrayList<Tweet> tweets) {
        ActiveAndroid.beginTransaction();
        try {
            $.each(tweets, tweet -> deleteById(tweet.getUid()));
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }
}
