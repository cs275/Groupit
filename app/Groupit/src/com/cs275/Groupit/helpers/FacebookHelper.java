/**
 * 
 */
package com.cs275.Groupit.helpers;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.gson.Gson;

/**
 * 
 *
 */
public class FacebookHelper {
	
	private static JSONArray groups;
	private static Session session;
	private static Map<String, Object> user;
	
	/**
	 * 
	 */
	public FacebookHelper(Session _session) {
		session=_session;
	}
	
	public static Session getSession(Context c){
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
	public JSONArray getGroups(Context c, Callback...call){
		if (groups!=null) { 
			if (call!=null)
				call[0].finished(groups);
			return groups;
		}
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
		String groupsString = sp.getString("groups", null);
		
		if (groupsString==null) {
			fetchGroups(c, call);
			return groups;
		}
		
		groups = new Gson().fromJson(groupsString, JSONArray.class);
		if (call!=null)
			call[0].finished(groups);
		return groups;
	}
	@SuppressWarnings("unchecked")
	public Map<String, Object> getUser(Context c, Callback... call){
		if (user!=null)
			return user;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
		String userString = sp.getString("user", null);
		if (userString==null) {
			fetchUser(c, call);
			return user;
		}
		user = new Gson().fromJson(userString, Map.class);
		return null;
	}
	
	/**
	 * Fetches the user's group through the facebook API.
	 * @param c
	 * @return
	 */
	public void fetchGroups(final Context c, final Callback... call){
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
								SharedPreferences prefs = c.getSharedPreferences(
									      "com.cs275.Groupit", Context.MODE_PRIVATE);
								prefs.edit().putString("groups", new Gson().toJson(groups));
								if (call.length>0)call[0].finished(groups);
							} catch (JSONException e) {
								e.printStackTrace();
	            	        }
         	        	}
         	        }
         	    }
         	).executeAsync();
	}
	
	public void fetchUser(final Context c, final Callback... call){
		Request.newMeRequest(session, new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser _user, Response response) {
				user=_user.asMap();
				SharedPreferences prefs = c.getSharedPreferences(
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
