/**
 * 
 */
package com.cs275.Groupit.helpers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

/**
 * 
 *
 */
public class FacebookHelper {
	
	private static JSONArray groups;
	private static Session session;
	private static Map<String, Object> user;
	private static Cache cache;
	private static Context context;
	//private final CountDownLatch loginLatch = new CountDownLatch (1);
	
	public FacebookHelper(Context _context){
		context = _context;
		cache = new Cache(context, "FacebookCache");
		session = getSession();
	}
	
	/**
	 * 
	 */ 
	public FacebookHelper(Session _session, Context _context) {
		context = _context;
		cache = new Cache(context, "FacebookCache");
		session=_session;
	}
	
	public static Session getSession(){
		if (session!=null)
			return session;
		return Session.getActiveSession();
	}
	
	/**
	 * Returns the groups that the user is subscribed to.
	 * @param c
	 * The context of the calling activity.
	 * @return
	 * A static version of the groups if available, or  sharedPreference if available, else, it will go to Facebook and fetch the data.
	 * Note if this is the first time getting the users groups, then it will return null.
	 */
	public void getGroups(Callback...call){
		if (cache.get("groups")!=null) {  
			if (call!=null)
				call[0].finished(((SerializableJSONArray)cache.get("groups")).getJSONArray());
		}else{
			fetchGroups(call);
		}
	}
	
	public JSONObject getGroupDetails(String id, final Callback...call){
		new Request(
			    session,
			    "/"+id,
			    null,
			    HttpMethod.GET,
			    new Request.Callback() {
			        public void onCompleted(Response response) {
			        	GraphObject obj = response.getGraphObject();
						Object thing = obj.getInnerJSONObject();
						call[0].finished(thing);
			        }
			    }
			).executeAsync();
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getUser(Callback... call){
		if (user!=null)
			return user;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		String userString = sp.getString("user", null);
		if (userString==null) {
			fetchUser(call);
			return user;
		}
		user = new Gson().fromJson(userString, Map.class);
		return user;
	}
	
	public static String getUserName(Context c){
		SharedPreferences prefs = c.getSharedPreferences(
			      "com.cs275.Groupit", Context.MODE_PRIVATE);
		String username = prefs.getString("username", null);
		return username;
		/*
		if (result==null){
			try {
				loginLatch.await ();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return user.get("name").toString(); */
	}
	
	/**
	 * Fetches the user's group through the facebook API.
	 * @param c
	 * @return
	 */
	public void fetchGroups(final Callback... call){
		new Request(
				session,
         	    "/me/groups",
         	    null,
         	    HttpMethod.GET,
         	    new Request.Callback() {
         	        public void onCompleted(Response response) {
         	            /* handle the result */
         	        	if (response.getError()==null){
							try {
								groups = response.getGraphObject().getInnerJSONObject().getJSONArray("data");
								cache.add("groups", new SerializableJSONArray(groups));
								if (call.length>0)call[0].finished(groups);
							} catch (JSONException e) {
								e.printStackTrace();
	            	        }
         	        	}
         	        }
         	    }
         	).executeAsync();
	}
	
	public void fetchUser(final Callback... call){
		if(!session.isOpened())
			Log.d("Error", "Not opened!");
	
		Request.newMeRequest(session, new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser _user, Response response) {
				user=_user.asMap();
				//loginLatch.countDown ();
				SharedPreferences prefs = context.getSharedPreferences(
					      "com.cs275.Groupit", Context.MODE_PRIVATE);
				prefs.edit().putString("user", new Gson().toJson(user));
				if (call.length>0)call[0].finished(user);
			}
		}).executeAsync();
	}
	
	// The callback interface
	public interface Callback {
	    void finished(Object g);
	}
}
