package me.shreyasr.quicknote.window;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.shreyasr.quicknote.App;
import me.shreyasr.quicknote.R;
import wei.mark.standout.StandOutWindow;

public class MenuAdapter extends BaseAdapter {

    private List<StandOutWindow.DropDownListItem> items;

    public MenuAdapter(List<StandOutWindow.DropDownListItem> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public StandOutWindow.DropDownListItem getItem(int pos) {
        return items.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(App.get()).inflate(R.layout.menu_spinner_icon, parent, false);
        }
        return view;
    }

    @Override
    public View getDropDownView(int pos, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(App.get()).inflate(R.layout.menu_spinner_item, parent, false);
        }

        final StandOutWindow.DropDownListItem item = getItem(pos);

        ((ImageView) view.findViewById(R.id.icon)).setImageResource(item.icon);
        ((TextView) view.findViewById(R.id.description)).setText(item.description);

        return view;
    }
}
