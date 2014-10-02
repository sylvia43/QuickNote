package floating.notepad.quicknote;

import android.app.Activity;
import android.os.Bundle;

import floating.notepad.quicknote.standout.StandOutWindow;

public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StandOutWindow.closeAll(this, NotepadWindow.class);
//        StandOutWindow.show(this, NotepadWindow.class, StandOutWindow.DEFAULT_ID);
        this.finish();
    }
}
