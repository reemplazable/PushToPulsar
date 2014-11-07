package com.reemplazable.playtopulsar;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.reemplazable.playtopulsar.handler.PlayToXbmcHandler;

public class PlayToPulsarService extends Service implements PushEndListener {

	private PlayToXbmcHandler playToXbmcHandler;
	public static final String uriString = "uriString"; 

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		this.playToXbmcHandler = new PlayToXbmcHandler(this, preferences.getAll());
		playToXbmcHandler.playToXbmc(intent.getStringExtra(PlayToPulsarService.uriString));
		return Service.START_NOT_STICKY;
	}

	@Override
	public void end() {
		stopSelf();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
