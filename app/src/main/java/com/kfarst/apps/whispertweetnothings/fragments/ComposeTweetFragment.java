package com.kfarst.apps.whispertweetnothings.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.codepath.apps.whispertweetnothings.R;
import com.codepath.apps.whispertweetnothings.databinding.FragmentComposeTweetBinding;
import com.kfarst.apps.whispertweetnothings.models.Tweet;
import com.kfarst.apps.whispertweetnothings.models.TweetViewModel;
import com.kfarst.apps.whispertweetnothings.models.User;

import org.parceler.Parcels;

import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ComposeTweetFragment extends DialogFragment {
    public interface PostStatusDialogListener {
        void onFinishEditDialog(Tweet tweet);
    }

    private FragmentComposeTweetBinding binding;
    private User currentUser;
    private Tweet tweet = new Tweet();
    private static String ARG_USER = "currentUser";

    // TODO: Rename and change types and number of parameters
    public static ComposeTweetFragment newInstance(User currentUser) {
        ComposeTweetFragment fragment = new ComposeTweetFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, Parcels.wrap(currentUser));
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

        if (getArguments() != null) {
            currentUser = Parcels.unwrap(getArguments().getParcelable(ARG_USER));

            Glide.with(binding.ivOwnProfileImage.getContext())
                    .load(currentUser.getProfileImageUrl())
                    .bitmapTransform(new RoundedCornersTransformation(binding.ivOwnProfileImage.getContext(), 10, 0))
                    .into(binding.ivOwnProfileImage);
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
}
