package com.reemplazable.playtopulsar.handler.task;

public class StopPlayerRunnable extends XbmcConnection implements Runnable {

	private int playerid;

	public StopPlayerRunnable(String hostDirection, int playerid) {
		super(hostDirection);
		this.playerid = playerid;
	}

	@Override
	public void run() {
		sendMessage("{\"jsonrpc\": \"2.0\", \"method\": \"Player.Stop\", \"params\":{\"playerid\":" + this.playerid +"}, \"id\" : 1}");
	}

}
