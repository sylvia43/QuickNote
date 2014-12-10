package floating.notepad.quicknote;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;

public class NotepadWindow extends StandOutWindow {

    SharedPreferences prefs;

    public static final String NOTE_CONTENT = "NOTE_CONTENT";
    public static final String POS_X = "POS_X";
    public static final String POS_Y = "POS_Y";
    public boolean collapsed = false;

    public static final int WIDTH = 500;
    public static final int HEIGHT = 500;
    public static final int SMALL_WIDTH = 96;
    public static final int SMALL_HEIGHT = 96;

    @Override
    public String getAppName() {
        return "Floating Notepad";
    }

    @Override
    public int getAppIcon() {
        return R.drawable.ic_launcher;
    }

    @Override
    public void createAndAttachView(final int id, final FrameLayout frame) {
        collapsed = false;

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.notepad_layout, frame, true);
        EditText et = (EditText) frame.findViewById(R.id.editText);

        et.setText(prefs.getString(NOTE_CONTENT, ""));

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                save(frame, id);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void afterTextChanged(Editable s) { }
        });

        ImageButton dock = (ImageButton) frame.findViewById(R.id.dockButton);
        dock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapsed = true;
                save(frame, id);
                frame.findViewById(R.id.editText).setVisibility(View.GONE);
                frame.findViewById(R.id.dockButton).setVisibility(View.GONE);
                frame.findViewById(R.id.settingsButton).setVisibility(View.GONE);
                frame.findViewById(R.id.openButton).setVisibility(View.VISIBLE);
                unfocus(id);
                updateViewLayout(id, getParams(id, null));
            }
        });

        ImageButton undock = (ImageButton) frame.findViewById(R.id.openButton);
        undock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapsed = false;
                save(frame, id);
                frame.findViewById(R.id.editText).setVisibility(View.VISIBLE);
                frame.findViewById(R.id.dockButton).setVisibility(View.VISIBLE);
                frame.findViewById(R.id.settingsButton).setVisibility(View.VISIBLE);
                frame.findViewById(R.id.openButton).setVisibility(View.GONE);
                unfocus(id);
                updateViewLayout(id, getParams(id, null));
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                unfocus(id);
                updateViewLayout(id, getParams(id, null));
            }
        }, 10);
    }

    @Override
    public StandOutLayoutParams getParams(int id, Window window) {
        return new StandOutLayoutParams(id,
                collapsed ? SMALL_WIDTH : WIDTH,
                collapsed ? SMALL_HEIGHT : HEIGHT,
                prefs != null ? prefs.getInt(POS_X, StandOutLayoutParams.BOTTOM) : StandOutLayoutParams.BOTTOM,
                prefs != null ? prefs.getInt(POS_Y, StandOutLayoutParams.LEFT) : StandOutLayoutParams.LEFT);
    }

    @Override
    public int getFlags(int id) {
        return StandOutFlags.FLAG_BODY_MOVE_ENABLE |
               StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE |
               StandOutFlags.FLAG_WINDOW_FOCUS_INDICATOR_DISABLE;
    }

    public void onMove(int id, Window window, View view, MotionEvent event) {
        save(window, id);
    }

    @Override
    public Notification getPersistentNotification(int id) {
        Notification.Builder n = new Notification.Builder(this);
        n.setSmallIcon(getAppIcon());
        n.setContentTitle(getPersistentNotificationTitle(id));
        n.setContentText(getPersistentNotificationMessage(id));
        n.setPriority(Notification.PRIORITY_MIN);
        n.setContentIntent(PendingIntent.getService(this, 0, getPersistentNotificationIntent(id), PendingIntent.FLAG_UPDATE_CURRENT));
        return n.build();
    }

    @Override
    public String getPersistentNotificationTitle(int id) {
        return getAppName();
    }

    @Override
    public String getPersistentNotificationMessage(int id) {
        return "Tap to save & close notepad";
    }

    @Override
    public Intent getPersistentNotificationIntent(int id) {
        return StandOutWindow.getCloseAllIntent(this, getClass());
    }

    public void savePosition(Window window, int id) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(POS_X, getWindow(id).getLayoutParams().x);
        editor.putInt(POS_Y, getWindow(id).getLayoutParams().y);
        editor.apply();
    }

    public void save(FrameLayout frame, int id) {
        SharedPreferences.Editor editor = prefs.edit();
        String text = ((EditText)frame.findViewById(R.id.editText)).getText().toString();
        editor.putString(NOTE_CONTENT, text);
        editor.putInt(POS_X, getWindow(id).getLayoutParams().x);
        editor.putInt(POS_Y, getWindow(id).getLayoutParams().y);
        editor.apply();
    }
}
