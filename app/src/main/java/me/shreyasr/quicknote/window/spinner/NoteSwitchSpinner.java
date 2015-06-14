package me.shreyasr.quicknote.window.spinner;

import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import me.shreyasr.quicknote.App;
import me.shreyasr.quicknote.window.WindowUtils;

public class NoteSwitchSpinner extends Spinner {

    NoteTitleClickListener onClickListener;
    DropdownPopup dropdownPopup;

    public NoteSwitchSpinner(Context context) {
        this(context, null);
    }

    public NoteSwitchSpinner(Context context, int mode) {
        this(context, null, android.R.attr.spinnerStyle, mode);
    }

    public NoteSwitchSpinner(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.spinnerStyle);
    }

    public NoteSwitchSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0, -1);
    }


    public NoteSwitchSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        this(context, attrs, defStyleAttr, 0, mode);
    }

    public NoteSwitchSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode) {
        super(context, attrs, defStyleAttr, defStyleRes);
        dropdownPopup = new DropdownPopup(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        dropdownPopup.setAdapter(new DropDownAdapter(getAdapter()));
        ((NoteSwitchSpinnerAdapter)getAdapter()).dialog = dropdownPopup;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void setCustomOnClickListener(NoteTitleClickListener listener) {
        this.onClickListener = listener;
    }

    @Override
    public boolean performClick() {
        dropdownPopup.show(false);
        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        setSelection(which);
        dialog.dismiss();
    }

    private static class DropDownAdapter implements ListAdapter, SpinnerAdapter {

        private SpinnerAdapter mAdapter;

        public DropDownAdapter(SpinnerAdapter adapter) {
            this.mAdapter = adapter;
        }

        public int getCount() {
            return mAdapter == null ? 0 : mAdapter.getCount();
        }

        public Object getItem(int position) {
            return mAdapter == null ? null : mAdapter.getItem(position);
        }

        public long getItemId(int position) {
            return mAdapter == null ? -1 : mAdapter.getItemId(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            return getDropDownView(position, convertView, parent);
        }

        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return mAdapter.getDropDownView(position, convertView, parent);
        }

        public boolean hasStableIds() {
            return mAdapter != null && mAdapter.hasStableIds();
        }

        public void registerDataSetObserver(DataSetObserver observer) {
            if (mAdapter != null) {
                mAdapter.registerDataSetObserver(observer);
            }
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (mAdapter != null) {
                mAdapter.unregisterDataSetObserver(observer);
            }
        }

        public boolean areAllItemsEnabled() {
            return true;
        }

        public boolean isEnabled(int position) {
            return true;
        }

        public int getItemViewType(int position) {
            return 0;
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean isEmpty() {
            return getCount() == 0;
        }
    }

    public class DropdownPopup extends ListPopupWindow {

        public DropdownPopup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);

            setAnchorView(NoteSwitchSpinner.this);
            setModal(true);
            setPromptPosition(POSITION_PROMPT_ABOVE);
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onClickListener.onNoteTitleClick(dropdownPopup, position);
                    App.track("notes", "switch");
                }
            });
        }

        void computeContentWidth() {
            setContentWidth(WindowUtils.getWidthPx());
            setHorizontalOffset(-WindowUtils.getSizePx());
        }

        public void show(boolean firstRun) {
            if (!firstRun)
                computeContentWidth();

            App.track("notes", "switcher_open");

            setInputMethodMode(ListPopupWindow.INPUT_METHOD_NOT_NEEDED);
            super.show();
            ListView listView = getListView();
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            setSelection(NoteSwitchSpinner.this.getSelectedItemPosition());
        }
    }

    public static abstract class NoteTitleClickListener {

        public abstract void onNoteTitleClick(DropdownPopup popup, int position);
    }
}
