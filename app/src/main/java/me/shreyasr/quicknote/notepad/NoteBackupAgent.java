package me.shreyasr.quicknote.notepad;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.SharedPreferencesBackupHelper;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.IOException;

import me.shreyasr.quicknote.ApplicationWrapper;
import me.shreyasr.quicknote.Constants;

public class NoteBackupAgent extends BackupAgentHelper {

    @Override
    public void onCreate() {
        Log.d("Backup", "Creating");
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(ApplicationWrapper.getInstance(),
                Constants.NOTE_TITLES, Constants.CURRENT_NOTE, Constants.COLLAPSED, // NotepadUtils.getNoteTitles()
                Constants.WIDTH_PREF, Constants.HEIGHT_PREF, Constants.SIZE_PREF,
                Constants.POS_X, Constants.POS_Y);
        addHelper("prefs", helper);
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
        Log.d("Backup", "Backing up");
        super.onBackup(oldState, data, newState);
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
        Log.d("Backup", "Restoring");
        super.onRestore(data, appVersionCode, newState);
    }
}
