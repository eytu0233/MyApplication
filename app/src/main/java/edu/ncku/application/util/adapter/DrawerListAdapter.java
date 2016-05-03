package edu.ncku.application.util.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.ncku.application.R;
import edu.ncku.application.util.DrawerListItem;

/**
 * Created by NCKU on 2016/1/15.
 */
public class DrawerListAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<DrawerListItem> drawerListItems;

    public DrawerListAdapter(Activity activity, ArrayList<DrawerListItem> drawerListItems) {
        this.activity = activity;
        this.drawerListItems = drawerListItems;
    }

    @Override
    public int getCount() {
        return drawerListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return drawerListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater) activity.getApplicationContext()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        try {
            convertView = mInflater
                    .inflate(R.layout.fragment_drawer_list_item, null);


            holder = new ViewHolder();
            holder.drawerString = (TextView) convertView
                    .findViewById(R.id.txtDrawer);
            holder.drawerString.setTextSize(holder.drawerString.getTextSize()/4);
            holder.drawerString.setText(((DrawerListItem)getItem(position)).getItemString());

            convertView.setTag(holder);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private class ViewHolder {
        TextView drawerString;
    }
}
