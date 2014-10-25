package com.reemplazable.playtopulsar.handler.task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.reemplazable.playtopulsar.handler.PlayToXbmcHandler;

public class PlayToXbmcRunnable extends XbmcConnection implements Runnable{

	private Handler handler;

	public PlayToXbmcRunnable(String hostDirection, Handler handler) {
		super(hostDirection);
		this.handler = handler;
	}

	@Override
	public void run() {
		XbmcHttpResponse responseHandler = new XbmcHttpResponse() {
			@Override
			protected void handleResult(Object result) {
				Log.d(TAG, result.toString());
				JSONArray arrayResult = (JSONArray) result;
				try {
					for(int i = 0; i < arrayResult.length(); i++) {
						JSONObject player = (JSONObject) arrayResult.get(i);
						Message message = new Message();
						message.what = PlayToXbmcHandler.PlayToXbmcActivityMessage.xbmcStopPlayer.ordinal();
						Bundle data = new Bundle();
						data.putInt(PlayToXbmcHandler.PlayToXbmcActivityMessage.xbmcStopPlayer.name(), player.getInt("playerid"));
						message.setData(data);
						handler.sendMessage(message);
					}
					Message message = new Message();
					message.what = PlayToXbmcHandler.PlayToXbmcActivityMessage.xbmcPlayFile.ordinal();
					handler.sendMessage(message);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
		sendMessage("{\"jsonrpc\": \"2.0\", \"method\": \"Player.GetActivePlayers\", \"id\": 1}", responseHandler);
	}}
