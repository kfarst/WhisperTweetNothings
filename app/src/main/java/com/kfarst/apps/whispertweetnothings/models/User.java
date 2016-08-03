package com.kfarst.apps.whispertweetnothings.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/*
 * This is a temporary, sample model that demonstrates the basic structure
 * of a SQLite persisted Model object. Check out the ActiveAndroid wiki for more details:
 * https://github.com/pardom/ActiveAndroid/wiki/Creating-your-database-model
 *
 */
@Parcel
//@Table(name = "users")
public class User /*extends Model*/ {
    // Define table fields
    //@Column(name = "name")
    public String name;

    //@Column(name = "uid")
    public Long id;

    //@Column(name = "screenName")
    public String screenName;

    //@Column(name = "profileImageUrl")
    public String profileImageUrl;

    public User() {
        super();
    }

    // Parse model from JSON
    public User(JSONObject object){
        super();

        try {
            this.name = object.getString("name");
            this.id = object.getLong("id");
            this.screenName = object.getString("screen_name");
            this.profileImageUrl = object.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Getters
    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    // Record Finders
    //public static User byId(long uid) {
    //    return new Select().from(User.class).where("uid = ?", uid).executeSingle();
    //}

    //public static List<User> recentItems() {
    //    return new Select().from(User.class).orderBy("uid DESC").limit("300").execute();
    //}

    public static User fromJSON(JSONObject jsonObject) {
        User user = new User();

        try {
            user.name = jsonObject.getString("name");
            user.id = jsonObject.getLong("id");
            user.screenName = jsonObject.getString("screen_name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }
}

