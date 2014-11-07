package com.reemplazable.playtopulsar.handler.task;

import java.util.Map;

public class XbmcGetActivePlayersRunnable extends XbmcConnection implements Runnable {

	protected XbmcGetActivePlayersRunnable(Map<String,?> params) {
		super(params);
	}

	@Override
	public void run() {
		sendMessage("{\"jsonrpc\": \"2.0\", \"method\": \"Player.GetActivePlayers\", \"id\": 1}");
	}
	
	

}
