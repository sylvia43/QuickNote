package me.shreyasr.quicknote.notepad;

import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.shreyasr.quicknote.App;
import me.shreyasr.quicknote.Constants;
import me.shreyasr.quicknote.R;
import me.shreyasr.quicknote.window.NotepadWindow;
import me.shreyasr.quicknote.window.spinner.NoteSwitchSpinner;
import me.shreyasr.quicknote.window.spinner.NoteSwitchSpinnerAdapter;

public class NotepadUtils {

    private final static SharedPreferences prefs = App.get().getSharedPrefs();

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
//            String defaultNoteTitle = ApplicationWrapper.get().getString(R.string.default_note_name);
//            prefs.edit()
//            prefs.edit().putString(Constants.CURRENT_NOTE, defaultNoteTitle).apply();
            return new String[0];
        }
        return prefs.getString(Constants.NOTE_TITLES, "").split(",");
    }

    public static String getNoteTitle(int position) {
        return getNoteTitles()[position];
    }

    public synchronized static void addNote(String title) {
        if (hasNoteTitle(title) || title.contains(","))
            return;
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
    }

    public static List<String> getNoteTitlesList() {
        return new ArrayList<>(Arrays.asList(getNoteTitles()));
    }

    public static void setNoteTitlesList(List<String> list) {
        boolean first = true;
        String titles = "";
        for (String s : list) {
            if (!first)
                titles += ",";
            first = false;
            titles += s;
        }
        prefs.edit().putString(Constants.NOTE_TITLES, titles).apply();
    }

    public static void editNoteTitle(String oldTitle, String newTitle) {
        List<String> titles = getNoteTitlesList();
        int index = titles.indexOf(oldTitle);
        String content = prefs.getString(oldTitle, "");
        if (index > -1)
            titles.set(index, newTitle);
        setNoteTitlesList(titles);
        prefs.edit().putString(newTitle, content).remove(oldTitle).apply();
        setCurrentNote(newTitle);
    }

    private static Toast cannotDeleteLastToast = null;
    public static void removeNoteTitle(String titleToRemove) {
        if (!hasNoteTitle(titleToRemove))
            return;
        if (getNoteTitles().length <= 1) {
            if (cannotDeleteLastToast == null)
                cannotDeleteLastToast = Toast.makeText(App.get(), "Cannot delete last note", Toast.LENGTH_SHORT);
            cannotDeleteLastToast.show();
            return;
        }
        List<String> titles = getNoteTitlesList();
        titles.remove(titleToRemove);
        setNoteTitlesList(titles);
        if (titleToRemove.equals(getCurrentNoteTitle()))
            setCurrentNote(titles.get(0));
    }

    public static void updateNotepad() {
        if (prefs.contains(Constants.OLD_NOTE_CONTENT)) {
            addNote("Old Notes");
            prefs.edit().putString("Old Notes", prefs.getString(Constants.OLD_NOTE_CONTENT, "")).remove(Constants.OLD_NOTE_CONTENT).apply();
        }
        ((EditText) NotepadWindow.instance.notepadView.findViewById(R.id.notepadContent)).setText(getCurrentNoteContent());
        NoteSwitchSpinner spinner = NotepadWindow.instance.spinner;
        NoteSwitchSpinnerAdapter adapter = (NoteSwitchSpinnerAdapter) spinner.getAdapter();
        spinner.setSelection(adapter.getPosition(getCurrentNoteTitle()));
        adapter.notifyDataSetChanged();
    }

    public static boolean isFirstRun() {
        return prefs.getBoolean(Constants.FIRST_RUN, true);
    }

    public static void addIntroNote() {
        String intro = App.get().getString(R.string.intro_note_title);
        addNote(intro);
        setCurrentNote(intro);
        saveContent("Welcome to QuickNote!\n\n" +
                "This content can be shown again from Preferences.\n\n" +
                "Top right button docks/undocks.\n\n" +
                "Left button opens the menu, with settings and info.\n\n" +
                "'+' button creates new note.\n\n" +
                "Dropdown in the center edits titles, and switches or deletes notes.\n\n" +
                "Enjoy!");
        prefs.edit().putBoolean(Constants.FIRST_RUN, false).apply();
        ((NoteSwitchSpinnerAdapter)NotepadWindow.instance.spinner.getAdapter()).append(intro);
        updateNotepad();
    }
}
