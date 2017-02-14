package com.kfarst.apps.whispertweetnothings.support;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by kfarst on 2/13/17.
 */

public class TweetSubmitButton extends Button implements OnCountChangedListener {

    public TweetSubmitButton(Context context) {
        super(context);
    }

    public TweetSubmitButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TweetSubmitButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void countChanged(int currentCount, boolean hasReachedTheEnd) {
        this.setClickable(!hasReachedTheEnd);
        this.setAlpha(hasReachedTheEnd ? 0.5f : 1.0f);
    }
}
