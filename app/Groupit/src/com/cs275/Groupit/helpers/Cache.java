package com.cs275.Groupit.helpers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public final class Cache{
	private HashMap<String, Serializable> cache;
	private Context context;
	private String key;
	public Cache(Context c, String _key){
		context = c;
		key=_key;
		restore();
	}
	public Object get(String key){
		if (cache!=null)
			return cache.get(key);
		return null;
	}
	public void add(String key, Object value){
		cache.put(key, (Serializable) value);
		persist();
	}
	public void persist(){
		new WriteTask(context, key, cache).execute();
	}
	public void restore(){
		try {
			cache = (HashMap<String, Serializable>)new InternalStorage().readObject(key);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (cache==null)
			cache = new HashMap<String, Serializable>();
	}
	public final class InternalStorage{

		private InternalStorage() {}

		public void writeObject(String key, HashMap<String, Serializable> object) throws IOException {
			new WriteTask(context, key, object).execute();
		}

		public Object readObject(String key) throws IOException,
		ClassNotFoundException {
			FileInputStream fis = context.openFileInput(key);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object object = ois.readObject();
			return object;
		}
	}
	
	private class WriteTask extends AsyncTask<String, String, String>{
		Context context;
		String key;
		HashMap<String, Serializable> value;
		public WriteTask(Context c, String _key, HashMap<String, Serializable> _value){
			context=c;
			key=_key;
			value=_value;
		}
		@Override
		protected String doInBackground(String... arg0) {
			try{
				FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(value);
				oos.close();
				fos.close();
			}catch(IOException e){
				e.printStackTrace();
			}
			return "";
		}
	}
	
}


