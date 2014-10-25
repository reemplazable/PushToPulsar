package com.reemplazable.playtopulsar.handler.task;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class XbmcConnection {
	
	protected static final String TAG = "PlayToXBMCActivity:XbmcConnection";
	private DefaultHttpClient httpClient;
	private HttpPost httpPost;

	protected XbmcConnection(String hostDirection) {
		this.httpClient = new DefaultHttpClient();
		String host = "http://" + hostDirection + "/jsonrpc";
		Log.d(TAG, host);
		this.httpPost = new HttpPost(host);
		this.httpPost.setHeader("Accept", "application/json, text/javascript");
		this.httpPost.setHeader("Content-type", "application/json");	
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
