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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class CreateGroup extends Controller {
	Activity activity;
	public CreateGroup(Activity dash) {
		activity = dash;
	}

	@Override
	public View inflate(final LayoutInflater inflator, final ViewGroup container) {
		rootView = inflator.inflate(R.layout.create_group,
				container, false);
		
		
		Button button = (Button) rootView.findViewById(R.id.createButton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				container.removeAllViews();
				//new FindGroup(activity).inflate(inflator, container);
				create();
			}
		});
		Button importGroup = (Button) rootView.findViewById(R.id.importButton);
		importGroup.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				RelativeLayout panel = (RelativeLayout) rootView.findViewById(R.id.FBGroupsPanel);
				panel.setVisibility(View.VISIBLE);
				FacebookHelper fb = new FacebookHelper(activity);
				fb.getGroups(new FacebookHelper.Callback() {
					@Override
					public void finished(Object g) {
						final JSONArray groups = (JSONArray)g;
						try{
							Log.d("Array", groups.getJSONObject(1).getString("name"));
							ArrayList<String> list = new ArrayList<String>();
							for (int i = 0; i < groups.length(); ++i) {
								list.add(groups.getJSONObject(i).getString("name"));
							}
							final StableArrayAdapter adapter = new StableArrayAdapter(activity,
									android.R.layout.simple_list_item_1, list);
							ListView listview = (ListView) rootView.findViewById(R.id.FBGroupsList);
							listview.setAdapter(adapter);
							
							listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
								@SuppressLint("NewApi") @Override
								public void onItemClick(AdapterView<?> parent, final View view,
										int position, long id) {
									try {
										fillFields(groups.getJSONObject(position));
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							});
						}catch(JSONException e){
							e.printStackTrace();
						}
					}
				});
			}
		});
		return rootView;
	}
	
	private void fillFields(JSONObject group) throws JSONException{
		Spinner category = (Spinner) rootView.findViewById(R.id.category);
		final EditText name = (EditText) rootView.findViewById(R.id.group_name);
		final EditText description = (EditText) rootView.findViewById(R.id.descriptionField);
	
		name.setText(group.getString("name"));
		
		RelativeLayout panel = (RelativeLayout) rootView.findViewById(R.id.FBGroupsPanel);
		panel.setVisibility(View.GONE);
		
		FacebookHelper fb = new FacebookHelper(activity);
		fb.getGroupDetails(group.getString("id"), new FacebookHelper.Callback() {			
			@Override
			public void finished(Object g) {
				Log.d("Finished", g.toString());
				JSONObject group = (JSONObject)g;
				try {
					description.setText(group.getString("description"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		//https://developers.facebook.com/docs/graph-api/reference/v2.0/group
	}
	
	public void create(){
		Spinner category = (Spinner) rootView.findViewById(R.id.category);
		
		Resources res = activity.getResources();
		String[] categories = res.getStringArray(R.array.category_array);
		
		String selectedCategory = categories[category.getSelectedItemPosition()];
		
		final EditText name = (EditText) rootView.findViewById(R.id.group_name);
		EditText description = (EditText) rootView.findViewById(R.id.descriptionField);
		final String username = FacebookHelper.getUserName(activity);
		ServerHelper.newGroup(name.getText().toString(), description.getText().toString(), username, "", selectedCategory, username, new ServerHelper.Callback() {
			
			@Override
			public void finished(Exception e, String resault) {
				//ServerHelper.joinGroup(name.getText().toString(), username);
				Toast.makeText(activity, "Created Groupit!", Toast.LENGTH_SHORT).show();
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
