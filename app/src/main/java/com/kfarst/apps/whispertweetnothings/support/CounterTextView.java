package com.kfarst.apps.whispertweetnothings.support;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.Observable;
import android.databinding.ObservableInt;
import android.util.AttributeSet;
import android.widget.TextView;
import com.codepath.apps.whispertweetnothings.R;

import java.util.ArrayList;

/**
 * Created by kfarst on 2/6/17.
 */

public class CounterTextView extends TextView implements OnCountChangedListener {
    @Override
    public void countChanged(int currentCount, boolean hasReachedTheEnd) {
        this.setText(""+currentCount);
        this.setTextColor(hasReachedTheEnd ? mInvalidTextColor : mValidTextColor);
    }

    private TypedArray attributes;
    private int mInvalidTextColor;
    private int mValidTextColor;

    public CounterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        attributes = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CounterTextView,
                0, 0);

        try {
            mValidTextColor = attributes.getInt(R.styleable.CounterTextView_validTextColor, android.R.color.darker_gray);
            mInvalidTextColor = attributes.getInt(R.styleable.CounterTextView_invalidTextColor, android.R.color.holo_red_dark);
            this.setTextColor(mValidTextColor);
        } finally {
            attributes.recycle();
        }
    }
}
