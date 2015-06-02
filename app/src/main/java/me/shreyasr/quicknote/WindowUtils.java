package me.shreyasr.quicknote;

public class WindowUtils {

    public static int getWidth() {
        return ApplicationWrapper.getInstance().getSharedPrefs()
                .getInt(Constants.WIDTH_PREF,
                        (int) (Constants.DEFAULT_WIDTH*ApplicationWrapper.getInstance().getScreenSize().x));
    }

    public static int getHeight() {
        return ApplicationWrapper.getInstance().getSharedPrefs()
                .getInt(Constants.HEIGHT_PREF,
                        (int) (Constants.DEFAULT_HEIGHT*ApplicationWrapper.getInstance().getScreenSize().y));
    }

    public static int getSmallWidth() {
        return ApplicationWrapper.getInstance().getSharedPrefs()
                .getInt(Constants.SMALL_WIDTH_PREF, Constants.DEFAULT_WIDTH_SMALL);
    }

    public static int getSmallHeight() {
        return ApplicationWrapper.getInstance().getSharedPrefs()
                .getInt(Constants.SMALL_HEIGHT_PREF, Constants.DEFAULT_HEIGHT_SMALL);
    }

    public static int getWindowBarHeight() {
        return getSmallHeight();
    }

    public static int getStatusBarSize() {
        int result = 0;
        int resourceId = ApplicationWrapper.getInstance().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            result = ApplicationWrapper.getInstance().getResources().getDimensionPixelSize(resourceId);
        return result;
    }

    public static int getSize() {
        return 96;
    }
}
