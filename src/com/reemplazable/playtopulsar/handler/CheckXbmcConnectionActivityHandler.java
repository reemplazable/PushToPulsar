package com.reemplazable.playtopulsar.handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.SharedPreferences;
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
	private SharedPreferences prefs;
	
	public CheckXbmcConnectionActivityHandler(PlayToPulsarActivity activity) {
		this.activity = activity;
		executor = Executors.newFixedThreadPool(1);
		prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		
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
		executor.execute(new CheckXbmcConnectionRunnable(prefs.getString("pref_host_direction" , ""), this));
	}

}
