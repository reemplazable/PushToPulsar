package com.reemplazable.torrenttomagnet;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base32;
import org.ardverk.coding.BencodingInputStream;
import org.ardverk.coding.BencodingOutputStream;

import android.net.Uri;
import android.util.Log;

public class TorrentToMagnet {
	
	private static final String TAG = "TorrentToMagnet";
	private MessageDigest sha1Digester = null;
	
	public TorrentToMagnet() throws NoSuchAlgorithmException {
		sha1Digester = getSha1Digester();
	}

	private MessageDigest getSha1Digester()
			throws NoSuchAlgorithmException {
		return MessageDigest.getInstance("SHA1");
	}

	public String toMagnet(Uri torrentFile) throws IOException {
		return toMagnet(torrentFile.getPath());
	}
	public String toMagnet(String torrentFile) throws IOException {
		BencodingInputStream bencodingISToString = null;
		try {
			Log.d(TAG, "File to read: " + torrentFile);
			String hash = this.calculateHash(torrentFile);
			sha1Digester.reset();
			DigestInputStream stream = new DigestInputStream(new BufferedInputStream(new FileInputStream(torrentFile)), sha1Digester);
			bencodingISToString = new BencodingInputStream(stream, true);
			Map<String, ?> torrentMap = (Map<String, ?>) bencodingISToString.readMap();
			return "magnet:?xt=" + "urn:btih:" + hash + "&" + this.createDisplayName(torrentMap) + "&" + this.createLength(torrentMap) + this.createAnounce(torrentMap);
		} catch (IOException e) {
			Log.d(TAG, "File to read error: " + e.getCause());
			e.printStackTrace();
		}
		finally {
			if (bencodingISToString != null) {
				bencodingISToString.close();
			}
		}
		return null;
	}
	
	private String createDisplayName(Map<String, ?> torrentMap) {
		@SuppressWarnings("unchecked")
		Map<String, ?> torrentInfo = (Map<String, ?>) torrentMap.get("info");
		return "dn=" + (String)torrentInfo.get("name");
	}
	
	private String createLength(Map<String, ?> torrentMap) {
		@SuppressWarnings("unchecked")
		Map<String, ?> torrentInfo = (Map<String, ?>) torrentMap.get("info");
		BigInteger length = (BigInteger) torrentInfo.get("length");
		if ( length != null) {
			return "xl=" + length.toString();
		} else {
			return "";
		}
	}
	
	private String createAnounce(Map<String, ?> torrentMap) {
		@SuppressWarnings("unchecked")
		List<List<?>> torrentAnnounce = (List<List<?>>) torrentMap.get("announce-list");
		String announce = "";
		for (List<?> announceObject : torrentAnnounce) {
			announce += "&tr=" + (String)announceObject.get(0);
		}
		return announce;
	}

	@SuppressWarnings("unchecked")
	private String calculateHash(String torrentFile) throws IOException {
		BencodingInputStream bencodingIS = null;
		try {
			sha1Digester.reset();
			DigestInputStream stream = new DigestInputStream(new BufferedInputStream(new FileInputStream(torrentFile)), sha1Digester);
			bencodingIS = new BencodingInputStream(stream, false);
			return mapToHash(((Map<String, ?>) bencodingIS.readMap().get("info")));
		} finally {
			if (bencodingIS != null) {
				bencodingIS.close();
			}
		}
	}

	private String mapToHash(Map<String, ?> map) throws IOException {
		BencodingOutputStream bencodingOS = null;
		try {
			Base32 base32 = new Base32();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			bencodingOS = new BencodingOutputStream(outputStream);
			
			bencodingOS.writeMap(orderInfoMap(map));
			sha1Digester.reset();
			byte[] buffer = null;
			buffer = outputStream.toByteArray();
			byte[] sha1Digest = sha1Digester.digest(buffer);
			return base32.encodeAsString(sha1Digest);
		} finally {
			if (bencodingOS != null) {
				bencodingOS.close();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, ?> orderInfoMap(Map<String, ?> infoMap) {
		Map<String, Object> orderInfoMap = new LinkedHashMap<String, Object>();
		manipulateInfoMap("piece length", orderInfoMap, infoMap);
		manipulateInfoMap("pieces", orderInfoMap, infoMap);
		manipulateInfoMap("private", orderInfoMap, infoMap);
		manipulateInfoMap("name", orderInfoMap, infoMap);
		manipulateInfoMap("name.utf-8", orderInfoMap, infoMap);
		Object lenght = infoMap.get("length");
		if (lenght != null) {
			manipulateInfoMap("length", orderInfoMap, infoMap);
			manipulateInfoMap("md5sum", orderInfoMap, infoMap);
		} else {
			orderInfoMap.put("files", createList((List<Map<String, Object>>)infoMap.get("files")));
		}
		return orderInfoMap;
	}
	
	private List<Object> createList(List<Map<String, Object>> filesList) {
		List<Object> fileOrderedList = new ArrayList<Object>();
		for (Map<String, Object> file : filesList) {
			Map<String, Object> orderInfoMap = new LinkedHashMap<String, Object>();
			manipulateInfoMap("length", orderInfoMap, file);
			manipulateInfoMap("md5sum", orderInfoMap, file);
			manipulateInfoMap("path", orderInfoMap, file);
			fileOrderedList.add(orderInfoMap);
		}
		return fileOrderedList;
	}
	
	private void manipulateInfoMap(String propertyName, Map<String, Object> orderInfoMap, Map<String,?> infoMap) {
		Object property = infoMap.get(propertyName);
		if (property != null) {
			orderInfoMap.put(propertyName, property);
		}
	}

}
