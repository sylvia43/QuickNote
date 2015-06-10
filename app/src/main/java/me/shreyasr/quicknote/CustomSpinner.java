package me.shreyasr.quicknote;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class CustomSpinner extends Spinner {

    private AlertDialog mPopup;
    private DialogInterface.OnClickListener onClickListener;

    public CustomSpinner(Context context) {
        super(context);
    }

    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mPopup != null && mPopup.isShowing()) {
            mPopup.dismiss();
            mPopup = null;
        }
    }

    public void setCustomItemClickListener(DialogInterface.OnClickListener listener) {
        this.onClickListener = listener;
    }

    //when clicked alertDialog is made
    @Override
    public boolean performClick() {
        Context context = getContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        CharSequence prompt = getPrompt();
        if (prompt != null) {
            builder.setTitle(prompt);
        }

        mPopup = builder.setSingleChoiceItems(new DropDownAdapter(getAdapter()), getSelectedItemPosition(), onClickListener).create();

        AlertDialog alert = builder.create();
        android.view.Window window = alert.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0; // x position
        params.y = 50; // y position
        params.height = 390; //LayoutParams.WRAP_CONTEN
        params.width = 315;
        params.horizontalMargin = 0;
        params.verticalMargin = 0;
        params.token = NotepadWindow.instance.notepadView.getWindowToken();
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
        window.setAttributes(params);
        alert.show();

        ((NoteListAdapter)getAdapter()).dialog = alert;

        //ListView.getDefaultSize(size, measureSpec)
        ListView listView = mPopup.getListView();
        //listView.set
        // Remove divider between rows
        listView.setDivider(null);

        // Set custom background
        listView.setBackgroundResource(R.drawable.spinner_dropdown_background);

        // Remove background from all (grand)parent's
        ViewParent parent = listView.getParent();
        while (parent != null && parent instanceof View) {
            ((View) parent).setBackgroundDrawable(null);

            parent = parent.getParent();
        }

        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        setSelection(which);
        dialog.dismiss();
    }

    private static class DropDownAdapter implements ListAdapter, SpinnerAdapter {
        private SpinnerAdapter mAdapter;

        /**
         * <p>Creates a new ListAddapter wrapper for the specified adapter.</p>
         *
         * @param adapter the Adapter to transform into a ListAdapter
         */
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

        /**
         * <p>Always returns false.</p>
         *
         * @return false
         */
        public boolean areAllItemsEnabled() {
            return true;
        }

        /**
         * <p>Always returns false.</p>
         *
         * @return false
         */
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
}

