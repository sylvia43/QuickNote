package me.shreyasr.quicknote.window.spinner;

import android.content.Context;
import android.util.AttributeSet;

import me.shreyasr.quicknote.window.WindowUtils;

public class NoteSwitchSpinner extends BaseSpinner {

    public NoteSwitchSpinner(Context context) {
        this(context, null);
    }

    public NoteSwitchSpinner(Context context, int mode) {
        this(context, null, android.R.attr.spinnerStyle, mode);
    }

    public NoteSwitchSpinner(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.spinnerStyle);
    }

    public NoteSwitchSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0, -1);
    }


    public NoteSwitchSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        this(context, attrs, defStyleAttr, 0, mode);
    }

    public NoteSwitchSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode) {
        super(context, attrs, defStyleAttr, defStyleRes, mode,
                WindowUtils.getWidthPx(), -WindowUtils.getSizePx(), "note_switch_spinner");
    }
}
