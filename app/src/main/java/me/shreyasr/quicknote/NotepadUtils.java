package me.shreyasr.quicknote;

import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.Toast;

public class NotepadUtils {

    private final static SharedPreferences prefs = ApplicationWrapper.getInstance().getSharedPrefs();

    public static boolean hasCurrentNote() {
        return prefs.contains(Constants.CURRENT_NOTE);
    }

    public static String getCurrentNoteTitle() {
        return prefs.getString(Constants.CURRENT_NOTE, "");
    }
    public static String getCurrentNoteContent() {
        if (!hasCurrentNote())
            return "";
        return prefs.getString(getCurrentNoteTitle(), "");
    }

    public static String[] getNoteTitles() {
        if (prefs.getString(Constants.NOTE_TITLES, "").isEmpty()) {
            String defaultNoteTitle = ApplicationWrapper.getInstance().getString(R.string.deault_note_name);
            addNote(defaultNoteTitle);
            prefs.edit().putString(Constants.CURRENT_NOTE, defaultNoteTitle).apply();
        }
        return prefs.getString(Constants.NOTE_TITLES, "").split(",");
    }

    public synchronized static void addNote(String title) {
        String titles = prefs.getString(Constants.NOTE_TITLES, "");
        SharedPreferences.Editor edit = prefs.edit();
        if (!"".equals(titles))
            edit.putString(Constants.NOTE_TITLES, titles + "," + title);
        else
            edit.putString(Constants.NOTE_TITLES, title);
        edit.apply();
    }

    public static void setCurrentNote(String currentNote) {
        if (!hasNoteTitle(currentNote))
            addNote(currentNote);
        prefs.edit().putString(Constants.CURRENT_NOTE, currentNote).apply();
        updateNotepad();
    }

    public static boolean hasNoteTitle(String note) {
        for (String title : getNoteTitles())
            if (title.equals(note))
                return true;
        return false;
    }

    public static void saveContent(String text) {
        if (hasCurrentNote())
            prefs.edit().putString(getCurrentNoteTitle(), text).apply();
        else
            setCurrentNote("default");
    }

    public static void editNoteTitle(String title, String newTitle) {
        String titles = prefs.getString(Constants.NOTE_TITLES, "").replace(title + ",", newTitle + ",").replace("," + title, "," + newTitle);
        prefs.edit().putString(Constants.NOTE_TITLES, titles).apply();
        setCurrentNote(newTitle);
    }

    public static void removeNoteTitle(String item) {
        if (!hasNoteTitle(item))
            return;
        String titles = prefs.getString(Constants.NOTE_TITLES, "");
        titles = titles.replace(item + ",", "").replace("," + item, "");
        prefs.edit().putString(Constants.NOTE_TITLES, titles).apply();
        if (item.equals(getCurrentNoteTitle()))
            setCurrentNote(getNoteTitles()[0]);
    }

    public static void updateNotepad() {
        ((EditText)NotepadWindow.instance.notepadView.findViewById(R.id.notepadContent)).setText(getCurrentNoteContent());
        CustomSpinner spinner = NotepadWindow.instance.spinner;
        NoteListAdapter adapter = (NoteListAdapter) spinner.getAdapter();
        spinner.setSelection(adapter.getPosition(getCurrentNoteTitle()));
        adapter.notifyDataSetChanged();
    }
}
