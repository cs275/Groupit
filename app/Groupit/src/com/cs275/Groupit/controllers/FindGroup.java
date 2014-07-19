package com.cs275.Groupit.controllers;

import com.cs275.Groupit.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FindGroup extends Controller {

	public FindGroup(Activity dash) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View inflate(LayoutInflater inflator, ViewGroup container) {
		rootView = inflator.inflate(R.layout.find_group,
				container, false);
		return rootView;
	}

}
