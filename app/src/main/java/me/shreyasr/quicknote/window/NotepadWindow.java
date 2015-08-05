package me.shreyasr.quicknote.window;

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
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import me.shreyasr.quicknote.App;
import me.shreyasr.quicknote.Constants;
import me.shreyasr.quicknote.R;
import me.shreyasr.quicknote.notepad.NotepadUtils;
import me.shreyasr.quicknote.window.spinner.BaseSpinner;
import me.shreyasr.quicknote.window.spinner.MenuSpinner;
import me.shreyasr.quicknote.window.spinner.NoteSwitchSpinner;
import me.shreyasr.quicknote.window.spinner.NoteSwitchSpinnerAdapter;
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
    public NoteSwitchSpinner spinner;
    Animation focusAnim = new AlphaAnimation(0, 1);

    @Override
    public String getAppName() {
        return App.get().getAppName();
    }

    @Override
    public int getAppIcon() {
        return App.get().getAppIcon();
    }

    public void collapse(View notepadFrame, final int id) {
        collapsed = true;
        focusAnim.cancel();
        save(notepadFrame, id);

        WindowUtils.setXPx(WindowUtils.getXPx() + WindowUtils.getWidthPx() - WindowUtils.getSizePx());

        notepadFrame.findViewById(R.id.notepadContent).setVisibility(View.GONE);
        notepadFrame.findViewById(R.id.dockButton).setVisibility(View.GONE);
        notepadFrame.findViewById(R.id.settingsSpinner).setVisibility(View.GONE);
        notepadFrame.findViewById(R.id.noteSelectionSpinner).setVisibility(View.GONE);
        notepadFrame.findViewById(R.id.addButton).setVisibility(View.GONE);
        notepadFrame.findViewById(R.id.undockButton).setVisibility(View.VISIBLE);
        unfocus(id);
        updateViewLayout(id, getParams(id, null));
    }

    public void undock(View notepadFrame, int id) {
        collapsed = false;
        focusAnim.cancel();
        save(notepadFrame, id);

        WindowUtils.setXPx(WindowUtils.getXPx() - WindowUtils.getWidthPx() + WindowUtils.getSizePx());

        notepadFrame.findViewById(R.id.notepadContent).setVisibility(View.VISIBLE);
        notepadFrame.findViewById(R.id.dockButton).setVisibility(View.VISIBLE);
        notepadFrame.findViewById(R.id.settingsSpinner).setVisibility(View.VISIBLE);
        notepadFrame.findViewById(R.id.noteSelectionSpinner).setVisibility(View.VISIBLE);
        notepadFrame.findViewById(R.id.addButton).setVisibility(View.VISIBLE);
        notepadFrame.findViewById(R.id.undockButton).setVisibility(View.GONE);
        unfocus(id);
        updateViewLayout(id, getParams(id, null));
    }

    @Override
    public void createAndAttachView(final int id, final FrameLayout frame) {
        App.track("window", "open");
        notepadView = frame;
        instance = this;
        prefs = App.get().getSharedPrefs();

        prefs.edit().putBoolean(Constants.COLLAPSED, false).apply();

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.notepad_layout, frame, true);
        final EditText editText = (EditText) frame.findViewById(R.id.notepadContent);

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

        //region Dock Button
        final ImageButton dock = (ImageButton) frame.findViewById(R.id.dockButton);
        dock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapse(frame, id);
                App.track("window", "dock");
            }
        });
        dock.setOnTouchListener(new DragMoveTouchListener(id));
        //endregion

        //region Undock Button
        ImageButton undock = (ImageButton) frame.findViewById(R.id.undockButton);
        undock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undock(frame, id);
                App.track("window", "undock");
            }
        });
        undock.setOnTouchListener(new DragMoveTouchListener(id));
        //endregion

        //region Menu Button
        MenuSpinner menu = (MenuSpinner) frame.findViewById(R.id.settingsSpinner);
        final MenuAdapter menuAdapter = new MenuAdapter(getDropDownItems(id));

        menu.setAdapter(menuAdapter);
        menu.setCustomOnClickListener(new BaseSpinner.CustomItemClickListener() {
            @Override
            public void onItemClick(BaseSpinner.DropdownPopup popup, int position) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(editText.getWindowToken(), 0);
                menuAdapter.getItem(position).action.run();
                popup.dismiss();
            }
        });
        menu.setOnTouchListener(new DragMoveTouchListener(id));
        //endregion

        //region Note Selection Spinner
        spinner = (NoteSwitchSpinner) frame.findViewById(R.id.noteSelectionSpinner);
        final NoteSwitchSpinnerAdapter adapter = new NoteSwitchSpinnerAdapter();

        spinner.setAdapter(adapter);
        spinner.setOnTouchListener(new DragMoveTouchListener(id, spinner));

        spinner.setCustomOnClickListener(new BaseSpinner.CustomItemClickListener() {
            @Override
            public void onItemClick(BaseSpinner.DropdownPopup popup, int position) {
                spinner.setSelection(position);
                String item = adapter.getItem(position);
                NotepadUtils.setCurrentNote(item);
                popup.dismiss();
            }
        });
        spinner.setSelection(adapter.getPosition(NotepadUtils.getCurrentNoteTitle()));

        //endregion

        //region Add Button
        ImageButton add = (ImageButton) frame.findViewById(R.id.addButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(App.get());
                input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        input.post(new Runnable() {
                            @Override
                            public void run() {
                                InputMethodManager inputMethodManager = (InputMethodManager) App.get().getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                            }
                        });
                    }
                });
                input.requestFocus();
                MaterialDialog dialog = new MaterialDialog.Builder(App.get())
                        .title("New Note Title")
                        .customView(input, false)
                        .positiveText(R.string.dialog_positive)
                        .negativeText(R.string.dialog_negative)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                String newNote = input.getText().toString();
                                if (!NotepadUtils.hasNoteTitle(newNote))
                                    adapter.append(newNote);
                                NotepadUtils.setCurrentNote(newNote);
                                NotepadUtils.updateNotepad();
                                App.track("notes", "add");
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                App.track("notes", "cancel");
                            }
                        })
                        .build();
                android.view.Window window = dialog.getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                params.token = NotepadWindow.this.notepadView.getWindowToken();
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
                window.setAttributes(params);
                dialog.show();
            }
        });
        add.setOnTouchListener(new DragMoveTouchListener(id));
        //endregion

        if (NotepadUtils.isFirstRun())
            NotepadUtils.addIntroNote();
        NotepadUtils.updateNotepad();
        adapter.notifyDataSetChanged();

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
        return App.get().getPersistentNotification();
    }

    @Override
    public String getPersistentNotificationTitle(int id) {
        return App.get().getPersistentNotificationTitle();
    }

    @Override
    public String getPersistentNotificationMessage(int id) {
        return App.get().getPersistentNotificationMessage();
    }

    @Override
    public Intent getPersistentNotificationIntent(int id) {
        return App.get().getPersistentNotificationIntent();
    }

    void save(View editText, int id) {
        String text = ((EditText)editText.findViewById(R.id.notepadContent)).getText().toString();
        NotepadUtils.saveContent(text);
        if (getWindow(id) != null) {
            WindowUtils.setXPx(getWindow(id).getLayoutParams().x);
            WindowUtils.setYPx(getWindow(id).getLayoutParams().y);
        }
        App.get().getBackupManager().dataChanged();
    }

    public List<DropDownListItem> getDropDownItems(final int id) {
        List<DropDownListItem> items = new ArrayList<DropDownListItem>();
        items.add(new DropDownListItem(R.drawable.ic_delete_white_48dp, getString(R.string.menu_clear), new Runnable() {
            @Override
            public void run() {
                App.track("menu", "clear");
                EditText et = (EditText) NotepadWindow.this.getWindow(id).findViewById(R.id.notepadContent);
                et.setText("");
            }
        }));
        items.add(new DropDownListItem(R.drawable.ic_content_copy_white_48dp, getString(R.string.menu_copy), new Runnable() {
            @Override
            public void run() {
                App.track("menu", "copy");
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setPrimaryClip(ClipData.newPlainText("Note", NotepadUtils.getCurrentNoteContent()));
            }
        }));
        items.add(new DropDownListItem(R.drawable.ic_content_paste_white_48dp, getString(R.string.menu_paste), new Runnable() {
            @Override
            public void run() {
                App.track("menu", "paste");
                try {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    EditText et = (EditText) NotepadWindow.this.getWindow(id).findViewById(R.id.notepadContent);
                    et.setText(NotepadUtils.getCurrentNoteContent() + clipboard.getPrimaryClip().getItemAt(0).getText());
                } catch (Exception ignored) { }
            }
        }));
        items.add(new DropDownListItem(R.drawable.ic_share_white_48dp, getString(R.string.menu_share), new Runnable() {
            @Override
            public void run() {
                App.track("menu", "share");
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Quick Note");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, NotepadUtils.getCurrentNoteContent());
                NotepadWindow.this.collapse(NotepadWindow.instance.notepadView, id);
                startActivity(Intent.createChooser(sharingIntent, "Share Note").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }));
        items.add(new DropDownListItem(R.drawable.ic_settings_white_48dp, getString(R.string.menu_preferences), new Runnable() {
            @Override
            public void run() {
                App.track("menu", "prefs");
                StandOutWindow.hide(App.get(), NotepadWindow.class, 0);
                StandOutWindow.show(App.get(), PreferencesWindow.class, 1);
            }
        }));
        items.add(new DropDownListItem(R.drawable.ic_info_outline_white_48dp, getString(R.string.menu_about), new Runnable() {
            @Override
            public void run() {
                App.track("menu", "about");
                StandOutWindow.hide(App.get(), NotepadWindow.class, 0);
                StandOutWindow.show(App.get(), InfoWindow.class, 1);
            }
        }));
        items.add(new DropDownListItem(R.drawable.ic_close_white_48dp, getString(R.string.menu_save_quit), new Runnable() {
            @Override
            public void run() {
                App.track("menu", "quit");
                closeAll();
            }
        }));
        return items;
    }

    int[] elementIds = new int[] { R.id.notepadContent, R.id.titlebar, R.id.undockButton, R.id.settingsSpinner, R.id.dockButton };

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
        App.track("window", "close");
        if (collapsed)
            undock(notepadView, 0);
        return false;
    }
}