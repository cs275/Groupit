package com.cs275.Groupit.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cs275.Groupit.R;
import com.cs275.Groupit.helpers.FacebookHelper;
import com.cs275.Groupit.helpers.ServerHelper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MessagingChat extends Controller{
	Activity activity;
	String GroupName;
	String item;
	
	public MessagingChat(Activity dashboard, String groupname) {
		activity = dashboard;
		GroupName = groupname;
	}

	public View inflate(LayoutInflater inflator, ViewGroup container) {
		rootView = inflator.inflate(R.layout.messaging,
				container, true);
		
		TextView text = (TextView) rootView.findViewById(R.id.Group_name);
		text.setText(GroupName);
		
		Button button = (Button) rootView.findViewById(R.id.createButton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()){
				case R.id.createButton:
					EditText text = (EditText) rootView.findViewById(R.id.SendMessage);
					final String message = text.getText().toString();
					
					create(GroupName, message);
					break;
				}
			}
		});
		
		Button button1 = (Button) rootView.findViewById(R.id.refresh);
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()){
				case R.id.refresh:
					ServerHelper.getMessages(GroupName, new ServerHelper.Callback(){
						@Override
						public void finished(Exception e, String g) {
							if (g == null || g.equals("null")){
								return;
							}
							System.out.println(g);
							JsonParser jp = new JsonParser();
					        JsonArray items = jp.parse(g).getAsJsonArray();
					        if( items.equals(null) || items.size() == 0 )
					        	return;
					        initListView(items);
						}
					});
					break;
				}
			}
		});
		
		ServerHelper.getMessages(GroupName, new ServerHelper.Callback(){
			@Override
			public void finished(Exception e, String g) {
				if (g == null || g.equals("null")){
					return;
				}
				JsonParser jp = new JsonParser();
		        JsonArray items = jp.parse(g).getAsJsonArray();
		        if( items.equals(null) || items.size() == 0 )
		        	return;
		        initListView(items);
			}
		});
		
		return rootView;
	}

	public void create( String groupname, String message ){
		
		ServerHelper.sendMessage(activity , message, groupname, new ServerHelper.Callback() {
			@Override
			public void finished(Exception e, String resault) {
				Toast.makeText(activity, "Message Sent!", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void initListView(JsonArray items){
		final ListView listview = (ListView) rootView.findViewById(R.id.listView1);

		final ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < items.size(); ++i) {
			list.add(items.get(i).getAsJsonObject().get("message").toString());
		}
		final StableArrayAdapter adapter = new StableArrayAdapter(activity,
				android.R.layout.simple_list_item_1, list);
		listview.setAdapter(adapter);
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