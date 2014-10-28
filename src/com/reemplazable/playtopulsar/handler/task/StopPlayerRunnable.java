package com.reemplazable.playtopulsar.handler.task;

import java.util.Map;

public class StopPlayerRunnable extends XbmcConnection implements Runnable {

	private int playerid;

	public StopPlayerRunnable(Map<String,?> params, int playerid) {
		super(params);
		this.playerid = playerid;
	}

	@Override
	public void run() {
		sendMessage("{\"jsonrpc\": \"2.0\", \"method\": \"Player.Stop\", \"params\":{\"playerid\":" + this.playerid +"}, \"id\" : 1}");
	}

}
