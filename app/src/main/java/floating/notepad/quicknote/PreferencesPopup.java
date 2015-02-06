package floating.notepad.quicknote;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;

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
    public void createAndAttachView(int id, final FrameLayout frame) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.preferences_layout, frame, true);

        WindowManager wm = (WindowManager) ApplicationWrapper.getInstance().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        SharedPreferences prefs = ApplicationWrapper.getInstance().getSharedPrefs();
        final SharedPreferences.Editor edit =  prefs.edit();

        SeekBar seekBarWidth = (SeekBar)frame.findViewById(R.id.widthSeekBar);
        seekBarWidth.setMax(size.x);
        seekBarWidth.setProgress(prefs.getInt(Constants.WIDTH_PREF, Constants.DEFAULT_WIDTH));
        seekBarWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < Constants.MIN_WIDTH) {
                    progress = Constants.MIN_WIDTH;
                    seekBar.setProgress(Constants.MIN_WIDTH);
                }
                edit.putInt(Constants.WIDTH_PREF, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar seekBarHeight = (SeekBar)frame.findViewById(R.id.heightSeekBar);
        seekBarHeight.setMax(size.y);
        seekBarHeight.setProgress(prefs.getInt(Constants.HEIGHT_PREF, Constants.DEFAULT_HEIGHT));
        seekBarHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < Constants.MIN_HEIGHT) {
                    progress = Constants.MIN_HEIGHT;
                    seekBar.setProgress(Constants.MIN_HEIGHT);
                }
                edit.putInt(Constants.HEIGHT_PREF, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ImageButton save = (ImageButton) frame.findViewById(R.id.savePreferencesButton);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.apply();
                StandOutWindow.close(ApplicationWrapper.getInstance(), PreferencesPopup.class, 1);
                StandOutWindow.show(ApplicationWrapper.getInstance(), NotepadWindow.class, 0);
            }
        });

        ImageButton close = (ImageButton) frame.findViewById(R.id.closePreferencesButton);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StandOutWindow.close(ApplicationWrapper.getInstance(), PreferencesPopup.class, 1);
                StandOutWindow.show(ApplicationWrapper.getInstance(), NotepadWindow.class, 0);
            }
        });

        Button reset = (Button) frame.findViewById(R.id.resetPreferencesButton);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.putInt(Constants.WIDTH_PREF, Constants.DEFAULT_WIDTH);
                edit.putInt(Constants.HEIGHT_PREF, Constants.DEFAULT_HEIGHT);
                edit.apply();
                StandOutWindow.close(ApplicationWrapper.getInstance(), PreferencesPopup.class, 1);
                StandOutWindow.show(ApplicationWrapper.getInstance(), NotepadWindow.class, 0);
            }
        });
    }

    @Override
    public StandOutLayoutParams getParams(int id, Window window) {
        WindowManager wm = (WindowManager) ApplicationWrapper.getInstance().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int rotation = display.getRotation();

        switch (rotation) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                return new StandOutLayoutParams(id,
                        (int)(size.x*0.8),
                        (int)(size.y*0.8),
                        (int)(size.x*0.1),
                        (int)(size.y*0.1));
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                return new StandOutLayoutParams(id,
                        (int)(size.y*0.8),
                        (int)(size.x*0.8),
                        (int)(size.y*0.1),
                        (int)(size.x*0.1));
            default:
                Log.e("ROTATION", "Unknown rotation: " + rotation + ".");
                return null;
        }


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