package com.reemplazable.playtopulsar;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;


public class ClipManager {

		public static String getFromClipboard(ClipboardManager clipboard,
			ContentResolver contentResolver) {
			if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.HONEYCOMB) {
				return getFromClipboardv11(clipboard, contentResolver);
			} else {
				return getFromClipboardv12(clipboard, contentResolver);
			}
		}
		
		@SuppressLint("NewApi")
		private static String getFromClipboardv12(ClipboardManager clipboard,
				ContentResolver contentResolver) {
			ClipData clip = clipboard.getPrimaryClip();
			if (clip != null) {
				ClipData.Item item = clip.getItemAt(0);
				return item.getText().toString();
			}
			return null;
		}
		
		@SuppressWarnings("deprecation")
		@SuppressLint("NewApi")
		private static String getFromClipboardv11(ClipboardManager clipboard,
				ContentResolver contentResolver) {
			if (clipboard.hasText()) {
				return clipboard.getText().toString();
			}
			return null;
		}

		@SuppressWarnings("deprecation")
		public static String getFromClipboard(android.text.ClipboardManager clipboard,
				ContentResolver contentResolver) {
			if (clipboard.hasText()) {
				return clipboard.getText().toString();
			}
			return null;
		}
	
}
