package com.reemplazable.playtopulsar.handler.task;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Base64;
import android.util.Log;

public class XbmcConnection {
	
	protected static final String TAG = "PlayToXBMCActivity:XbmcConnection";
	private DefaultHttpClient httpClient;
	private HttpPost httpPost;
	

	protected XbmcConnection(Map<String, ?> params) {
		String hostDirection = (String) params.get("pref_host_direction");
		Boolean userAuthEnabled = (Boolean) params.get("pref_switch_auth");
		this.httpClient = new DefaultHttpClient();
		String host = "http://" + hostDirection + "/jsonrpc";
		Log.d(TAG, host);
		this.httpPost = new HttpPost(host);
		this.httpPost.setHeader("Accept", "application/json, text/javascript");
		this.httpPost.setHeader("Content-type", "application/json");
		if (userAuthEnabled) {
			String username = (String) params.get("pref_username");
			String password = (String) params.get("pref_password");
			if (username != null && username.length() > 0 && password != null && password.length() > 0){
				String auth = Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP);
				Log.d(TAG, "auth: " + auth);
				this.httpPost.setHeader("Authorization", "Basic " + auth);
			}
		}
	}
	
	void sendMessage(String jsonMmessage) {
		sendMessage(jsonMmessage, getEmptyHandler());
	}
	
	void sendMessage(String jsonMmessage, ResponseHandler<Object> response) {
	    //convert parameters into JSON object
	    //JSONObject holder = getJsonObjectFromMap(params);
	    StringEntity se = null;
		try {
			se = new StringEntity(jsonMmessage);
			httpPost.setEntity(se);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	    this.httpPost.setEntity(se);

	    try {
	    	Log.d(TAG, "execute post: " + jsonMmessage);
			httpClient.execute(httpPost, response);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
		}
	}
	
	private ResponseHandler<Object> getEmptyHandler() {
		return new ResponseHandler<Object>() {
			@Override
			public Object handleResponse(HttpResponse response)
					throws ClientProtocolException, IOException {
				Log.d(TAG, response.toString());
				return response;
			}
		};
	}

}
