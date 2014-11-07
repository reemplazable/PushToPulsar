package com.reemplazable.playtopulsar.handler;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.os.Message;

import com.reemplazable.playtopulsar.PushEndListener;
import com.reemplazable.playtopulsar.handler.task.PlayItemRunnable;
import com.reemplazable.playtopulsar.handler.task.PlayToXbmcRunnable;
import com.reemplazable.playtopulsar.handler.task.StopPlayerRunnable;

public class PlayToXbmcHandler extends Handler {

	public enum PlayToXbmcActivityMessage {
		xbmcStopPlayer, xbmcPlayFile, xbmcPlaySent;
	}
		
	private ExecutorService executor;
	private Map<String, ?> params;
	private String uriString;
	private PushEndListener pushEndListener;
	
	public PlayToXbmcHandler(PushEndListener pushEndListener, Map<String, ?> params) {
		this.executor = Executors.newFixedThreadPool(1);
		this.params = params;
		this.pushEndListener = pushEndListener;
	}
	
	@Override
	public void handleMessage(Message msg) {
		PlayToXbmcActivityMessage message = PlayToXbmcActivityMessage.values()[msg.what];
		switch (message) {
        case xbmcStopPlayer :
        	this.stopPlayer(msg.getData().getInt(PlayToXbmcActivityMessage.xbmcStopPlayer.name()));
            break;
        case xbmcPlayFile :
        	this.playFile(uriString);
        	break;
        case xbmcPlaySent :
        	pushEndListener.end();
        	break;
        }
			
        super.handleMessage(msg);
	}
	
	public void playToXbmc(String uriString) {
		this.uriString = uriString;
		this.execute(new PlayToXbmcRunnable(params, this));
	}

	private void stopPlayer(int playerId) {
		this.execute(new StopPlayerRunnable(params, playerId));
	}
	
	private void playFile(String uriString) {
		this.execute(new PlayItemRunnable(params, uriString, this));
	}

	private void execute(Runnable runnable) {
		executor.execute(runnable);
	}
	
}
