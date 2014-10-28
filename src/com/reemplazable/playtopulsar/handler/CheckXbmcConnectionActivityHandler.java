package com.reemplazable.playtopulsar.handler;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import com.reemplazable.playtopulsar.PlayToPulsarActivity;
import com.reemplazable.playtopulsar.handler.task.CheckXbmcConnectionRunnable;

public class CheckXbmcConnectionActivityHandler extends Handler{

	public enum CheckXbmcConnectionActivityMessage {
		xbmcConnection
	}
	
	private PlayToPulsarActivity activity;
	private ExecutorService executor;
	private Map<String, ?> params;
	
	public CheckXbmcConnectionActivityHandler(PlayToPulsarActivity activity) {
		this.activity = activity;
		executor = Executors.newFixedThreadPool(1);
		params = PreferenceManager.getDefaultSharedPreferences(activity).getAll();
		
	}
	
	@Override
	public void handleMessage(Message msg) {
		CheckXbmcConnectionActivityMessage message = CheckXbmcConnectionActivityMessage.values()[msg.what];
		switch (message) {
        case xbmcConnection :
        	activity.activateConnection();
            break;
        }
        super.handleMessage(msg);
	}
	
	public void checkConnection() {
		CheckXbmcConnectionRunnable connection = null;
		connection = new CheckXbmcConnectionRunnable(params, this);
		executor.execute(connection);
	}

}
