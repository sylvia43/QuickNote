package me.shreyasr.quicknote.window.spinner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.shreyasr.quicknote.ApplicationWrapper;
import me.shreyasr.quicknote.R;
import me.shreyasr.quicknote.notepad.NotepadUtils;
import me.shreyasr.quicknote.window.NotepadWindow;

public class NoteSwitchSpinnerAdapter extends BaseAdapter {

    private final List<String> noteTitles;
    public NoteSwitchSpinner.DropdownPopup dialog = null;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(ApplicationWrapper.getInstance()).inflate(android.R.layout.simple_spinner_item, parent, false);
            ((TextView) convertView).setText(noteTitles.get(position));
        }
        return convertView;
    }

    @Override
    public View getDropDownView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(ApplicationWrapper.getInstance()).inflate(R.layout.spinner_item, parent, false);

        ((TextView) convertView.findViewById(R.id.spinner_item_content)).setText(noteTitles.get(position));

        convertView.findViewById(R.id.spinner_item_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(ApplicationWrapper.getInstance());
                input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        input.post(new Runnable() {
                            @Override
                            public void run() {
                                InputMethodManager inputMethodManager = (InputMethodManager) ApplicationWrapper.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                            }
                        });
                    }
                });
                input.requestFocus();
                input.append(NotepadUtils.getNoteTitle(position));
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        new ContextThemeWrapper(ApplicationWrapper.getInstance(), android.R.style.Theme_DeviceDefault_Dialog))
                        .setTitle("Edit Note Title")
                        .setView(input)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newTitle = input.getText().toString();
                                if (NotepadUtils.hasNoteTitle(newTitle))
                                    return;
                                String originalTitle = getItem(position);
                                noteTitles.set(position, newTitle);
                                NotepadUtils.editNoteTitle(originalTitle, newTitle);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null);
                AlertDialog alert = builder.create();
                android.view.Window window = alert.getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                params.token = NotepadWindow.instance.notepadView.getWindowToken();
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
                window.setAttributes(params);
                alert.show();

                if (dialog != null) dialog.dismiss();
            }
        });

        convertView.findViewById(R.id.spinner_item_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toRemove = getItem(position);
                if (getCount() > 1)
                    noteTitles.remove(position);
                NotepadUtils.removeNoteTitle(toRemove);
                notifyDataSetChanged();
                if (dialog != null) dialog.dismiss();
            }
        });

        return convertView;
    }

    public void append(String newNote) {
        noteTitles.add(newNote);
        notifyDataSetChanged();
    }
}