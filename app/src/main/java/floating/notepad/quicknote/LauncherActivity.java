package floating.notepad.quicknote;

import android.app.Activity;
import android.os.Bundle;

import wei.mark.standout.StandOutWindow;

public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StandOutWindow.closeAll(ApplicationWrapper.getInstance(), NotepadWindow.class);
        StandOutWindow.show(ApplicationWrapper.getInstance(), NotepadWindow.class, StandOutWindow.DEFAULT_ID);
        this.finish();
    }
}
