package floating.notepad.quicknote;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import wei.mark.standout.StandOutWindow;

public class ApplicationWrapper extends Application {

    private static ApplicationWrapper instance;
    public static ApplicationWrapper getInstance() { return instance; }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }


    public String getAppName() {
        return "QuickNote";
    }

    public int getAppIcon() {
        return R.drawable.ic_launcher;
    }

    public String getPersistentNotificationTitle() {
        return getAppName();
    }

    public String getPersistentNotificationMessage() {
        return "Tap to save & close notepad";
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
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }
}