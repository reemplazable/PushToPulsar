package com.reemplazable.playtopulsar.handler;

import android.net.Uri;

public class URLFactory {
	
	public enum Site {
		//possible switch for default torrent handler?
		//torrent("plugin://plugin.video.xbmctorrent/play/", true);
		torrent("plugin://plugin.video.pulsar/play?uri=", true);
		
		public String pluginURL;
		public Boolean encodeURL;
		Site(String pluginURL, Boolean encodeURL) {
			this.pluginURL = pluginURL;
			this.encodeURL = encodeURL;
		}
	}

	public static final Site getURL(Uri uri) {
	    return Site.torrent;
	}
	
}
