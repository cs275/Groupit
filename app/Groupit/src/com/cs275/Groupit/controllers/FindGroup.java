package com.cs275.Groupit.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cs275.Groupit.R;
import com.cs275.Groupit.helpers.FacebookHelper;
import com.cs275.Groupit.helpers.ServerHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class FindGroup extends Controller {
	Activity activity;
	public FindGroup(Activity dash) {
		activity = dash;
		// TODO Auto-generated constructor stub
	}

	@Override
	public View inflate(LayoutInflater inflator, ViewGroup container) {
		rootView = inflator.inflate(R.layout.find_group,
				container, false);
		
		new ServerHelper(activity).getAllGroupNames(new ServerHelper.Callback(){
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
		
		SearchView search = (SearchView) rootView.findViewById(R.id.searchView);
		search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String arg0) {
				ServerHelper.search(arg0, new ServerHelper.Callback() {
					@Override
					public void finished(Exception e, String result) {
						if (result=="null") return;
						JsonParser jp = new JsonParser();
				        JsonArray items = jp.parse(result).getAsJsonArray();
				        initListView(items);
					}
				});
				return false;
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
				final String item = (String) ((TextView)view).getText();
				final String username = FacebookHelper.getUserName(activity);
				ServerHelper.joinGroup(item, username, new ServerHelper.Callback() {
					
					@Override
					public void finished(Exception e, String result) {
						if (result.equals("1")){
							Toast.makeText(activity, "Joined "+item, Toast.LENGTH_SHORT).show();
						}
					}
				});
			}

		});
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
