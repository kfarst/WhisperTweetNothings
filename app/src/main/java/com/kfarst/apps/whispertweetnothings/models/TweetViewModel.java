package com.kfarst.apps.whispertweetnothings.models;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by kfarst on 8/2/16.
 */
public class TweetViewModel extends BaseObservable {
    private static Integer TOTAL_TWEET_LENGTH = 140;
    private Tweet tweet;
    public ObservableField<Integer> charactersRemaining = new ObservableField<>(TOTAL_TWEET_LENGTH);

    public TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // do nothing
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // do nothing
        }

        @Override
        public void afterTextChanged(Editable editable) {
            tweet.setStatus(editable.toString());
            charactersRemaining.set(TOTAL_TWEET_LENGTH - editable.toString().length());
        }
    };

    public void setTweet(Tweet tweet) {
        this.tweet = tweet;
    }

    public Tweet getTweet() {
        return tweet;
    }

    public TweetViewModel(Tweet tweet) {
        this.tweet = tweet;
    }
}
