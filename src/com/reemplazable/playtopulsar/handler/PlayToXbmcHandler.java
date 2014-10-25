package com.reemplazable.playtopulsar.handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import com.reemplazable.playtopulsar.PlayToPulsarActivity;
import com.reemplazable.playtopulsar.handler.URLFactory.Site;
import com.reemplazable.playtopulsar.handler.task.PlayItemRunnable;
import com.reemplazable.playtopulsar.handler.task.PlayToXbmcRunnable;
import com.reemplazable.playtopulsar.handler.task.StopPlayerRunnable;

public class PlayToXbmcHandler extends Handler {

	public enum PlayToXbmcActivityMessage {
		xbmcStopPlayer, xbmcPlayFile;
	}
		
	private ExecutorService executor;
	private SharedPreferences prefs;
	private Uri uri;
	private Site site;
	
	public PlayToXbmcHandler(PlayToPulsarActivity activity) {
		executor = Executors.newFixedThreadPool(1);
		prefs = PreferenceManager.getDefaultSharedPreferences(activity);
	}
	
	@Override
	public void handleMessage(Message msg) {
		PlayToXbmcActivityMessage message = PlayToXbmcActivityMessage.values()[msg.what];
		switch (message) {
        case xbmcStopPlayer :
        	this.stopPlayer(msg.getData().getInt(PlayToXbmcActivityMessage.xbmcStopPlayer.name()));
            break;
        case xbmcPlayFile :
        	this.playFile(uri, site);
        	break;
        }
			
        super.handleMessage(msg);
	}
	
	public void playToXbmc(Uri uri, Site site) {
		this.uri = uri;
		this.site = site;
		this.execute(new PlayToXbmcRunnable(getHostDirection(), this));
	}

	private void stopPlayer(int playerId) {
		this.execute(new StopPlayerRunnable(getHostDirection(), playerId));
	}
	
	private void playFile(Uri uri, Site site) {
		this.execute(new PlayItemRunnable(getHostDirection(), uri, site));
	}

	private String getHostDirection() {
		return prefs.getString("pref_host_direction" , "");
	}
	
	private void execute(Runnable runnable) {
		executor.execute(runnable);
	}
	
}
