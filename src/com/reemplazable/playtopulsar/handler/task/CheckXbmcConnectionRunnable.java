package com.reemplazable.playtopulsar.handler.task;

import com.reemplazable.playtopulsar.handler.CheckXbmcConnectionActivityHandler;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class CheckXbmcConnectionRunnable extends XbmcConnection implements Runnable {

	private Handler handler;
	
	public CheckXbmcConnectionRunnable(String hostDirection, Handler handler) {
		super(hostDirection);
		this.handler = handler;
	}
	
	@Override
	public void run() {
		final Message message = new Message();
        message.what = CheckXbmcConnectionActivityHandler.CheckXbmcConnectionActivityMessage.xbmcConnection.ordinal();
        XbmcHttpResponse responseHandler = new XbmcHttpResponse() {
        	@Override
        	protected void handleResult(Object result) {
        		String string = result.toString();
				Log.d(TAG, string);
				if ("pong".equals(string)) {
					handler.sendMessage(message);
				}
        	}
		};
		
		sendMessage("{\"jsonrpc\": \"2.0\", \"method\": \"JSONRPC.Ping\", \"id\": 2}", responseHandler);
		
	}
	
}
