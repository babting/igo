package com.babting.igo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.babting.igo.R;
import com.babting.igo.xml.model.SearchLocationResultAdapterModel;

public class SearchLocationResultAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	private List<SearchLocationResultAdapterModel> list;
	private int layout;
	
	public SearchLocationResultAdapter(Context context,	List<SearchLocationResultAdapterModel> list, int layout) {
		super();
		this.context = context;
		layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.list = list;
		this.layout = layout;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}
	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}
	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int pos = position;
		
		if(convertView == null) {
			convertView = layoutInflater.inflate(layout, parent, false);
		}
		
		TextView txtViewTitle = (TextView) convertView.findViewById(R.id.searchLocationRstCustomTextViewTitle);
		TextView txtViewDesc = (TextView) convertView.findViewById(R.id.searchLocationRstCustomTextViewDesc);
		
		txtViewTitle.setText(this.list.get(pos).getTitle());
		txtViewDesc.setText(this.list.get(pos).getDescription());
		
		return convertView;
	}
}
