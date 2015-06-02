package me.shreyasr.quicknote;

import android.content.SharedPreferences;

public class WindowUtils {

    private final static SharedPreferences prefs = ApplicationWrapper.getInstance().getSharedPrefs();

    // prefs.getInt(Constants.POS_X, StandOutLayoutParams.RIGHT), prefs.getInt(Constants.POS_Y, StandOutLayoutParams.TOP));
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
        return ApplicationWrapper.getInstance().getSharedPrefs()
                .getInt(Constants.WIDTH_PREF,
                        (int) (Constants.DEFAULT_WIDTH*ApplicationWrapper.getInstance().getScreenSize().x));
    }

    public static int getHeightPx() {
        return ApplicationWrapper.getInstance().getSharedPrefs()
                .getInt(Constants.HEIGHT_PREF,
                        (int) (Constants.DEFAULT_HEIGHT*ApplicationWrapper.getInstance().getScreenSize().y));
    }

    public static int getSizePx() {
        return ApplicationWrapper.getInstance().getSharedPrefs()
                .getInt(Constants.SMALL_HEIGHT_PREF, Constants.DEFAULT_HEIGHT_SMALL);
    }

    public static int getStatusBarSize() {
        int result = 0;
        int resourceId = ApplicationWrapper.getInstance().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            result = ApplicationWrapper.getInstance().getResources().getDimensionPixelSize(resourceId);
        return result;
    }

    private static float density = -1;
    public static float getDensity() {
        if (density == -1)
            density = ApplicationWrapper.getInstance().getResources().getDisplayMetrics().density;
        return density;
    }
}
