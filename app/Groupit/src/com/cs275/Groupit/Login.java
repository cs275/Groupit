/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cs275.Groupit;

import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.R.array;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.model.GraphMultiResult;

import com.google.gson.*;




public class Login extends Activity {
    private static final String URL_PREFIX_FRIENDS = "https://graph.facebook.com/me/friends?access_token=";

    private TextView textInstructionsOrLink;
    private Button buttonLoginLogout;
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    private Request.GraphUserCallback userCallback = new UserRequestCallback();
    Intent i;
    private int count=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        i=new Intent(this, Dashboard.class);
        
        buttonLoginLogout = (Button)findViewById(R.id.buttonLoginLogout);
        textInstructionsOrLink = (TextView)findViewById(R.id.instructionsOrLink);

        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        Session session = Session.getActiveSession();
        
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
            	List<String> publishPermissions = Arrays.asList("public_profile","user_groups");
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback).setPermissions(publishPermissions));
                new FacebookHelper(session);
            }
        }

        //updateView();
    }

    @Override
    public void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(statusCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(statusCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }

    private void updateView() {
        Session session = Session.getActiveSession();
        if (session.isOpened()) {
            
            buttonLoginLogout.setText("Logout");
            Request.newMeRequest(session, userCallback).executeAsync();
            Log.d("Exipre Date: ", session.getExpirationDate().toString());
           
            new Request(
            	    session,
            	    "/me/groups",
            	    null,
            	    HttpMethod.GET,
            	    new Request.Callback() {
            	        public void onCompleted(Response response) {
            	            /* handle the result */
            	        	if (response.getError()!=null)
            	        		Log.d("Error: ",response.getError().toString());
            	         	JSONArray groups;
            	         	String s="\n\nGroups:\n";
							try {
								groups = response.getGraphObject().getInnerJSONObject().getJSONArray("data");

	            	         	for (int i=0; i<groups.length(); i++){
	            	         		s+="     " + groups.getJSONObject(i).getString("name")+"\n";
	            	         	}
	            	         	//i.putExtra("groups", new Gson().toJson(groups));
						        AttemptToGoToNextActivity();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							
	            	        }
							textInstructionsOrLink.setText(textInstructionsOrLink.getText()+s);
							
            	        }
            	    }
            	).executeAsync();

            buttonLoginLogout.setOnClickListener(new OnClickListener() {
                public void onClick(View view) { onClickLogout(); }
            });
        } else {
            textInstructionsOrLink.setText("Click to login");
            buttonLoginLogout.setText("Login");
            buttonLoginLogout.setOnClickListener(new OnClickListener() {
                public void onClick(View view) { onClickLogin(); }
            });
        }
    }

    private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }
    }

    private void onClickLogout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
        	Log.d("Permissions: ",session.getPermissions().toString());
        	Log.d("Permissions: ",session.getAccessToken());
        	
            updateView();
        }
    }
    private class UserRequestCallback implements Request.GraphUserCallback{
		@Override
		public void onCompleted(GraphUser user, Response response) {
			// TODO Auto-generated method stub
			Log.d("Your name is: ",user.getFirstName());
			textInstructionsOrLink.setText("Hello "+user.getFirstName()+textInstructionsOrLink.getText());
			AttemptToGoToNextActivity();
			//i.putExtra("user", new Gson().toJson(user));
			//Log.d("From Callback.response: ",response.getGraphObject().asMap().keySet().toString());
		}
    }
    private Boolean AttemptToGoToNextActivity(){
    	count++;
    	Boolean next = count==2;
    	if (next)
    		startActivity(i);
    	return next;
    }
}
