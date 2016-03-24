package edu.ncku.application.util.adapter;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import edu.ncku.application.R;
import edu.ncku.application.fragments.NewsViewerFragment;
import edu.ncku.application.model.Message;

/**
 * Created by NCKU on 2016/1/8.
 */
public class ListMsgsAdapter extends BaseAdapter {

    private static final String DEBUG_TAG = ListMsgsAdapter.class.getName();
    private int show;

    private Activity activity;
    private LinkedList<Message> AllMsgs = new LinkedList<Message>(), showMsgs = new LinkedList<Message>();

    public ListMsgsAdapter(Activity activity, LinkedHashSet<Message> msgsSet, int localShow) {
        this.activity = activity;

        this.show = (localShow > msgsSet.size()) ? msgsSet.size() : localShow;

        for(Message msg : msgsSet){
            AllMsgs.addFirst(msg);
        }

        for (int i = 0; i < show; i++) {
            showMsgs.add(AllMsgs.get(i));
        }
    }

    /**
     * Update news list which shows on the screen
     *
     * @param moreShow the number of the news which want to show more
     * @return the number of the news which show more
     */
    public int showMoreOldMessaage(int moreShow) {
        try {
            int original = this.getCount();
            if (original == AllMsgs.size())
                return 0;// 當沒有舊的訊息時不再更新

            if (original + moreShow >= AllMsgs.size()) {
                Log.v(DEBUG_TAG, "全部資料顯示出來");
                for (int i = original; i < AllMsgs.size(); i++) {
                    showMsgs.addLast(AllMsgs.get(i));
                }
                this.notifyDataSetChanged();	// 通知更新UI
                Log.v(DEBUG_TAG, "return "
                        + (this.getCount() - original));

                return this.getCount() - original;
            }

            Log.v(DEBUG_TAG, "未滿");
            for (int i = original; i < original + moreShow; i++) {
                showMsgs.addLast(AllMsgs.get(i));
            }

            this.notifyDataSetChanged();
            Log.v(DEBUG_TAG, "return " + moreShow);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return moreShow;
    }

    public void triggerViewClick(int offset){
        int position = getCount() - (offset + 1);// Adapter類別取出資料的順序是倒反的
        getView((position >= 0)?position:0, null, null).callOnClick();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return showMsgs.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return showMsgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater) activity.getApplicationContext()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        try {
            convertView = mInflater
                    .inflate(R.layout.fragment_msgs_list_item, null);
            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    Message news = (Message) getItem(position);

                    Bundle bundle = new Bundle();
                    bundle.putString("title", news.getTitle());
                    bundle.putString("date", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format((long)news.getPubTime() * 1000));
                    bundle.putString("unit", "");
                    bundle.putString("contents", news.getContents().replace("\r\n", "<br>").trim());

                    NewsViewerFragment msgViewerFragment = new NewsViewerFragment();
                    msgViewerFragment.setArguments(bundle);

                    FragmentManager fragmentManager = activity.getFragmentManager();
                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .add(R.id.content_frame, msgViewerFragment).commit();
                }
            });

            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView
                    .findViewById(R.id.txtTitle);
            holder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);

            convertView.setTag(holder);

            Message items = (Message) getItem(position);
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            int timeStamp = items.getPubTime();
            String title = items.getTitle(), date = sdFormat.format(new Date((long)timeStamp*1000)), unit = items.getUnit();

            holder.txtTitle.setText((title!=null)?title:"");
            holder.txtDate.setText((date != null) ? date : "");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private class ViewHolder {
        TextView txtTitle;
        TextView txtDate;
    }
}
