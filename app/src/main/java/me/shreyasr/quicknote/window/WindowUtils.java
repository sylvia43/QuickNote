package me.shreyasr.quicknote.window;

import android.content.SharedPreferences;
import android.util.Log;

import me.shreyasr.quicknote.App;
import me.shreyasr.quicknote.Constants;
import me.shreyasr.quicknote.R;

public class WindowUtils {

    private static final String TAG = WindowUtils.class.getSimpleName();

    private final static SharedPreferences prefs;

    static { // Allows the Android Studio preview to function
        SharedPreferences temp = null;
        try {
            temp = App.get().getSharedPrefs();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        prefs = temp;
    }

    public static int getXPx() {
        return prefs.getInt(Constants.POS_X, 0);
    }

    public static int getYPx() {
        return prefs.getInt(Constants.POS_Y, 0);
    }

    public static void setXPx(int val) {
        prefs.edit().putInt(Constants.POS_X, val).apply();
    }

    public static void setYPx(int val) {
        prefs.edit().putInt(Constants.POS_Y, val).apply();
    }

    public static int getWidthPx() {
        if (prefs == null) return (int) Constants.DEFAULT_WIDTH_DP;
        return prefs.getInt(Constants.WIDTH_PREF,
                (int) (Constants.DEFAULT_WIDTH_DP * getDensity()));
    }

    public static int getHeightPx() {
        return prefs.getInt(Constants.HEIGHT_PREF,
                (int) (Constants.DEFAULT_HEIGHT_DP * getDensity()));
    }

    public static void setWidthPx(int val) {
        prefs.edit().putInt(Constants.WIDTH_PREF, val).apply();
    }

    public static void setHeightPx(int val) {
        prefs.edit().putInt(Constants.HEIGHT_PREF, val).apply();
    }

    public static int getSizePx() {
        if (prefs == null) return 48;
        return (int) (prefs.getInt(Constants.SIZE_PREF, App.get().getResources().getInteger(R.integer.size))*getDensity());
    }

    public static int getDefaultWidthPx() {
        return (int) (Constants.DEFAULT_WIDTH_DP * getDensity());
    }

    public static int getDefaultHeightPx() {
        return (int) (Constants.DEFAULT_HEIGHT_DP * getDensity());
    }

    public static int getMinWidthPx() {
        return getSizePx()*4;
    }

    public static int getMinHeightPx() {
        return getSizePx()*2;
    }

    public static float getDensity() {
        return App.get().getResources().getDisplayMetrics().density;
    }

    public static int dpToPx(int dp) {
        return (int) (dp*getDensity());
    }

    public static void reset() {
        setWidthPx(getDefaultWidthPx());
        setHeightPx(getDefaultHeightPx());
    }

    public static int getStatusBarSize() {
        int result = 0;
        int resourceId = App.get().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            result = App.get().getResources().getDimensionPixelSize(resourceId);
        return result;
    }
}
