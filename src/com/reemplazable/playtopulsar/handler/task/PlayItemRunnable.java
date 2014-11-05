package com.reemplazable.playtopulsar.handler.task;

import java.util.Map;

import com.reemplazable.playtopulsar.handler.URLFactory.Site;

public class PlayItemRunnable extends XbmcConnection implements Runnable {

	private String uriString;

	public PlayItemRunnable(Map<String,?> params, String uriString) {
		super(params);
		this.uriString = uriString;
	}

	@Override
	public void run() {
		sendMessage("{\"jsonrpc\": \"2.0\", \"method\": \"Player.Open\", \"params\":{\"item\" :{ \"file\" : \"" + Site.torrent.pluginURL + getPluginUri() + "\" }}, \"id\" :1}");
	}
	
	private String getPluginUri() {
		return uriString;
	}

}
