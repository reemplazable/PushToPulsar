package com.reemplazable.playtopulsar.handler.task;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public abstract class XbmcHttpResponse implements ResponseHandler<Object> {
	
	private static final String TAG = "PlayToXBMCActivity:XbmcHttpResponse";
	
	protected abstract void handleResult(Object result);

	public HttpResponse handleResponse(HttpResponse response)
			throws ClientProtocolException, IOException {
		Log.d(TAG, "response satus code: " + response.getStatusLine().getStatusCode());
		handleJSONResponse(response);
		return response;
	}
	
	public void handleJSONResponse(HttpResponse response) throws IOException {
		if (response.getStatusLine().getStatusCode() <= 300) {
			String responseBody = EntityUtils.toString(response.getEntity());
			Log.d(TAG, "response entity: " + responseBody);
			try {
				JSONObject jsonObj = new JSONObject(responseBody);
				Object result = jsonObj.get("result");
				String string = result.toString();
				Log.d(TAG, string);
				this.handleResult(result);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
}
