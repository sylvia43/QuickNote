package floating.notepad.quicknote;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;

public class PreferencesPopup extends StandOutWindow {

    @Override
    public String getAppName() {
        return "QuickNote";
    }

    @Override
    public int getAppIcon() {
        return R.drawable.ic_launcher;
    }

    @Override
    public void createAndAttachView(int id, FrameLayout frame) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.preferences_layout, frame, true);

        ImageButton close = (ImageButton) frame.findViewById(R.id.closePreferencesButton);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StandOutWindow.close(ApplicationWrapper.getInstance().getApplicationContext(), PreferencesPopup.class, 1);
                StandOutWindow.show(ApplicationWrapper.getInstance().getApplicationContext(), NotepadWindow.class, 0);
            }
        });
    }

    @Override
    public StandOutLayoutParams getParams(int id, Window window) {
        WindowManager wm = (WindowManager) ApplicationWrapper.getInstance().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return new StandOutLayoutParams(id,
                (int)(size.x*0.8),
                (int)(size.y*0.8),
                (int)(size.x*0.1),
                (int)(size.y*0.1));
    }

    @Override
    public int getFlags(int id) {
        return StandOutFlags.FLAG_WINDOW_FOCUS_INDICATOR_DISABLE;
    }

    @Override
    public Notification getPersistentNotification(int id) {
        return ApplicationWrapper.getInstance().getPersistentNotification();
    }
}
