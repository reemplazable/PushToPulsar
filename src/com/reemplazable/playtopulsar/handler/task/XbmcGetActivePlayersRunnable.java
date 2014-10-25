package com.reemplazable.playtopulsar.handler.task;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

public class XbmcGetActivePlayersRunnable extends XbmcConnection implements Runnable {

	protected XbmcGetActivePlayersRunnable(String hostDirection) {
		super(hostDirection);
	}

	@Override
	public void run() {
		ResponseHandler<Object> response = new ResponseHandler<Object>() {
			@Override
			public Object handleResponse(HttpResponse response)
					throws ClientProtocolException, IOException {
				return response;
			}
		};
		sendMessage("{\"jsonrpc\": \"2.0\", \"method\": \"Player.GetActivePlayers\", \"id\": 1}", response);
	}
	
	

}
