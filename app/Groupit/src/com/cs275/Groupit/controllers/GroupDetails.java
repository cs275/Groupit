package com.cs275.Groupit.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cs275.Groupit.R;
import com.cs275.Groupit.helpers.FacebookHelper;
import com.cs275.Groupit.helpers.ServerHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GroupDetails extends Controller{

	Activity activity;
	String groupName;
	
	public GroupDetails(Activity dashboard, String groupName){
		activity = dashboard;
		this.groupName = groupName;
	}
	
	@Override
	public View inflate(LayoutInflater inflator, ViewGroup container) {
		
		rootView = inflator.inflate(R.layout.group_home,
				container, false);
		
		final TextView groupTitle = (TextView)(rootView.findViewById(R.id.groupName));
		final TextView groupDescription = (TextView)(rootView.findViewById(R.id.groupDescription));
		final ListView groupMembers = (ListView)(rootView.findViewById(R.id.memberList));
		final Button joinButton = (Button)(rootView.findViewById(R.id.joinButton));
//		new ServerHelper(activity).getGroup(groupName, new ServerHelper.Callback(){
//			@Override
//			public void finished(Exception e, String g) {
//				System.out.print("g");
//				if (g==null || e!=null || g.equals("null")){
//					groupTitle.setText("Error");
//					return;
//				}
//				JsonObject group = new JsonParser().parse(g).getAsJsonObject();
//				
//				groupTitle.setText(group.get("groupName").getAsString());
//				groupDescription.setText(group.get("description").getAsString());
//				
//				JsonArray members = group.get("members").getAsJsonArray();
//				
//				final ArrayList<String> list = new ArrayList<String>();
//				for (int i = 0; i < members.size(); ++i) {
//					list.add(members.get(i).getAsString());
//				}
//				final StableArrayAdapter adapter = new StableArrayAdapter(activity,
//					android.R.layout.simple_list_item_1, list);
//				groupMembers.setAdapter(adapter);
//				
//			} 
//		});
		
		String g = "{ \"members\": [ \"1\", \"2\", \"3\" ], \"groupName\": \"TestGroupy\", \"description\": \"This is the json I generated to test my page because server stuff not working atm\"}";
		if (g==null || g.equals("null")){
			groupTitle.setText("Error");
			return rootView;
		}
		JsonObject group = new JsonParser().parse(g).getAsJsonObject();
	
		groupTitle.setText(group.get("groupName").getAsString());
		groupDescription.setText(group.get("description").getAsString());
	
		JsonArray members = group.get("members").getAsJsonArray();
	
		final ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < members.size(); ++i) {
			list.add(members.get(i).getAsString());
		}
		final StableArrayAdapter adapter = new StableArrayAdapter(activity, android.R.layout.simple_list_item_1, list);
		groupMembers.setAdapter(adapter);
	
		joinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { 
            	joinGroup();
            }
        });
		
		return rootView;
	}
	
	public void joinGroup() {
		
		final String username = FacebookHelper.getUserName(activity);
		ServerHelper.joinGroup(groupName, username, new ServerHelper.Callback() {
			
			@Override
			public void finished(Exception e, String result) {
				if (result.equals("1")){
					Toast.makeText(activity, "Joined "+ groupName, Toast.LENGTH_SHORT).show();
				}
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
