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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Messaging extends Controller{
	Activity activity;
	public Messaging(Activity dashboard) {
		activity = dashboard;
	}

	@Override
	public View inflate(LayoutInflater inflator, ViewGroup container) {
		rootView = inflator.inflate(R.layout.message_center,
				container, false);
		
		Button button = (Button) rootView.findViewById(R.id.createButton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				create();
			}
		});
		
		return rootView;
	}
	
	public void create(){
		Spinner category = (Spinner) rootView.findViewById(R.id.category);
		
		Resources res = activity.getResources();
		String[] categories = res.getStringArray(R.array.category_array);
		
		String selectedCategory = categories[category.getSelectedItemPosition()];
		
		final EditText name = (EditText) rootView.findViewById(R.id.group_name);
		EditText message = (EditText) rootView.findViewById(R.id.SendMessage);
		ServerHelper.sendMessage(activity , message.toString(), name.toString(), new ServerHelper.Callback() {
			
			@Override
			public void finished(Exception e, String resault) {
				Toast.makeText(activity, "Message Sent!", Toast.LENGTH_SHORT).show();
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