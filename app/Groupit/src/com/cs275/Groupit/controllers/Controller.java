package com.cs275.Groupit.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class Controller {
	public Controller() {}
	protected View rootView;
	public abstract View inflate(LayoutInflater inflator,  ViewGroup container);
}
