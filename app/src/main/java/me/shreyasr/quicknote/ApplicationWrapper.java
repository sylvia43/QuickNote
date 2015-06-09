package me.shreyasr.quicknote;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;

import wei.mark.standout.StandOutWindow;

public class ApplicationWrapper extends Application {

    private static ApplicationWrapper instance;
    public static ApplicationWrapper getInstance() { return instance; }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        backupManager = new BackupManager(this);
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker("UA-63677547-1"); // Replace with actual tracker/property Id
        tracker.setScreenName("QuickNote Tracker");
        tracker.enableExceptionReporting(true);
    }

    Tracker tracker = null;
    GoogleAnalytics analytics = null;
    public Tracker getTracker() { return tracker; }

    public static void track(String category, String action) {
        instance.tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .build());
    }

    BackupManager backupManager = null;
    public BackupManager getBackupManager() { return backupManager; }

    public String getAppName() {
        return getString(R.string.app_name);
    }

    public int getAppIcon() {
        return R.drawable.ic_launcher;
    }

    public String getPersistentNotificationTitle() {
        return getAppName();
    }

    public String getPersistentNotificationMessage() {
        return getString(R.string.saveAndClose);
    }

    public Intent getPersistentNotificationIntent() {
        return StandOutWindow.getCloseAllIntent(this, NotepadWindow.class);
    }

    public Notification getPersistentNotification() {
        Notification.Builder n = new Notification.Builder(this);
        n.setSmallIcon(getAppIcon());
        n.setContentTitle(getPersistentNotificationTitle());
        n.setContentText(getPersistentNotificationMessage());
        n.setPriority(Notification.PRIORITY_MIN);
        n.setContentIntent(PendingIntent.getService(this, 0, getPersistentNotificationIntent(), PendingIntent.FLAG_UPDATE_CURRENT));
        return n.build();
    }

    public SharedPreferences getSharedPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    public Point getScreenSize() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }
}