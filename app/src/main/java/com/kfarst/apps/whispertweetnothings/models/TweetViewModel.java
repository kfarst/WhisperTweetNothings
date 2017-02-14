package com.kfarst.apps.whispertweetnothings.models;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.codepath.apps.whispertweetnothings.R;
import com.kfarst.apps.whispertweetnothings.support.CounterTextView;
import com.kfarst.apps.whispertweetnothings.support.TweetSubmitButton;

import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.lang.System.in;

/**
 * Created by kfarst on 8/2/16.
 */
public class TweetViewModel extends BaseObservable {
    @BindView(R.id.counterTextView)
    CounterTextView counterTextView;
    @BindView(R.id.etTweet)
    EditText tweetBody;
    @BindView(R.id.btnTweet)
    TweetSubmitButton tweetSubmitButton;

    private static Integer TOTAL_TWEET_LENGTH = 140;
    private Tweet tweet;

    public TweetViewModel(View view, Tweet tweet) {
        ButterKnife.bind(this, view);

        this.tweet = tweet;

        tweetBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateFromStatus(editable.toString());
            }
        });

        tweetBody.setText(tweet.getStatus());
    }

    private void updateFromStatus(String status) {
        counterTextView.countChanged(TOTAL_TWEET_LENGTH - status.length(),
                TOTAL_TWEET_LENGTH < status.length());

        tweetSubmitButton.countChanged(TOTAL_TWEET_LENGTH - status.length(),
                TOTAL_TWEET_LENGTH < status.length() || status.length() == 0);

        tweet.setStatus(status);
    }
}
