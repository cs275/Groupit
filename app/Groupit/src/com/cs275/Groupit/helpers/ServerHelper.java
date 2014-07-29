package com.cs275.Groupit.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class ServerHelper {
	private static final String baseUrl = "http://10.0.2.2:3000";
	private static String[] cache = new String[5];

	public ServerHelper() {
		
	}
	
	public static Boolean newGroup(String name, String description, String admin, String location, String category, String user, Callback...call){
		RequestTask task=new RequestTask();
		if (call.length>0)
			task.addCallback(call[0]);
		task.execute(baseUrl+"/newGroup/",
				"name", name,
				"description", description,
				"admin", admin,
				"location", location,
				"category", category,
				"user", user);
		return true;
	}
	public static void getAllGroups(Callback...call){
		int fNum = 0;
		if (cache[fNum]!=null)
			call[fNum].finished(null, cache[fNum]);
		RequestTask task=new RequestTask();
		task.addCache(fNum);
		if (call.length>0)
			task.addCallback(call[0]);
		task.execute(baseUrl+"/allGroups/");
	}
	public static void getAllGroupNames(Callback...call){
		int fNum = 1;
		if (cache[fNum]!=null)
			call[0].finished(null, cache[fNum]);
		RequestTask task=new RequestTask();
		task.addCache(fNum); 
		if (call.length>0)
			task.addCallback(call[0]);
		task.execute(baseUrl+"/allGroupNames/");
	}
	public static void getGroupsForUser(String userName, Callback...call){
		int fNum = 2;
		if (cache[fNum]!=null){
			call[0].finished(null, cache[fNum]);
		}
		RequestTask task=new RequestTask();
		task.addCache(fNum);
		if (call.length>0)
			task.addCallback(call[0]);
		task.execute(baseUrl+"/getGroupsForUser/",
				"user", userName);
	}
	public static void getMembers(String groupName, Callback...call){
		RequestTask task=new RequestTask();
		if (call.length>0)
			task.addCallback(call[0]);
		task.execute(baseUrl+"/getMembers/",
				"group", groupName);
	}
	public static void search(String query, Callback...call){
		RequestTask task=new RequestTask();
		if (call.length>0)
			task.addCallback(call[0]);
		task.execute(baseUrl+"/search/",
				"query", query);
	}
	public static void joinGroup(String groupName, String userName, Callback...call){
		RequestTask task=new RequestTask();
		if (call.length>0)
			task.addCallback(call[0]);
		task.execute(baseUrl+"/joinGroup/",
				"user", userName,
				"group", groupName);
	}
	
	public static class RequestTask extends AsyncTask<String, String, String>{
		private Callback call=null;
		private String finalResult = null;
		private int cacheNum=-1;
		public RequestTask addCallback(Callback _call){
			call = _call;	
			return this;
		}
		public void addCache(int _cacheNum){
			cacheNum=_cacheNum;
		}
		@Override
		protected String doInBackground(String... uri) {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(uri[0]);
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			for (int i=1; i<uri.length; i+=2){
				pairs.add(new BasicNameValuePair(uri[i], uri[i+1]));
			}
			try {
				post.setEntity(new UrlEncodedFormEntity(pairs));
				HttpResponse response = client.execute(post);
				String result = EntityUtils.toString(response.getEntity());
				Log.d("The resault: ", result);
				finalResult = result;
				
			} catch (UnsupportedEncodingException e) { 
				if (call!=null){
					call.finished(e, null);
					
			    }
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				if (call!=null){
					call.finished(e, null);
			    }
				e.printStackTrace();
			} catch (IOException e) {
				if (call!=null){
					call.finished(e, null);
			    }
				e.printStackTrace();
			}
			return "";
		}
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        if (cacheNum!=-1)
	        	cache[cacheNum]=finalResult;
	        if (call!=null){
		        new Handler().post(new Runnable() {
				    @Override
				    public void run() {
				        call.finished(null, finalResult);
				    }
				});
	        }
	        //Do anything with response..
	    }
	}
	


	public static long copy(InputStream is, OutputStream os) {
		int BUFFER_SIZE = 8192;
		byte[] buf = new byte[BUFFER_SIZE];
		long total = 0;
		int len = 0;
		try {
			while (-1 != (len = is.read(buf))) {
				os.write(buf, 0, len);
				total += len;
			}
		} catch (IOException ioe) {
			throw new RuntimeException("error reading stream", ioe);
		}
		return total;
	}
	
	static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	// The callback interface
	public interface Callback {
		void finished(Exception e, String resault);
	}


}
