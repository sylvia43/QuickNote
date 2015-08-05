package me.shreyasr.quicknote.window.spinner;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import me.shreyasr.quicknote.App;

public abstract class BaseSpinner extends Spinner {

    private int width;
    private final int xOff;
    private final String logName;
    CustomItemClickListener onClickListener;
    DropdownPopup dropdownPopup;

    public BaseSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode,
                       int width, int xOff, String logName) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.width = width;
        this.xOff = xOff;
        this.logName = logName;
        dropdownPopup = new DropdownPopup(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        dropdownPopup.setAdapter(new DropDownAdapter(getAdapter()));
        dropdownPopup.show(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void setCustomOnClickListener(CustomItemClickListener listener) {
        this.onClickListener = listener;
    }

    @Override
    public boolean performClick() {
        dropdownPopup.show(false);
        return true;
    }

    @Override
    public void onClick(@NonNull DialogInterface dialog, int which) {
        setSelection(which);
        dialog.dismiss();
    }

    /** Redirectes getView to getDropDownView, since both SpinnerAdapter and ListAdapter use getView. */
    private static class DropDownAdapter extends BaseAdapter {

        @NonNull private final SpinnerAdapter adapter;

        public DropDownAdapter(@NonNull SpinnerAdapter adapter) {
            this.adapter = adapter;
        }

        public int getCount() { return adapter.getCount(); }
        public Object getItem(int position) { return adapter.getItem(position); }
        public long getItemId(int position) { return adapter.getItemId(position); }

        public View getView(int position, View convertView, ViewGroup parent) {
            return adapter.getDropDownView(position, convertView, parent);
        }
    }

    public class DropdownPopup extends ListPopupWindow {

        public DropdownPopup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);

            setAnchorView(BaseSpinner.this);
            setModal(true);
            setFocusable(true);
            setPromptPosition(POSITION_PROMPT_ABOVE);
            setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onClickListener.onItemClick(dropdownPopup, position);
                    App.track(logName, "spinner item selected");
                }
            });
        }

        public void show(boolean firstRun) {
            App.track(logName, "spinner open");
            setHorizontalOffset(xOff);

            setInputMethodMode(ListPopupWindow.INPUT_METHOD_NOT_NEEDED);

            super.show();
            ListView listView = getListView();
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setDivider(null);
            setSelection(BaseSpinner.this.getSelectedItemPosition());

            if (width == -1 && firstRun) {
                for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                    View view = getViewByPosition(listView, i);
                    view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                    width = Math.max(width, view.getMeasuredWidth());
                }
            }
            setContentWidth(width);
            if (firstRun) dismiss();
        }

        public View getViewByPosition(ListView listView, int pos) {
            final int firstListItemPosition = listView.getFirstVisiblePosition();
            final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

            if (pos < firstListItemPosition || pos > lastListItemPosition ) {
                return listView.getAdapter().getView(pos, null, listView);
            } else {
                final int childIndex = pos - firstListItemPosition;
                return listView.getChildAt(childIndex);
            }
        }
    }

    public static abstract class CustomItemClickListener {

        public abstract void onItemClick(DropdownPopup popup, int position);
    }
}
