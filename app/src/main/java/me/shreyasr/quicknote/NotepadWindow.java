package me.shreyasr.quicknote;

import android.app.Notification;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;

public class NotepadWindow extends StandOutWindow {

    class DragMoveTouchListener implements View.OnTouchListener {

        int dragging = 0;
        int sx = 0;
        int sy = 0;
        final int id;
        final int xOff;

        DragMoveTouchListener(int id, int xOff) {
            this.id = id;
            this.xOff = xOff;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (prefs.getBoolean(Constants.LOCK, false))
                return false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sx = Math.round(event.getRawX());
                    sy = Math.round(event.getRawY());
                    return false;
                case MotionEvent.ACTION_MOVE:
                    int ex = Math.round(event.getRawX());
                    int ey = Math.round(event.getRawY());

                    dragging++;
                    if ((ex - sx) * (ex - sx) + (ey - sy) * (ey - sy) < 20 * 20)
                        return false;

                    int size = WindowUtils.getSizePx();
                    WindowUtils.setXPx(ex - xOff - size/2);
                    WindowUtils.setYPx(ey-size);

                    updateViewLayout(id, getParams(id, null));
                    return true;
                case MotionEvent.ACTION_UP:
                    if (dragging > 5) {
                        dragging = 0;
                        return true;
                    }
                default:
                    return false;
            }
        }
    }

    public static NotepadWindow instance;
    private SharedPreferences prefs;
    boolean collapsed = false;
    public FrameLayout notepadView;
    Animation focusAnim = new AlphaAnimation(0, 1);

    @Override
    public String getAppName() {
        return ApplicationWrapper.getInstance().getAppName();
    }

    @Override
    public int getAppIcon() {
        return ApplicationWrapper.getInstance().getAppIcon();
    }

    public void collapse(View notepadFrame, final int id) {
        collapsed = true;
        focusAnim.cancel();
        save(notepadFrame, id);

        WindowUtils.setXPx(WindowUtils.getXPx()+WindowUtils.getWidthPx()-WindowUtils.getSizePx());

        notepadFrame.findViewById(R.id.editText).setVisibility(View.GONE);
        notepadFrame.findViewById(R.id.dockButton).setVisibility(View.GONE);
        notepadFrame.findViewById(R.id.settingsButton).setVisibility(View.GONE);
        notepadFrame.findViewById(R.id.openButton).setVisibility(View.VISIBLE);
        unfocus(id);
        updateViewLayout(id, getParams(id, null));
    }

    public void undock(View notepadFrame, int id) {
        collapsed = false;
        focusAnim.cancel();
        save(notepadFrame, id);

        WindowUtils.setXPx(WindowUtils.getXPx() - WindowUtils.getWidthPx() + WindowUtils.getSizePx());

        notepadFrame.findViewById(R.id.editText).setVisibility(View.VISIBLE);
        notepadFrame.findViewById(R.id.dockButton).setVisibility(View.VISIBLE);
        notepadFrame.findViewById(R.id.settingsButton).setVisibility(View.VISIBLE);
        notepadFrame.findViewById(R.id.openButton).setVisibility(View.GONE);
        unfocus(id);
        updateViewLayout(id, getParams(id, null));
    }

    @Override
    public void createAndAttachView(final int id, final FrameLayout frame) {
        notepadView = frame;
        instance = this;
        prefs = ApplicationWrapper.getInstance().getSharedPrefs();

        prefs.edit().putBoolean(Constants.COLLAPSED, false).apply();

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.notepad_layout, frame, true);
        final EditText editText = (EditText) frame.findViewById(R.id.editText);

        editText.setText(prefs.getString(Constants.NOTE_CONTENT, ""));

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                save(frame, id);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final ImageButton dock = (ImageButton) frame.findViewById(R.id.dockButton);
        dock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapse(frame, id);
            }
        });
        dock.setOnTouchListener(new DragMoveTouchListener(id, WindowUtils.getWidthPx()-WindowUtils.getSizePx()));

        ImageButton undock = (ImageButton) frame.findViewById(R.id.openButton);
        undock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undock(frame, id);
            }
        });
        undock.setOnTouchListener(new DragMoveTouchListener(id, 0));

        ImageButton menu = (ImageButton) frame.findViewById(R.id.settingsButton);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText.getWindowToken(), 0);
                getDropDown(id).showAsDropDown(v, 0, -2);
            }
        });
        menu.setOnTouchListener(new DragMoveTouchListener(id, 0));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                unfocus(id);
                updateViewLayout(id, getParams(id, null));
                save(editText, id);
            }
        }, 10);
    }

    @Override
    public StandOutLayoutParams getParams(int id, Window window) {
        Point screen = ApplicationWrapper.getInstance().getScreenSize();
        if (prefs != null)
            return new StandOutLayoutParams(id,
                    collapsed ? WindowUtils.getSizePx() : WindowUtils.getWidthPx(),
                    collapsed ? WindowUtils.getSizePx() : WindowUtils.getHeightPx(),
                    WindowUtils.getXPx(), WindowUtils.getYPx());
        return new StandOutLayoutParams(id,
                collapsed ? Constants.DEFAULT_WIDTH_SMALL : (int)(Constants.DEFAULT_WIDTH*screen.x),
                collapsed ? Constants.DEFAULT_HEIGHT_SMALL : (int)(Constants.DEFAULT_HEIGHT*screen.y),
                StandOutLayoutParams.RIGHT, StandOutLayoutParams.TOP);
    }

    @Override
    public int getFlags(int id) {
        if (prefs != null && prefs.getBoolean(Constants.LOCK, false))
            return StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE
                    | StandOutFlags.FLAG_WINDOW_FOCUS_INDICATOR_DISABLE;
        return StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE
                | StandOutFlags.FLAG_WINDOW_FOCUS_INDICATOR_DISABLE
                | StandOutFlags.FLAG_BODY_MOVE_ENABLE;
    }

    public void onMove(int id, Window window, View view, MotionEvent event) {
        save(window, id);
    }

    @Override
    public Notification getPersistentNotification(int id) {
        return ApplicationWrapper.getInstance().getPersistentNotification();
    }

    @Override
    public String getPersistentNotificationTitle(int id) {
        return ApplicationWrapper.getInstance().getPersistentNotificationTitle();
    }

    @Override
    public String getPersistentNotificationMessage(int id) {
        return ApplicationWrapper.getInstance().getPersistentNotificationMessage();
    }

    @Override
    public Intent getPersistentNotificationIntent(int id) {
        return ApplicationWrapper.getInstance().getPersistentNotificationIntent();
    }

    void save(View editText, int id) {
        SharedPreferences.Editor editor = prefs.edit();
        String text = ((EditText)editText.findViewById(R.id.editText)).getText().toString();
        editor.putString(Constants.NOTE_CONTENT, text);
        if (getWindow(id) != null) {
            editor.putInt(Constants.POS_X, getWindow(id).getLayoutParams().x);
            editor.putInt(Constants.POS_Y, getWindow(id).getLayoutParams().y);
        }
        editor.apply();
        ApplicationWrapper.getInstance().getBackupManager().dataChanged();
    }

    public List<DropDownListItem> getDropDownItems(final int id) {
        List<DropDownListItem> items = new ArrayList<DropDownListItem>();
        items.add(new DropDownListItem(R.drawable.ic_action_delete, getString(R.string.menu_clear), new Runnable() {
            @Override
            public void run() {
                EditText et = (EditText) NotepadWindow.this.getWindow(id).findViewById(R.id.editText);
                et.setText("");
            }
        }));
        items.add(new DropDownListItem(R.drawable.ic_action_copy, getString(R.string.menu_copy), new Runnable() {
            @Override
            public void run() {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setPrimaryClip(ClipData.newPlainText("Note", prefs.getString(Constants.NOTE_CONTENT, "")));
            }
        }));
        items.add(new DropDownListItem(R.drawable.ic_action_paste, getString(R.string.menu_paste), new Runnable() {
            @Override
            public void run() {
                try {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    EditText et = (EditText) NotepadWindow.this.getWindow(id).findViewById(R.id.editText);
                    et.setText(prefs.getString(Constants.NOTE_CONTENT, "") + clipboard.getPrimaryClip().getItemAt(0).getText());
                } catch (Exception ignored) { }
            }
        }));
        items.add(new DropDownListItem(R.drawable.ic_action_share, getString(R.string.menu_share), new Runnable() {
            @Override
            public void run() {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Quick Note");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, prefs.getString(Constants.NOTE_CONTENT, ""));
                NotepadWindow.this.collapse(NotepadWindow.instance.notepadView, id);
                startActivity(Intent.createChooser(sharingIntent, "Share Note").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }));
        items.add(new DropDownListItem(R.drawable.ic_action_settings, getString(R.string.menu_preferences), new Runnable() {
            @Override
            public void run() {
                StandOutWindow.hide(ApplicationWrapper.getInstance(), NotepadWindow.class, 0);
                StandOutWindow.show(ApplicationWrapper.getInstance(), PreferencesPopup.class, 1);
            }
        }));
        items.add(new DropDownListItem(R.drawable.ic_action_about, getString(R.string.menu_about), new Runnable() {
            @Override
            public void run() {
                StandOutWindow.hide(ApplicationWrapper.getInstance(), NotepadWindow.class, 0);
                StandOutWindow.show(ApplicationWrapper.getInstance(), InfoPopup.class, 1);
            }
        }));
        items.add(new DropDownListItem(R.drawable.ic_action_cancel, getString(R.string.menu_save_quit), new Runnable() {
            @Override
            public void run() {
                closeAll();
            }
        }));
        return items;
    }

    int[] elementIds = new int[] { R.id.editText, R.id.titlebar, R.id.openButton, R.id.settingsButton, R.id.dockButton };

    @Override
    public boolean onFocusChange(int id, Window window, boolean focus) {
        float opacity = prefs.getInt(Constants.OPACITY, Constants.DEFAULT_OPACITY)/255f;

        if (focus)
            focusAnim = new AlphaAnimation(opacity, 1);
        else
            focusAnim = new AlphaAnimation(1, opacity);
        focusAnim.setInterpolator(new LinearInterpolator());
        focusAnim.setDuration(100);
        focusAnim.setFillAfter(true);

        for (int elementId : elementIds) {
            View view = window.findViewById(elementId);
            if (view.getVisibility() == View.VISIBLE)
                view.startAnimation(focusAnim);
            else
                view.clearAnimation();
        }
        return false;
    }

    @Override
    public boolean onClose(int id, Window window) {
        if (collapsed)
            undock(notepadView, 0);
        return false;
    }
}