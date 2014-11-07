package com.reemplazable.playtopulsar.handler.task;

import java.util.Map;

import android.os.Handler;
import android.os.Message;

import com.reemplazable.playtopulsar.handler.PlayToXbmcHandler;
import com.reemplazable.playtopulsar.handler.URLFactory.Site;

public class PlayItemRunnable extends XbmcConnection implements Runnable {

	private String uriString;
	private Handler handler;

	public PlayItemRunnable(Map<String,?> params, String uriString, Handler handler) {
		super(params);
		this.uriString = uriString;
		this.handler = handler;
	}

	@Override
	public void run() {
		XbmcHttpResponse responseHandler = new XbmcHttpResponse() {
			@Override
			protected void handleResult(Object result) {
				Message message = new Message();
				message.what = PlayToXbmcHandler.PlayToXbmcActivityMessage.xbmcPlaySent.ordinal();
				handler.sendMessage(message);
			}
			
		};
		sendMessage("{\"jsonrpc\": \"2.0\", \"method\": \"Player.Open\", \"params\":{\"item\" :{ \"file\" : \"" + Site.torrent.pluginURL + getPluginUri() + "\" }}, \"id\" :1}", responseHandler);
	}
	
	private String getPluginUri() {
		return uriString;
	}

}
