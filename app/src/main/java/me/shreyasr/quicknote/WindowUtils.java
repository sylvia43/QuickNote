package me.shreyasr.quicknote;

public class WindowUtils {

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
