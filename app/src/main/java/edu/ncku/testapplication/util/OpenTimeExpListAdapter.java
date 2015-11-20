package edu.ncku.testapplication.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import edu.ncku.testapplication.R;

public class OpenTimeExpListAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private List<String> mListDataHeader;
	private HashMap<String, List<String>> mListDataChild;

	public OpenTimeExpListAdapter(Context context, List<String> listDataHeader,
								  HashMap<String, List<String>> listChildData) {
		this.mContext = context;
		this.mListDataHeader = listDataHeader;
		this.mListDataChild = listChildData;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
				.get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		final String childText = (String) getChild(groupPosition, childPosition); // 取得內容

		/* 設置內容layout */
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(
					R.layout.fragment_info_open_time_content, null);
		}

		/* 設置內容 */
		TextView txtListChild = (TextView) convertView
				.findViewById(R.id.txtContent);

		txtListChild.setText(childText);
		txtListChild.setTextColor(Color.BLACK);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
				.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.mListDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return this.mListDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		String headerTitle = (String) getGroup(groupPosition); // 取得標題
		/* 設置標題layout */
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(
					R.layout.fragment_lib_info_item, null);
		}

		convertView.findViewById(R.id.icEnterImgView).setVisibility(
				View.INVISIBLE);

		/* 設置標題 */
		TextView lblListHeader = (TextView) convertView
				.findViewById(R.id.txtTitle);
		lblListHeader.setTypeface(null, Typeface.BOLD);
		lblListHeader.setText(headerTitle);

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

}
