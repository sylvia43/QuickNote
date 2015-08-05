package me.shreyasr.quicknote.window.spinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.shreyasr.quicknote.App;
import me.shreyasr.quicknote.R;
import me.shreyasr.quicknote.notepad.NotepadUtils;
import me.shreyasr.quicknote.window.NotepadWindow;

public class NoteSwitchSpinnerAdapter extends BaseAdapter {

    private final List<String> noteTitles;
    public NoteSwitchSpinner.DropdownPopup dropdownPopup = null;

    public NoteSwitchSpinnerAdapter() {
        noteTitles = Collections.synchronizedList(new ArrayList<>(Arrays.asList(NotepadUtils.getNoteTitles())));
    }

    @Override
    public int getCount() {
        return noteTitles.size();
    }

    @Override
    public String getItem(int position) {
        return noteTitles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getPosition(String title) {
        return noteTitles.indexOf(title);
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(App.get()).inflate(R.layout.note_switch_spinner_preview, parent, false);
        }
        ((TextView) view.findViewById(R.id.notepad_spinner_item_text)).setText(noteTitles.get(position));
        return view;
    }

    @Override
    public View getDropDownView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(App.get()).inflate(R.layout.note_switch_spinner_item, parent, false);
        }

        ((TextView) view.findViewById(R.id.spinner_item_content)).setText(noteTitles.get(position));

        view.findViewById(R.id.spinner_item_edit).setOnClickListener(new View.OnClickListener() {
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
                input.append(NotepadUtils.getNoteTitle(position));
                MaterialDialog dialog = new MaterialDialog.Builder(App.get())
                        .title("Edit Note Title")
                        .customView(input, false)
                        .positiveText(R.string.dialog_positive)
                        .negativeText(R.string.dialog_negative)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                String newTitle = input.getText().toString();
                                if (NotepadUtils.hasNoteTitle(newTitle))
                                    return;
                                String originalTitle = getItem(position);
                                noteTitles.set(position, newTitle);
                                NotepadUtils.editNoteTitle(originalTitle, newTitle);
                                notifyDataSetChanged();
                                App.track("notes", "edit");
                                closeDropdown();
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                App.track("notes", "edit_cancel");
                                closeDropdown();
                            }
                        })
                        .build();
                android.view.Window window = dialog.getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                params.token = NotepadWindow.instance.notepadView.getWindowToken();
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
                window.setAttributes(params);
                dialog.show();

                closeDropdown();
            }
        });

        view.findViewById(R.id.spinner_item_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.track("notes", "delete");
                String toRemove = getItem(position);
                if (getCount() > 1)
                    noteTitles.remove(position);
                NotepadUtils.removeNoteTitle(toRemove);
                notifyDataSetChanged();
                closeDropdown();
            }
        });

        return view;
    }

    public void append(String newNote) {
        noteTitles.add(newNote);
        notifyDataSetChanged();
    }

    public void closeDropdown() {
        if (dropdownPopup != null) dropdownPopup.dismiss();
    }
}