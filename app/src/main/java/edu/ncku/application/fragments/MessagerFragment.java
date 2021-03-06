package edu.ncku.application.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import edu.ncku.application.MainActivity;
import edu.ncku.application.R;
import edu.ncku.application.adapter.ListMsgsAdapter;
import edu.ncku.application.io.file.MsgsReaderTask;
import edu.ncku.application.model.Message;

/**
 * 顯示推播訊息的列表頁面，當參數大於等於0時，進入該位置的推播訊息
 */
public class MessagerFragment extends Fragment {

    private static final String DEBUG_FLAG = MessagerFragment.class.getName();
    private static final String POSITION = "POSITION";

    private MainActivity activity;

    private ProgressBar progressBar;
    private TextView textView;
    private ListView listView;
    private Handler mHandler = new Handler();
    private ListMsgsAdapter listViewAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MessagerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessagerFragment getInstance(int position) {
        MessagerFragment messagerFragment = new MessagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(POSITION, position);
        messagerFragment.setArguments(bundle);
        return messagerFragment;
    }

    public MessagerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_messager, container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.msgProgressBar);
        textView = (TextView) rootView.findViewById(R.id.msgTip);
        listView = (ListView) rootView.findViewById(R.id.msgListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(DEBUG_FLAG, "position : " + position);
                changeToMsgViewer(position);
            }
        }); // 註冊點擊事件

        /* 實現多選刪除功能 */
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                activity.getMenuInflater().inflate(R.menu.menu_delete, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final android.view.ActionMode mode, MenuItem item) {
                try {
                    switch (item.getItemId()) {
                        case R.id.selectMenuItem:
                            for (int position = 0; position < listViewAdapter.getCount(); position++) {
                                listView.setItemChecked(position, true);
                            }
                            break;
                        case R.id.deleteMenuItem:
                            (new AlertDialog.Builder(getActivity()))
                                    .setMessage(R.string.deleteHint)
                                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // User clicked OK button
                                            listViewAdapter.deleteSelect(listView.getCheckedItemPositions());
                                            onActivityCreated(null);
                                            mode.finish();
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // User cancelled the dialog
                                        }
                                    }).show();
                            break;
                        default:
                            Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show();
                            break;
                    }
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {

            }

            @Override
            public void onItemCheckedStateChanged(android.view.ActionMode mode, final int position, long id, final boolean checked) {
                mode.setTitle(Integer.toString(selectItems()));
                mode.invalidate();
                Log.d(DEBUG_FLAG, String.format("Position : %d %s", position, (checked) ? "checked" : "isn't checked"));
            }

            private int selectItems() {
                int select = 0;
                SparseBooleanArray checkedItemPositions = listView.getCheckedItemPositions();
                for (int i = 0; i < checkedItemPositions.size(); i++) {
                    if (checkedItemPositions.get(checkedItemPositions.keyAt(i))) {
                        select++;
                    }
                }

                return select;
            }

        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(DEBUG_FLAG, "ReaderTask start!");
                    if (updateList()) {
                        progressBar.setVisibility(View.INVISIBLE);
                        listView.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.INVISIBLE);
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, 500);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem settingItem = menu.findItem(R.id.settingMenuItem);
        if (settingItem != null) {
            settingItem.setVisible(false);
        }
        super.onPrepareOptionsMenu(menu);
    }

    private void setListAdapter(final ListAdapter adapter) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                listView.setAdapter(adapter);
            }
        });
    }

    /**
     * 從檔案讀入推播訊息，並顯示出來
     *
     * @return 載入推播訊息是否成功
     * @throws Exception
     */
    private boolean updateList() throws Exception {
        MsgsReaderTask msgsReaderTask = new MsgsReaderTask(this);
        msgsReaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        listViewAdapter = msgsReaderTask.get();

        if (listViewAdapter != null) {
            setListAdapter(listViewAdapter);
            int position = getArguments().getInt(POSITION);

            /* 當有來自Notification的位置參數，將會自動轉入該推播訊息 */
            Log.d(DEBUG_FLAG, "position : " + position);
            if (position >= 0 && position < listViewAdapter.getCount()) {
                int realPosition = listViewAdapter.getCount() - (position + 1);
                changeToMsgViewer(realPosition);
                getArguments().putInt(POSITION, -1);
            }
            return true;
        } else {
            Log.e(DEBUG_FLAG, "listViewAdapter is null!");
            return false;
        }
    }

    /**
     * 進入該推播訊息內容頁面
     *
     * @param position 推播訊息位置
     */
    private void changeToMsgViewer(int position) {
        Message news = (Message) listViewAdapter.getItem(position);

        Bundle bundle = new Bundle();
        bundle.putString("title", news.getTitle());
        bundle.putString("date", new SimpleDateFormat("yyyy/MM/dd HH:mm").format((long) news.getPubTime() * 1000));
        bundle.putString("unit", ""); // 推播訊息沒有單位
        bundle.putString("contents", news.getContents().replace("\r\n", "<br>").trim());

        NewsViewerFragment msgViewerFragment = new NewsViewerFragment();
        msgViewerFragment.setArguments(bundle);

        FragmentManager fragmentManager = activity.getFragmentManager();
        fragmentManager.beginTransaction()
                .addToBackStack(null)
                .add(R.id.content_frame, msgViewerFragment).commit();
    }

}
