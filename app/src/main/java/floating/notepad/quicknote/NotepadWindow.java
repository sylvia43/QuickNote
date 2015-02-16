package floating.notepad.quicknote;

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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;

public class NotepadWindow extends StandOutWindow {

    private SharedPreferences prefs;
    private boolean collapsed = false;

    @Override
    public String getAppName() {
        return ApplicationWrapper.getInstance().getAppName();
    }

    @Override
    public int getAppIcon() {
        return ApplicationWrapper.getInstance().getAppIcon();
    }

    @Override
    public void createAndAttachView(final int id, final FrameLayout frame) {
        prefs = ApplicationWrapper.getInstance().getSharedPrefs();

        prefs.edit().putBoolean(Constants.COLLAPSED, false).apply();


        final Point size = ApplicationWrapper.getInstance().getScreenSize();

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.notepad_layout, frame, true);
        EditText et = (EditText) frame.findViewById(R.id.editText);

        et.setText(prefs.getString(Constants.NOTE_CONTENT, ""));

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                save(frame, id);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void afterTextChanged(Editable s) { }
        });

        final ImageButton dock = (ImageButton) frame.findViewById(R.id.dockButton);
        dock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapsed = true;
                save(frame, id);
                if (prefs.contains(Constants.POS_X))
                    prefs.edit().putInt(Constants.POS_X,
                            prefs.getInt(Constants.POS_X, -1) +
                            prefs.getInt(Constants.WIDTH_PREF,(int)(Constants.DEFAULT_WIDTH*size.x)) -
                            prefs.getInt(Constants.SMALL_WIDTH_PREF, Constants.DEFAULT_WIDTH_SMALL)
                    ).apply();
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
                if (prefs.contains(Constants.POS_X))
                    prefs.edit().putInt(Constants.POS_X,
                            prefs.getInt(Constants.POS_X, -1) +
                                    prefs.getInt(Constants.SMALL_WIDTH_PREF, Constants.DEFAULT_WIDTH_SMALL) - prefs.getInt(Constants.WIDTH_PREF, (int)(Constants.DEFAULT_WIDTH*size.x))).apply();
                frame.findViewById(R.id.editText).setVisibility(View.VISIBLE);
                frame.findViewById(R.id.dockButton).setVisibility(View.VISIBLE);
                frame.findViewById(R.id.settingsButton).setVisibility(View.VISIBLE);
                frame.findViewById(R.id.openButton).setVisibility(View.GONE);
                unfocus(id);
                updateViewLayout(id, getParams(id, null));
            }
        });
        undock.setOnTouchListener(new View.OnTouchListener() {
            int dragging = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        int ex = Math.round(event.getRawX());
                        int ey = Math.round(event.getRawY());
                        int sx = prefs.getInt(Constants.SMALL_WIDTH_PREF, Constants.DEFAULT_WIDTH_SMALL);
                        int sy = prefs.getInt(Constants.SMALL_HEIGHT_PREF, Constants.DEFAULT_HEIGHT_SMALL);
                        prefs.edit().putInt(Constants.POS_X, ex - sx/2).putInt(Constants.POS_Y, ey - sy).apply();
                        updateViewLayout(id, getParams(id, null));
                        dragging++;
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
        });

        ImageButton menu = (ImageButton) frame.findViewById(R.id.settingsButton);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDropDown(id).showAsDropDown(v);
            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (collapsed)
                    dock.callOnClick();
                unfocus(id);
                updateViewLayout(id, getParams(id, null));
            }
        }, 10);
    }

    @Override
    public StandOutLayoutParams getParams(int id, Window window) {
        Point screen = ApplicationWrapper.getInstance().getScreenSize();
        if (prefs != null)
            return new StandOutLayoutParams(id,
                    collapsed ? prefs.getInt(Constants.SMALL_WIDTH_PREF, Constants.DEFAULT_WIDTH_SMALL) : prefs.getInt(Constants.WIDTH_PREF, (int)(Constants.DEFAULT_WIDTH*screen.x)),
                    collapsed ? prefs.getInt(Constants.SMALL_HEIGHT_PREF, Constants.DEFAULT_HEIGHT_SMALL) : prefs.getInt(Constants.HEIGHT_PREF, (int)(Constants.DEFAULT_HEIGHT*screen.y)),
                    prefs.getInt(Constants.POS_X, StandOutLayoutParams.RIGHT), prefs.getInt(Constants.POS_Y, StandOutLayoutParams.TOP));
        return new StandOutLayoutParams(id,
                collapsed ? Constants.DEFAULT_WIDTH_SMALL : (int)(Constants.DEFAULT_WIDTH*screen.x),
                collapsed ? Constants.DEFAULT_HEIGHT_SMALL : (int)(Constants.DEFAULT_HEIGHT*screen.y),
                StandOutLayoutParams.RIGHT, StandOutLayoutParams.TOP);
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

    void save(FrameLayout frame, int id) {
        SharedPreferences.Editor editor = prefs.edit();
        String text = ((EditText)frame.findViewById(R.id.editText)).getText().toString();
        editor.putString(Constants.NOTE_CONTENT, text);
        editor.putInt(Constants.POS_X, getWindow(id).getLayoutParams().x);
        editor.putInt(Constants.POS_Y, getWindow(id).getLayoutParams().y);
        editor.apply();
    }

    public List<DropDownListItem> getDropDownItems(final int id) {
        List<DropDownListItem> items = new ArrayList<DropDownListItem>();
        items.add(new DropDownListItem(R.drawable.ic_action_delete, "Clear", new Runnable() {
            @Override
            public void run() {
                EditText et = (EditText) NotepadWindow.this.getWindow(id).findViewById(R.id.editText);
                et.setText("");
            }
        }));
        items.add(new DropDownListItem(R.drawable.ic_action_copy, "Copy", new Runnable() {
            @Override
            public void run() {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setPrimaryClip(ClipData.newPlainText("Note", prefs.getString(Constants.NOTE_CONTENT, "")));
            }
        }));
        items.add(new DropDownListItem(R.drawable.ic_action_paste, "Paste", new Runnable() {
            @Override
            public void run() {
                try {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    EditText et = (EditText) NotepadWindow.this.getWindow(id).findViewById(R.id.editText);
                    et.setText(prefs.getString(Constants.NOTE_CONTENT, "") + clipboard.getPrimaryClip().getItemAt(0).getText());
                } catch (Exception e) {
                }
            }
        }));
        items.add(new DropDownListItem(R.drawable.ic_action_share, "Share", new Runnable() {
            @Override
            public void run() {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Quick Note");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, prefs.getString(Constants.NOTE_CONTENT, ""));
                startActivity(Intent.createChooser(sharingIntent, "Share Note").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }));
        items.add(new DropDownListItem(R.drawable.ic_action_settings, "Preferences", new Runnable() {
            @Override
            public void run() {
                StandOutWindow.hide(ApplicationWrapper.getInstance(), NotepadWindow.class, 0);
                StandOutWindow.show(ApplicationWrapper.getInstance(), PreferencesPopup.class, 1);
            }
        }));
        items.add(new DropDownListItem(R.drawable.ic_action_about, "About", new Runnable() {
            @Override
            public void run() {
                StandOutWindow.hide(ApplicationWrapper.getInstance(), NotepadWindow.class, 0);
                StandOutWindow.show(ApplicationWrapper.getInstance(), InfoPopup.class, 1);
            }
        }));
        items.add(new DropDownListItem(R.drawable.ic_action_cancel, "Save & Quit", new Runnable() {
            @Override
            public void run() {
                closeAll();
            }
        }));
        return items;
    }
}