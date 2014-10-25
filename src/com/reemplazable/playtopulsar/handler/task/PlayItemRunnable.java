package com.reemplazable.playtopulsar.handler.task;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.net.Uri;

import com.reemplazable.playtopulsar.handler.URLFactory.Site;

public class PlayItemRunnable extends XbmcConnection implements Runnable {

	private Site site;
	private Uri uri;

	public PlayItemRunnable(String hostDirection, Uri uri, Site site) {
		super(hostDirection);
		this.uri = uri;
		this.site = site;
	}

	@Override
	public void run() {
		sendMessage("{\"jsonrpc\": \"2.0\", \"method\": \"Player.Open\", \"params\":{\"item\" :{ \"file\" : \"" + site.pluginURL + getPluginUri() + "\" }}, \"id\" :1}");
	}
	
	private String getPluginUri() {
		if (site.encodeURL) {
			String encodedUri = "";
			try {
				encodedUri = URLEncoder.encode(uri.toString(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return encodedUri;
		} else {
			return uri.getQueryParameter("v");
		}
	}

}
