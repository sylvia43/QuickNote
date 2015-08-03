package me.shreyasr.quicknote.window.spinner;

import android.content.Context;
import android.util.AttributeSet;

import me.shreyasr.quicknote.window.WindowUtils;

public class DropdownSpinner extends BaseSpinner {

    public DropdownSpinner(Context context) {
        this(context, null);
    }

    public DropdownSpinner(Context context, int mode) {
        this(context, null, android.R.attr.spinnerStyle, mode);
    }

    public DropdownSpinner(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.spinnerStyle);
    }

    public DropdownSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0, -1);
    }


    public DropdownSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        this(context, attrs, defStyleAttr, 0, mode);
    }

    public DropdownSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode) {
        super(context, attrs, defStyleAttr, defStyleRes, mode,
                WindowUtils.getSizePx()*3, 0, "drop_down_spinner");
    }
}
