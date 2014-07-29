package com.cs275.Groupit.controllers;

import com.cs275.Groupit.R;
import com.cs275.Groupit.helpers.FacebookHelper;
import com.cs275.Groupit.helpers.ServerHelper;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CreateGroup extends Controller {
	Activity activity;
	public CreateGroup(Activity dash) {
		activity = dash;
	}

	@Override
	public View inflate(LayoutInflater inflator, ViewGroup container) {
		rootView = inflator.inflate(R.layout.create_group,
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
		
		final EditText name = (EditText) rootView.findViewById(R.id.name_select);
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

}
