package com.cs275.Groupit.controllers;

import com.cs275.Groupit.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CreateGroup extends Controller {

	public CreateGroup(Activity dash) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View inflate(LayoutInflater inflator, ViewGroup container) {
		rootView = inflator.inflate(R.layout.create_group,
				container, false);
		return rootView;	}

}
