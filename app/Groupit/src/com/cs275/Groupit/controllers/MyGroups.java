package com.cs275.Groupit.controllers;

import com.cs275.Groupit.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyGroups extends Controller{
	public MyGroups(Activity dashboard) {
		
	}

	@Override
	public View inflate(LayoutInflater inflator, ViewGroup container) {
		rootView = inflator.inflate(R.layout.fragment_dashboard,
				container, false);
		TextView v = (TextView)(rootView.findViewById(R.id.name));
		v.setText("Section 1");
		return rootView;
	}

}
