package me.shreyasr.quicknote;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.SharedPreferencesBackupHelper;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.IOException;

public class NoteBackupAgent extends BackupAgentHelper {

    @Override
    public void onCreate() {
        Log.d("Backup", "Creating");
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(ApplicationWrapper.getInstance(),
                Constants.NOTE_CONTENT, Constants.COLLAPSED,
                Constants.POS_X, Constants.POS_Y,
                Constants.WIDTH_PREF, Constants.HEIGHT_PREF,
                Constants.SMALL_WIDTH_PREF, Constants.SMALL_HEIGHT_PREF);
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
