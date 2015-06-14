package me.shreyasr.quicknote.window;

import android.content.SharedPreferences;

import me.shreyasr.quicknote.App;
import me.shreyasr.quicknote.Constants;
import me.shreyasr.quicknote.R;

public class WindowUtils {

    private final static SharedPreferences prefs = App.get().getSharedPrefs();

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
        return (int) (prefs.getInt(Constants.SIZE_PREF, App.get().getResources().getInteger(R.integer.size))*getDensity());
    }

    public static int getDefaultWidthPx() {
        return (int) (Constants.DEFAULT_WIDTH_DP * getDensity());
    }

    public static int getDefaultHeightPx() {
        return (int) (Constants.DEFAULT_HEIGHT_DP * getDensity());
    }

    public static int getMinWidthPx() {
        return (int) (Constants.MIN_WIDTH_DP * getDensity());
    }

    public static int getMinHeightPx() {
        return (int) (Constants.MIN_HEIGHT_DP * getDensity());
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
