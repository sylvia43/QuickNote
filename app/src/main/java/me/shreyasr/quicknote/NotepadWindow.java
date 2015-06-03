package me.shreyasr.quicknote;

import android.app.Notification;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;

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
        int xOff;
        private View view;

        DragMoveTouchListener(int id) {
            this(id, null);
        }

        /*
         * View is for a Spinner or such object, which highlights on down
         * and only dehighlights when it's activated on up.
         */
        DragMoveTouchListener(int id, View view) {
            this.id = id;
            this.view = view;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (prefs.getBoolean(Constants.LOCK, false))
                return false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sx = Math.round(event.getRawX());
                    sy = Math.round(event.getRawY());
                    xOff = sx - WindowUtils.getXPx();
                    if (view == null)
                        return false;
                    else
                        return true;
                case MotionEvent.ACTION_MOVE:
                    int ex = Math.round(event.getRawX());
                    int ey = Math.round(event.getRawY());

                    dragging++;
                    if ((ex - sx) * (ex - sx) + (ey - sy) * (ey - sy) < 20 * 20)
                        return false;

                    WindowUtils.setXPx(ex-xOff);
                    WindowUtils.setYPx(ey-WindowUtils.getSizePx());

                    updateViewLayout(id, getParams(id, null));
                    return true;
                case MotionEvent.ACTION_UP:
                    if (dragging > 5) {
                        dragging = 0;
                        return true;
                    }
                    if (view != null) {
                        view.performClick();
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

        WindowUtils.setXPx(WindowUtils.getXPx() + WindowUtils.getWidthPx() - WindowUtils.getSizePx());

        notepadFrame.findViewById(R.id.editText).setVisibility(View.GONE);
        notepadFrame.findViewById(R.id.dockButton).setVisibility(View.GONE);
        notepadFrame.findViewById(R.id.settingsButton).setVisibility(View.GONE);
        notepadFrame.findViewById(R.id.noteSelectionSpinner).setVisibility(View.GONE);
        notepadFrame.findViewById(R.id.undockButton).setVisibility(View.VISIBLE);
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
        notepadFrame.findViewById(R.id.noteSelectionSpinner).setVisibility(View.VISIBLE);
        notepadFrame.findViewById(R.id.undockButton).setVisibility(View.GONE);
        unfocus(id);
        updateViewLayout(id, getParams(id, null));
    }

    @Override
    public void createAndAttachView(final int id, final FrameLayout frame) {
        ApplicationWrapper.track("window", "open");
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
                ApplicationWrapper.track("window", "dock");
            }
        });
        dock.setOnTouchListener(new DragMoveTouchListener(id, WindowUtils.getWidthPx() - WindowUtils.getSizePx()));

        ImageButton undock = (ImageButton) frame.findViewById(R.id.undockButton);
        undock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undock(frame, id);
                ApplicationWrapper.track("window", "undock");
            }
        });
        undock.setOnTouchListener(new DragMoveTouchListener(id));

        ImageButton menu = (ImageButton) frame.findViewById(R.id.settingsButton);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText.getWindowToken(), 0);
                getDropDown(id).showAsDropDown(v, 0, -2);
                ApplicationWrapper.track("window", "menu open");
            }
        });
        menu.setOnTouchListener(new DragMoveTouchListener(id));

        final Spinner spinner = (Spinner) frame.findViewById(R.id.noteSelectionSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{"ayyyy", "lmao"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnTouchListener(new DragMoveTouchListener(id, spinner));

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
        return new StandOutLayoutParams(id,
                collapsed ? WindowUtils.getSizePx() : WindowUtils.getWidthPx(),
                collapsed ? WindowUtils.getSizePx() : WindowUtils.getHeightPx(),
                WindowUtils.getXPx(), WindowUtils.getYPx());
    }

    @Override
    public int getFlags(int id) {
        int flags = StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE
                | StandOutFlags.FLAG_WINDOW_FOCUS_INDICATOR_DISABLE;
        if (prefs == null || !prefs.getBoolean(Constants.LOCK, false))
            flags |= StandOutFlags.FLAG_BODY_MOVE_ENABLE;
        return flags;
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
        String text = ((EditText)editText.findViewById(R.id.editText)).getText().toString();
        prefs.edit().putString(Constants.NOTE_CONTENT, text).apply();
        if (getWindow(id) != null) {
            WindowUtils.setXPx(getWindow(id).getLayoutParams().x);
            WindowUtils.setYPx(getWindow(id).getLayoutParams().y);
        }
        ApplicationWrapper.getInstance().getBackupManager().dataChanged();
    }

    public List<DropDownListItem> getDropDownItems(final int id) {
        List<DropDownListItem> items = new ArrayList<DropDownListItem>();
        items.add(new DropDownListItem(R.drawable.ic_action_delete, getString(R.string.menu_clear), new Runnable() {
            @Override
            public void run() {
                ApplicationWrapper.track("menu", "clear");
                EditText et = (EditText) NotepadWindow.this.getWindow(id).findViewById(R.id.editText);
                et.setText("");
            }
        }));
        items.add(new DropDownListItem(R.drawable.ic_action_copy, getString(R.string.menu_copy), new Runnable() {
            @Override
            public void run() {
                ApplicationWrapper.track("menu", "copy");
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setPrimaryClip(ClipData.newPlainText("Note", prefs.getString(Constants.NOTE_CONTENT, "")));
            }
        }));
        items.add(new DropDownListItem(R.drawable.ic_action_paste, getString(R.string.menu_paste), new Runnable() {
            @Override
            public void run() {
                ApplicationWrapper.track("menu", "paste");
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
                ApplicationWrapper.track("menu", "share");
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
                ApplicationWrapper.track("menu", "prefs");
                StandOutWindow.hide(ApplicationWrapper.getInstance(), NotepadWindow.class, 0);
                StandOutWindow.show(ApplicationWrapper.getInstance(), PreferencesPopup.class, 1);
            }
        }));
        items.add(new DropDownListItem(R.drawable.ic_action_about, getString(R.string.menu_about), new Runnable() {
            @Override
            public void run() {
                ApplicationWrapper.track("menu", "about");
                StandOutWindow.hide(ApplicationWrapper.getInstance(), NotepadWindow.class, 0);
                StandOutWindow.show(ApplicationWrapper.getInstance(), InfoPopup.class, 1);
            }
        }));
        items.add(new DropDownListItem(R.drawable.ic_action_cancel, getString(R.string.menu_save_quit), new Runnable() {
            @Override
            public void run() {
                ApplicationWrapper.track("menu", "quit");
                closeAll();
            }
        }));
        return items;
    }

    int[] elementIds = new int[] { R.id.editText, R.id.titlebar, R.id.undockButton, R.id.settingsButton, R.id.dockButton };

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
        ApplicationWrapper.track("window", "close");
        if (collapsed)
            undock(notepadView, 0);
        return false;
    }
}