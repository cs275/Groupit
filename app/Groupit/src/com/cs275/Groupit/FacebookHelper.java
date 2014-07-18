/**
 * 
 */
package com.cs275.Groupit;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.google.gson.Gson;

/**
 * 
 *
 */
public class FacebookHelper {
	
	private static JSONArray groups;
	private static Session session;
	
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
	 */
	public JSONArray getGroups(Context c){
		if (groups!=null) return groups;
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
		String groupsString = sp.getString("groups", null);
		
		if (groupsString==null) return fetchGroups(c);
		
		groups = new Gson().fromJson(groupsString, JSONArray.class);
		return groups;
	}
	
	/**
	 * Fetches the user's group through the facebook API.
	 * @param c
	 * @return
	 */
	private JSONArray fetchGroups(final Context c){
		 new Request(
         	    session,
         	    "/me/groups",
         	    null,
         	    HttpMethod.GET,
         	    new Request.Callback() {
         	        public void onCompleted(Response response) {
         	            /* handle the result */
         	        	if (response.getError()!=null){
							try {
								groups = response.getGraphObject().getInnerJSONObject().getJSONArray("data");
								SharedPreferences prefs = c.getSharedPreferences(
									      "com.example.app", Context.MODE_PRIVATE);
								prefs.edit().putString("groups", new Gson().toJson(groups));
							} catch (JSONException e) {
								e.printStackTrace();
	            	        }
         	        	}
         	        }
         	    }
         	).executeAndWait();
		 	return groups;
	}
}
