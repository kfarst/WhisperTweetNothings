package com.kfarst.apps.whispertweetnothings.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

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
@Table(name = "users")
@Parcel(analyze={User.class})
public class User extends Model {
    // Define table fields
    @Column(name = "name")
    public String name;

    @Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public Long uid;

    @Column(name = "screenName")
    public String screenName;

    @Column(name = "profileImageUrl")
    public String profileImageUrl;

    @Column(name = "profileBackgroundImageUrl")
    public String profileBackgroundImageUrl;

    @Column(name = "currentUser")
    public boolean currentUser;

    @Column(name = "friendsCount")
    public Integer friendsCount;

    @Column(name = "followersCount")
    public Integer followersCount;

    @Column(name = "description")
    public String description;

    public User() {
        super();
    }

    // Parse model from JSON
    public User(JSONObject object){
        super();

        try {
            this.name = object.getString("name");
            this.uid = object.getLong("id");
            this.screenName = object.getString("screen_name");
            this.profileImageUrl = object.getString("profile_image_url");
            this.description = object.getString("description");
            this.profileBackgroundImageUrl = object.optString("profile_background_image_url");
            this.friendsCount = object.getInt("friends_count");
            this.followersCount = object.getInt("followers_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Getters
    public String getName() {
        return name;
    }

    public Long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getProfileBackgroundImageUrl() {
        return profileBackgroundImageUrl;
    }

    public void setProfileBackgroundImageUrl(String profileBackgroundImageUrl) {
        this.profileBackgroundImageUrl = profileBackgroundImageUrl;
    }

    public boolean isCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(boolean currentUser) {
        this.currentUser = currentUser;
    }

    public Integer getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(Integer friendsCount) {
        this.friendsCount = friendsCount;
    }

    public Integer getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Record Finders
    public static User byId(long uid) {
        return new Select().from(User.class).where("uid = ?", uid).executeSingle();
    }

    public static User byCurrentUser() {
        return new Select().from(User.class).where("currentUser = ?", true).executeSingle();
    }

    public static List<User> recentItems() {
        return new Select().from(User.class).orderBy("uid DESC").limit("300").execute();
    }

    public static User findOrCreateFromJSON(JSONObject json) {
        User existingUser = new User();
        long uId = 0;

        try {
            uId = json.getLong("id");
            existingUser = byId(uId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (existingUser != null) {
            // found and return existing
            return existingUser;
        } else {
            // create and return new user
            User user = User.fromJSON(json);
            user.save();
            return user;
        }
    }

    public static User fromJSON(JSONObject jsonObject) {
        User user = new User();

        try {
            user.name = jsonObject.getString("name");
            user.uid = jsonObject.getLong("id");
            user.screenName = jsonObject.getString("screen_name");
            user.description = jsonObject.getString("description");
            user.profileImageUrl = jsonObject.getString("profile_image_url");
            user.profileBackgroundImageUrl = jsonObject.optString("profile_background_image_url");
            user.friendsCount = jsonObject.getInt("friends_count");
            user.followersCount = jsonObject.getInt("followers_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    public static ArrayList<User> fromJSON(JSONArray jsonArray) {
        JSONObject userJson;
        ArrayList<User> users = new ArrayList<User>(jsonArray.length());

        // Process each result in json array, decode and convert to tweet object
        for (int i=0; i < jsonArray.length(); i++) {
            try {
                userJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            User user = fromJSON(userJson);
            if (user != null) {
                users.add(user);
            }
        }

        return users;
    }
}

