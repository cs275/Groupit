/**
 * Author: Andrew Shidel
 * Description: Searches a dropbox account for the file "/move/__list", and downloads the listed files to the users local machine.
 * Usage:
 * 		Compile:	"make compile"  or  "javac -cp temboo.jar Dropbox.java"
 * 		Run:		"make run"  or  "java -cp .:./temboo.jar Dropbox"
 */

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.codec.binary.Base64;

import com.temboo.Library.Dropbox.FilesAndMetadata.GetFile;
import com.temboo.Library.Dropbox.FilesAndMetadata.GetFile.GetFileInputSet;
import com.temboo.Library.Dropbox.FilesAndMetadata.GetFile.GetFileResultSet;
import com.temboo.Library.Dropbox.OAuth.FinalizeOAuth;
import com.temboo.Library.Dropbox.OAuth.FinalizeOAuth.*;
import com.temboo.Library.Dropbox.OAuth.InitializeOAuth;
import com.temboo.Library.Dropbox.OAuth.InitializeOAuth.*;
import com.temboo.core.TembooException;
import com.temboo.core.TembooSession;


public class Dropbox {
	private final static String key = "5bhl43fqyo8mk5e";
	private final static String secret = "tarn5u5rt8pylpk";
	public static void main(String[] args) throws TembooException, IOException {

		//Get the temboo session
		TembooSession session = getSession();

		//Initialize oAuth
		String res[] = initOauth(session);

		//Open the Authorization URL in the browser.
		openInBrowser(res[0]);

		//Finalize the authorization
		String tokens[] = finalizeOauth(session, res[1], res[2]);

		//Download the __list file.
		String __list = new String(Base64.decodeBase64(get__list(session, tokens[0], tokens[1])));

		//Split the items along whitespace.
		String[] items = __list.split("\\s+");

		//Download and write each item.
		String file="";
		for (int i=0; i<items.length; i+=2){
			file=getItem(session, items[i], tokens[0], tokens[1]);
			byte[] data = Base64.decodeBase64(file);
			System.out.println("Writting: "+items[i]);
			try (OutputStream stream = new FileOutputStream(items[i+1]+"/"+items[i])) {
				stream.write(data);
			}
		}
		System.out.println("Finished!");
	}

	//Creates a new Temboo session.
	private static TembooSession getSession() throws TembooException{
		return new TembooSession("andrewshidel", "myFirstApp", "5b6846d3f0e84d3aa8aa566a774a6a05");
	}
	//Starts the oauth process.
	//Returns: [authorization url, callbackID, token secret]
	private static String[] initOauth(TembooSession session) throws TembooException{
		InitializeOAuth initializeOAuthChoreo = new InitializeOAuth(session);

		// Get an InputSet object for the choreo
		InitializeOAuthInputSet initializeOAuthInputs = initializeOAuthChoreo.newInputSet();

		// Set inputs
		initializeOAuthInputs.set_DropboxAppSecret(secret);
		initializeOAuthInputs.set_DropboxAppKey(key);

		// Execute Choreo
		InitializeOAuthResultSet initializeOAuthResults = initializeOAuthChoreo.execute(initializeOAuthInputs);

		String arr[] = new String[3];
		arr[0]=initializeOAuthResults.get_AuthorizationURL();
		arr[1]=initializeOAuthResults.get_CallbackID();
		arr[2]=initializeOAuthResults.get_OAuthTokenSecret();
		return arr;
	}

	//Tries to open a URL in the default we browser
	//If it fails, the user is prompted to navigate manually.
	private static void openInBrowser(String url){
		try {
			//Try to open the url in the default browser.
			java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
		}catch (java.io.IOException e) {
			System.out.println(e.getMessage());
		}catch(java.awt.HeadlessException e){
			//The browser could not be opened. Prompt the user to manually navigate.
			System.out.println("Please navigate to the following page in any web browser: \n"+url);
		}
	}
	//Finish the Oauth process.
	//Returns: [access token, access token secret]
	private static String[] finalizeOauth(TembooSession session, String callback, String tokenSecret) throws TembooException{
		FinalizeOAuth finalizeOAuthChoreo = new FinalizeOAuth(session);

		// Get an InputSet object for the choreo
		FinalizeOAuthInputSet finalizeOAuthInputs = finalizeOAuthChoreo.newInputSet();

		// Set inputs
		finalizeOAuthInputs.set_CallbackID(callback);
		finalizeOAuthInputs.set_DropboxAppKey(key);
		finalizeOAuthInputs.set_DropboxAppSecret(secret);
		finalizeOAuthInputs.set_OAuthTokenSecret(tokenSecret);


		// Execute Choreo
		FinalizeOAuthResultSet finalizeOAuthResults = finalizeOAuthChoreo.execute(finalizeOAuthInputs);

		return new String[]{finalizeOAuthResults.get_AccessToken(), finalizeOAuthResults.get_AccessTokenSecret()};
	}
	//Downloads the "move/__list file"
	private static String get__list(TembooSession session, String accessToken, String accesstokenSecret) throws TembooException{
		return getFile(session, "/move/__list", accessToken, accesstokenSecret, false);
	}
	//Downloads any file from dropbox.
	private static String getItem(TembooSession session, String location, String accessToken, String accesstokenSecret) throws TembooException{
		return getFile(session, location, accessToken, accesstokenSecret, true);
	}
	//Gets a file from Dropbox.
	private static String getFile(TembooSession session, String path, String accessToken, String accesstokenSecret, Boolean base64) throws TembooException{
		GetFile getFileChoreo = new GetFile(session);

		// Get an InputSet object for the choreo
		GetFileInputSet getFileInputs = getFileChoreo.newInputSet();

		// Set inputs
		getFileInputs.set_AccessToken(accessToken);
		getFileInputs.set_AppSecret(secret);
		getFileInputs.set_Root("dropbox");
		getFileInputs.set_AccessTokenSecret(accesstokenSecret);
		getFileInputs.set_AppKey(key);
		getFileInputs.set_Path(path);
		getFileInputs.set_EncodeFileContent(base64);

		// Execute Choreo
		GetFileResultSet getFileResults = getFileChoreo.execute(getFileInputs);
		return getFileResults.get_Response();
	}

}
