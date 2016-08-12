package com.kfarst.apps.whispertweetnothings.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codepath.apps.whispertweetnothings.R;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.kfarst.apps.whispertweetnothings.models.User;
import com.kfarst.apps.whispertweetnothings.support.ViewHelper;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UserProfileActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {

    @BindView(R.id.ivUserProfileBackground) ImageView mIvUserProfileBackground;
    @BindView(R.id.toolbar) View mToolbarView;
    @BindView(R.id.scroll) ObservableScrollView mScrollView;
    @BindView(R.id.ivUserProfileImage) ImageView ivUserProfileImage;
    @BindView(R.id.tvUserProfileName) TextView tvUserProfileName;
    @BindView(R.id.tvUserProfileHandle) TextView tvUserProfileHandle;
    @BindView(R.id.tvUserProfileDescription) TextView tvUserProfileDescription;
    @BindView(R.id.tvUserProfileFollowingCount) TextView tvUserProfileFollowingCount;
    @BindView(R.id.tvUserProfileFollowersCount) TextView tvUserProfileFollowersCount;

    private int mParallaxImageHeight;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setupViews();
    }

    private void setupViews() {
        ButterKnife.bind(this);

        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.primary)));

        mScrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);

        if (user.getProfileBackgroundImageUrl() != null) {
            Glide.with(mIvUserProfileBackground.getContext())
                    .load(user.getProfileBackgroundImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mIvUserProfileBackground);
        }

        Glide.with(ivUserProfileImage.getContext())
                .load(user.getProfileImageUrl())
                .bitmapTransform(new jp.wasabeef.glide.transformations.RoundedCornersTransformation(ivUserProfileImage.getContext(), 5, 0))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivUserProfileImage);

        tvUserProfileName.setText(user.getName());
        tvUserProfileHandle.setText("@"+user.getScreenName());
        tvUserProfileDescription.setText(user.getDescription());
        tvUserProfileFollowingCount.setText(""+user.getFriendsCount());
        tvUserProfileFollowersCount.setText(""+user.getFollowersCount());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.primary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(mIvUserProfileBackground, scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
