package me.shreyasr.quicknote.window.spinner;

import android.content.Context;
import android.util.AttributeSet;

public class MenuSpinner extends BaseSpinner {

    public MenuSpinner(Context context) {
        this(context, null);
    }

    public MenuSpinner(Context context, int mode) {
        this(context, null, android.R.attr.spinnerStyle, mode);
    }

    public MenuSpinner(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.spinnerStyle);
    }

    public MenuSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0, -1);
    }


    public MenuSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        this(context, attrs, defStyleAttr, 0, mode);
    }

    public MenuSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode) {
        super(context, attrs, defStyleAttr, defStyleRes, mode,
                -1, 0, "drop_down_spinner");
    }
}
