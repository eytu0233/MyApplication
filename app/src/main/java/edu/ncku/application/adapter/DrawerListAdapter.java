package edu.ncku.application.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.ncku.application.R;
import edu.ncku.application.util.DrawerListItem;
import edu.ncku.application.util.Preference;

/**
 * Created by NCKU on 2016/1/15.
 */
public class DrawerListAdapter extends BaseAdapter {

    private static final String DEBUG_FLAG = DrawerListAdapter.class.getName();

    final private Activity activity;
    final private ArrayList<DrawerListItem> drawerListItems;
    final private boolean containName;

    public DrawerListAdapter(Activity activity, ArrayList<DrawerListItem> drawerListItems, boolean containName) {
        this.activity = activity;
        this.drawerListItems = drawerListItems;
        this.containName = containName;
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
            if (containName && position == 0) {
                holder.drawerString.setPadding(0, 40, 0, 40);
                holder.drawerString.setTextSize(holder.drawerString.getTextSize() / 2);
                holder.drawerString.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Log.d(DEBUG_FLAG, "Long click...");
                        new AlertDialog.Builder(activity)
                                .setMessage(Preference.getDeviceID(activity))
                                .setTitle(Preference.getUsername(activity) + "'s DeviceID")
                                .create().show();
                        return true;
                    }
                });

                holder.drawerString.setTextColor(Color.argb(127, 3, 0, 15));
            } else {
                holder.drawerString.setTextSize(holder.drawerString.getTextSize() / 4);
            }
            holder.drawerString.setText(((DrawerListItem) getItem(position)).getItemString());

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
