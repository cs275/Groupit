package com.cs275.Groupit.controllers;

import org.json.JSONArray;
import org.json.JSONException;

import com.cs275.Groupit.R;
import com.cs275.Groupit.helpers.FacebookHelper;
import com.cs275.Groupit.helpers.ServerHelper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyGroups extends Controller{
	Activity activity;
	public MyGroups(Activity dashboard) {
		activity = dashboard;
	}

	@Override
	public View inflate(LayoutInflater inflator, ViewGroup container) {
		rootView = inflator.inflate(R.layout.fragment_dashboard,
				container, false);
		
		final TextView v = (TextView)(rootView.findViewById(R.id.name));
		//Log.d("Username: ", new FacebookHelper(FacebookHelper.getSession(activity)).getUserName(activity));
		ServerHelper.getGroupsForUser(FacebookHelper.getUserName(activity), new ServerHelper.Callback(){
			@Override
			public void finished(Exception e, String g) {
				
				if (e!=null || g.equals("null")){
					v.setText("You currently have no groups");
					return;
				}
				JsonArray groups = new JsonParser().parse(g).getAsJsonArray();
				String content = "My Groups: \n    ";
				for (int i=0; i<groups.size(); i++){
					content+=groups.get(i).getAsString()+"\n    ";
				}
				v.setText(content);
			} 
		}); 
		
		return rootView;
	}
}
