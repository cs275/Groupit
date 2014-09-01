package com.cs275.Groupit.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cs275.Groupit.R;
import com.cs275.Groupit.helpers.FacebookHelper;
import com.cs275.Groupit.helpers.ServerHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class Messaging extends Controller{
	Activity activity;
	Button button;
	String item;
	public Messaging(Activity dashboard) {
		activity = dashboard;
	}

	@Override
	public View inflate(final LayoutInflater inflator, final ViewGroup container) {
		rootView = inflator.inflate(R.layout.message_center,
				container, false);
		
		button = (Button) rootView.findViewById(R.id.goButton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()){
				case R.id.goButton:
					container.removeAllViews();
					
					new MessagingChat(activity, item).inflate(inflator, container);
					break;
				}
			}
		});
		
		new ServerHelper(activity).getGroupsForUser(FacebookHelper.getUserName(activity), new ServerHelper.Callback(){
			@Override
			public void finished(Exception e, String g) {
				if (g==null || g.equals("null")){
					return;
				}
				JsonParser jp = new JsonParser();
		        JsonArray items = jp.parse(g).getAsJsonArray();
		        initListView(items);
			} 
		});
		
		return rootView;
	}
	
	private void initListView(JsonArray items){
		final ListView listview = (ListView) rootView.findViewById(R.id.listview);

		final ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < items.size(); ++i) {
			list.add(items.get(i).getAsString());
		}
		final StableArrayAdapter adapter = new StableArrayAdapter(activity,
				android.R.layout.simple_list_item_1, list);
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@SuppressLint("NewApi") @Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				item = (String) ((TextView)view).getText();
			}
		});
	}
	
	public void create(){
		Spinner category = (Spinner) rootView.findViewById(R.id.category);
		
		Resources res = activity.getResources();
		String[] categories = res.getStringArray(R.array.category_array);
		
		String selectedCategory = categories[category.getSelectedItemPosition()];
	}
	
	private class StableArrayAdapter extends ArrayAdapter<String> {

	    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

	    public StableArrayAdapter(Context context, int textViewResourceId,
	        List<String> objects) {
	      super(context, textViewResourceId, objects);
	      for (int i = 0; i < objects.size(); ++i) {
	        mIdMap.put(objects.get(i), i);
	      }
	    }

	    @Override
	    public long getItemId(int position) {
	      String item = getItem(position);
	      return mIdMap.get(item);
	    }

	    @Override
	    public boolean hasStableIds() {
	      return true;
	    }

	  }

}