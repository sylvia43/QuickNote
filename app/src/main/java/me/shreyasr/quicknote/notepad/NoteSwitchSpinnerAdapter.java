package me.shreyasr.quicknote.notepad;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.shreyasr.quicknote.ApplicationWrapper;
import me.shreyasr.quicknote.R;

public class NoteSwitchSpinnerAdapter extends BaseAdapter {

    private final List<String> noteTitles;
     DialogInterface dialog;

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
                if (NotepadUtils.hasNoteTitle("asdf"))
                    return;
                String originalTitle = getItem(position);
                noteTitles.set(position, "asdf");
                NotepadUtils.editNoteTitle(originalTitle, "asdf");
                if (dialog != null) dialog.dismiss();
            }
        });

        convertView.findViewById(R.id.spinner_item_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toRemove = getItem(position);
                noteTitles.remove(position);
                NotepadUtils.removeNoteTitle(toRemove);
                if (dialog != null) dialog.dismiss();
            }
        });

        return convertView;
    }

    public void insert(String newTitle, int pos) {
        noteTitles.add(pos, newTitle);
        notifyDataSetChanged();
    }
}