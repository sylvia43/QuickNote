package floating.notepad.quicknote;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;

public class NotepadWindow extends StandOutWindow {

    SharedPreferences prefs;

    @Override
    public String getAppName() {
        return "Floating Notepad";
    }

    @Override
    public int getAppIcon() {
        return R.drawable.ic_launcher;
    }

    @Override
    public void createAndAttachView(final int id, final FrameLayout frame) {
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.notepad_layout, frame, true);
        EditText et = (EditText) frame.findViewById(R.id.editText);

        et.setText(prefs.getString("text", ""));

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                save(frame);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Spinner spinner = (Spinner) frame.findViewById(R.id.spinner);

        List<CharSequence> noteNames = new ArrayList<CharSequence>();
        String[] noteTitles = prefs.getString("NOTE_TITLES", "New Note").split(",");

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_item, noteTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button dock = (Button) frame.findViewById(R.id.dockButton);
        dock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frame.findViewById(R.id.editText).setVisibility(View.GONE);
                frame.findViewById(R.id.dockButton).setVisibility(View.GONE);
                frame.findViewById(R.id.spinner).setVisibility(View.GONE);
                frame.findViewById(R.id.openButton).setVisibility(View.VISIBLE);
                updateViewLayout(id, new StandOutLayoutParams(id, 200, 200, StandOutLayoutParams.BOTTOM,
                        StandOutLayoutParams.LEFT));
            }
        });

        Button undock = (Button) frame.findViewById(R.id.openButton);
        undock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frame.findViewById(R.id.editText).setVisibility(View.VISIBLE);
                frame.findViewById(R.id.dockButton).setVisibility(View.VISIBLE);
                frame.findViewById(R.id.spinner).setVisibility(View.VISIBLE);
                frame.findViewById(R.id.openButton).setVisibility(View.GONE);
                updateViewLayout(id, getParams(id, null));
            }
        });

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("NOTE_TITLES", "Asdf, asdfg, asdfhj");
        editor.commit();
    }

    @Override
    public StandOutLayoutParams getParams(int id, Window window) {
        return new StandOutLayoutParams(id, 500, 500, StandOutLayoutParams.BOTTOM,
                StandOutLayoutParams.LEFT);
    }

    @Override
    public int getFlags(int id) {
        return StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE;
    }

    @Override
    public Notification getPersistentNotification(int id) {
        Notification.Builder n = new Notification.Builder(this);
        n.setSmallIcon(getAppIcon());
        n.setContentTitle(getPersistentNotificationTitle(id));
        n.setContentText(getPersistentNotificationMessage(id));
        n.setPriority(Notification.PRIORITY_MIN);
        n.setContentIntent(PendingIntent.getService(this, 0, getPersistentNotificationIntent(id), PendingIntent.FLAG_UPDATE_CURRENT));
        return n.build();
    }

    @Override
    public String getPersistentNotificationTitle(int id) {
        return getAppName();
    }

    @Override
    public String getPersistentNotificationMessage(int id) {
        return "Tap to save & close notepad";
    }

    @Override
    public Intent getPersistentNotificationIntent(int id) {
        return StandOutWindow.getCloseAllIntent(this, getClass());
    }

    public void save(FrameLayout frame) {
        SharedPreferences.Editor editor = prefs.edit();
        String text = ((EditText)frame.findViewById(R.id.editText)).getText().toString();
        editor.putString("text", text).commit();
    }
}
