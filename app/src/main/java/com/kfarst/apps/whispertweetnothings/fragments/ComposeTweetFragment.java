package com.kfarst.apps.whispertweetnothings.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.apps.whispertweetnothings.R;
import com.codepath.apps.whispertweetnothings.databinding.FragmentComposeTweetBinding;
import com.kfarst.apps.whispertweetnothings.api.TwitterApplication;
import com.kfarst.apps.whispertweetnothings.api.TwitterClient;
import com.kfarst.apps.whispertweetnothings.models.Tweet;
import com.kfarst.apps.whispertweetnothings.models.TweetViewModel;
import com.kfarst.apps.whispertweetnothings.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ComposeTweetFragment extends DialogFragment {
    public interface PostStatusDialogListener {
        void onFinishEditDialog(Tweet tweet);
    }

    private FragmentComposeTweetBinding binding;
    private Tweet respondingTweet;
    private User currentUser;
    private Tweet tweet = new Tweet();
    private static String ARG_RESPONDING_TWEET = "currentUser";

    // TODO: Rename and change types and number of parameters
    public static ComposeTweetFragment newInstance(Tweet respondingTweet) {
        ComposeTweetFragment fragment = new ComposeTweetFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_RESPONDING_TWEET, Parcels.wrap(respondingTweet));
        fragment.setArguments(args);
        return fragment;
    }

    public ComposeTweetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compose_tweet, container, false);
        binding = FragmentComposeTweetBinding.bind(view);
        binding.setTweetViewModel(new TweetViewModel(tweet));
        ButterKnife.bind(this, view);

        getCurrentUser();

        if (getArguments() != null) {
            respondingTweet = Parcels.unwrap(getArguments().getParcelable(ARG_RESPONDING_TWEET));

            if (respondingTweet != null) {
                binding.getTweetViewModel().getTweet().setStatus("@" + respondingTweet.getUser().getScreenName());
            }
        }

        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
           int width = ViewGroup.LayoutParams.MATCH_PARENT;
           int height = ViewGroup.LayoutParams.MATCH_PARENT;
           dialog.getWindow().setLayout(width, height);
        }
    }

    @OnClick(R.id.ibClose)
    public void closeDialog(View view) {
        dismiss();
    }

    @OnClick(R.id.btnTweet)
    public void postStatus(View view) {
        PostStatusDialogListener listener = (PostStatusDialogListener) getActivity();
        listener.onFinishEditDialog(tweet);
        dismiss();
    }

    private void getCurrentUser() {
        TwitterApplication.getRestClient().getCurrentUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                currentUser = User.findOrCreateFromJSON(response);

                Glide.with(binding.ivOwnProfileImage.getContext())
                        .load(currentUser.getProfileImageUrl())
                        .bitmapTransform(new RoundedCornersTransformation(binding.ivOwnProfileImage.getContext(), 10, 0))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.ivOwnProfileImage);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }
}
