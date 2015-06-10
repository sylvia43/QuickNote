package me.shreyasr.quicknote.notepad;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import me.shreyasr.quicknote.R;

public class NotepadEditText extends EditText {

    private static Paint linePaint;
    private Rect bounds = new Rect();

    public NotepadEditText(Context context, AttributeSet attributes) {
        super(context, attributes);
        linePaint = new Paint();
        linePaint.setColor(getResources().getColor(R.color.lines));
        linePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        linePaint.setAlpha((int) (this.getAlpha()*255));
        int firstLineY = getLineBounds(0, bounds);
        int lineHeight = getLineHeight();
        int totalLines = Math.max(getLineCount(), getHeight() / lineHeight);

        for (int i = 0; i < totalLines; i++) {
            int lineY = firstLineY + i * lineHeight;
            canvas.drawLine(bounds.left, lineY, bounds.right, lineY, linePaint);
        }
        super.onDraw(canvas);
    }
}