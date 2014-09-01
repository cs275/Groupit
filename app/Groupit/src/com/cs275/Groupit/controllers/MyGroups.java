package com.cs275.Groupit.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.cs275.Groupit.R;
import com.cs275.Groupit.helpers.FacebookHelper;
import com.cs275.Groupit.helpers.ServerHelper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MyGroups extends Controller{
	public Boolean isRoot = false;
	Activity activity;
	public MyGroups(Activity dashboard) {
		activity = dashboard;
	}
	
	@Override
	public View inflate(LayoutInflater inflator, ViewGroup container) {
		rootView = inflator.inflate(R.layout.fragment_dashboard,
				container, isRoot);
		
		
		//Log.d("Username: ", new FacebookHelper(FacebookHelper.getSession(activity)).getUserName(activity));
		new ServerHelper(activity).getGroupsForUser(FacebookHelper.getUserName(activity), new ServerHelper.Callback(){
			@Override
			public void finished(Exception e, String g) {
				TextView v = (TextView)(rootView.findViewById(R.id.name)); 
				if (g==null || e!=null || g.equals("null")){
					v.setText("You currently have no groups");
					return;
				}
				JsonArray groups = new JsonParser().parse(g).getAsJsonArray();
				
				String content = FacebookHelper.getUserName(activity)+"'s Groupits: \n    ";
				final ArrayList<String> list = new ArrayList<String>();
				for (int i=0; i<groups.size(); i++){
					list.add(groups.get(i).getAsString());
				}
				final StableArrayAdapter adapter = new StableArrayAdapter(activity,
						android.R.layout.simple_list_item_1, list);
				ListView listview = (ListView) activity.findViewById(R.id.GroupList);
				listview.setAdapter(adapter);
				v.setText(content);
			} 
		}); 
		
		return rootView;
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
