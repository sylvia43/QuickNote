package me.shreyasr.quicknote.window;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import me.shreyasr.quicknote.App;
import me.shreyasr.quicknote.R;
import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;

public class InfoWindow extends StandOutWindow {

    @Override
    public String getAppName() {
        return App.get().getAppName();
    }

    @Override
    public int getAppIcon() {
        return App.get().getAppIcon();
    }

    @Override
    public void createAndAttachView(final int id, final FrameLayout frame) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.info_popup, frame, true);

        TextView githubLink = (TextView) frame.findViewById(R.id.github_link);
        githubLink.setText(Html.fromHtml(getString(R.string.github_link)));
        githubLink.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                App.track("about", "github");
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/anubiann00b/QuickNote"));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    InfoWindow.this.startActivity(i);
                    StandOutWindow.close(App.get(), InfoWindow.class, 1);
                        NotepadWindow.instance.show(0);
                    NotepadWindow.instance.collapse(NotepadWindow.instance.notepadView, 0);
                }
                return true;
            }
        });

        TextView feedback = (TextView) frame.findViewById(R.id.feedback);
        feedback.setText(Html.fromHtml(getString(R.string.sendFeedback)));
        feedback.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                App.track("about", "feedback");
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Intent i = new Intent(Intent.ACTION_SENDTO);
                    i.setType("text/plain");
                    i.setData(Uri.parse("mailto:"));
                    i.putExtra(Intent.EXTRA_EMAIL, new String[] { "skraman1999@gmail.com" });
                    i.putExtra(Intent.EXTRA_SUBJECT, "QuickNote Feedback");
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    InfoWindow.this.startActivity(i);
                    StandOutWindow.close(App.get(), InfoWindow.class, 1);
                    NotepadWindow.instance.show(0);
                    NotepadWindow.instance.collapse(NotepadWindow.instance.notepadView, 0);
                }
                return true;
            }
        });

        ImageButton close = (ImageButton) frame.findViewById(R.id.closeInfoButton);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StandOutWindow.close(App.get(), InfoWindow.class, 1);
                NotepadWindow.instance.show(0);
            }
        });
    }

    @Override
    public StandOutLayoutParams getParams(int id, Window window) {
        WindowManager wm = (WindowManager) App.get().getSystemService(Context.WINDOW_SERVICE);
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
                return null;
        }
    }

    @Override
    public int getFlags(int id) {
        return StandOutFlags.FLAG_WINDOW_FOCUS_INDICATOR_DISABLE;
    }

    @Override
    public Notification getPersistentNotification(int id) {
        return App.get().getPersistentNotification();
    }
}
