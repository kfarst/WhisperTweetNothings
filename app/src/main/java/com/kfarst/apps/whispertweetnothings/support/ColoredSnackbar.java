package com.kfarst.apps.whispertweetnothings.support;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

/**
 * Created by kfarst on 8/2/16.
 */
public class ColoredSnackBar {

    private static final int red = android.R.color.holo_red_dark;
    private static final int green = android.R.color.holo_green_dark;
    private static final int blue = android.R.color.holo_blue_dark;
    private static final int orange = android.R.color.holo_orange_dark;
    private static final int white = android.R.color.white;

    private static View getSnackBarLayout(Snackbar snackbar) {
        if (snackbar != null) {
            return snackbar.getView();
        }
        return null;
    }

    private static Snackbar colorSnackBar(Snackbar snackbar, int colorId) {
        View snackBarView = getSnackBarLayout(snackbar);
        if (snackBarView != null) {
            snackBarView.setBackgroundColor(getColorFromResource(colorId, snackBarView.getContext()));
            snackbar.setActionTextColor(getColorFromResource(white, snackBarView.getContext()));
        }

        return snackbar;
    }

    public static Snackbar info(Snackbar snackbar) {
        return colorSnackBar(snackbar, blue);
    }

    public static Snackbar warning(Snackbar snackbar) {
        return colorSnackBar(snackbar, orange);
    }

    public static Snackbar alert(Snackbar snackbar) {
        return colorSnackBar(snackbar, red);
    }

    public static Snackbar confirm(Snackbar snackbar) {
        return colorSnackBar(snackbar, green);
    }

    private static int getColorFromResource(int color, Context context) {
        return ContextCompat.getColor(context, color);
    }

}
