package com.reemplazable.playtopulsar;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.reemplazable.playtopulsar.handler.CheckXbmcConnectionActivityHandler;
import com.reemplazable.playtopulsar.handler.PlayToXbmcHandler;
import com.reemplazable.torrenttomagnet.TorrentToMagnet;

public class PlayToPulsarActivity extends ActionBarActivity implements PushEndListener{

	private CheckXbmcConnectionActivityHandler checkXbmcConnectionhandler;
	private PlayToXbmcHandler playToXbmcHandler;
	private static final String TAG = "PlayToPulsarActivity";
	private String uriString = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_to_pulsar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.play_to_pulsar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            launchSettingsActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String host = preferences.getString("pref_host_direction", "");
		this.checkXbmcConnectionhandler = new CheckXbmcConnectionActivityHandler(this);
		Log.d(TAG, "host: " + host);
		getUriFromIntent();
		enablePushButton();
		if (host != null && host.length() <= 0) {
			showSettings();
		} else {
			checkXbmcConnectionhandler.checkConnection();
			this.playToXbmcHandler = new PlayToXbmcHandler(this, preferences.getAll());
			if (uriString != null) {
				if (getAutomaticPlayToXbmc()) {
					this.finish();
				}
			}
		}
	}

	private void getUriFromIntent() {
		Uri uri = getUri(getIntent());
		if (uri != null) {
			try {
				String uriTextString = transformMagnet(uri);
				if (uriTextString != null && isTorrent(uriTextString)) {
					setUri(uriTextString);
				} else {
					showErrorNoTorrent();
				}
			} catch (IOException e) {
				showError(e, uri);
			} catch (NoSuchAlgorithmException e) {
				showError(e, uri);
			}
		} else {
			this.uriString = null;
		}
	}

	private void showErrorNoTorrent() {
		Toast toast = Toast.makeText(getApplicationContext(), R.string.toast_not_a_torrent, Toast.LENGTH_LONG);
		toast.show();		
	}

	private void setUri(String uriTextString)
			throws UnsupportedEncodingException {
		TextView text = (TextView) findViewById(R.id.textXBMCUri);
		text.setText(uriTextString);
		this.uriString = URLEncoder.encode(uriTextString, "UTF-8");
	}

	private void showSettings() {
		Toast toast = Toast.makeText(getApplicationContext(), R.string.toast_need_host, Toast.LENGTH_LONG);
		toast.show();
		launchSettingsActivity();
		checkXbmcConnectionhandler.checkConnection();
	}

	private void showError(Exception e, Uri uri) {
		showError(e, uri.toString());
	}
	
	private void showError(Exception e, String uri) {
		Toast toast = Toast.makeText(getApplicationContext(), "Error on: " + uri.toString() + " " + e.getMessage(), Toast.LENGTH_LONG);
		toast.show();
	}

	private String transformMagnet(Uri uri)
			throws NoSuchAlgorithmException, IOException {
		String magnet = null;
		if (uri.getScheme() != null && uri.getScheme().contains("file")) {
			magnet = transformFileToMagnet(uri);
		} else {
			magnet = transformUriToMagnet(uri);
		}
		return magnet;
	}

	private String transformUriToMagnet(Uri uri) {
		return uri.toString();
	}

	private String transformFileToMagnet(Uri uri) throws NoSuchAlgorithmException, IOException {
		TorrentToMagnet torrentToMagnet = new TorrentToMagnet();
		return torrentToMagnet.toMagnet(uri);
	}
	
	private Uri getUri(Intent intent) {
		String action = intent.getAction();
		Uri uri = null;
		if (Intent.ACTION_VIEW.equals(action)) {
			uri = intent.getData();
		}
		Bundle extras = intent.getExtras();
		if (Intent.ACTION_SEND.equals(action) && !extras.isEmpty()) {
			String uriText = extras.getString(Intent.EXTRA_TEXT);
			Log.d(TAG, "dataIntent: key: " + Intent.EXTRA_TEXT + " value: " + uriText);
			uri = Uri.parse(uriText);
			}
		return uri;
	}

	private void launchSettingsActivity() {
		Intent i = new Intent(this, SettingsActivity.class);
		startActivity(i);
	}
	
	public void activateConnection() {
		ToggleButton xbmcStatus = (ToggleButton) findViewById(R.id.toggleButton1);
		if (xbmcStatus != null) {
			xbmcStatus.setChecked(true);
		}
		if (uriString != null) {
			Button button = (Button) findViewById(R.id.sendToXBMCButton);
			button.setEnabled(true);
			if (getAutomaticPlayToXbmc()) {
				Intent intent = new Intent(this, PlayToPulsarService.class);
				intent.putExtra(PlayToPulsarService.uriString, uriString);
				this.startService(intent);
			}
		}
	}
	
	public void deactivateConnection() {
		ToggleButton xbmcStatus = (ToggleButton) findViewById(R.id.toggleButton1);
		if (xbmcStatus != null) {
			xbmcStatus.setChecked(false);
		}
		Button button = (Button) findViewById(R.id.sendToXBMCButton);
		button.setEnabled(false);
	}

	private void enablePushButton() {
		if (uriString != null) {
			Button button = (Button) findViewById(R.id.sendToXBMCButton);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					playToXbmcHandler.playToXbmc(uriString);
				}
			});
		}
	}
	
	public void copyFromClipboard(View v) {
		String uri = null;
		try {
			uri = getFromClipboard();
			if (uri != null) {
				if (isTorrent(uri)) {
					setUri(uri);
					enablePushButton();
					checkXbmcConnectionhandler.checkConnection();
				} else {
					showErrorNoTorrent();
				}
			} else {
				showErrorNoTorrent();
			}
		} catch (UnsupportedEncodingException e) {
			showError(e, uri);
		}
	}
	
	private boolean isTorrent(String uri) {
		return uri != null && (uri.contains(".torrent") || uri.contains("magnet:")); 
	}

	@SuppressWarnings("deprecation")
	private String getFromClipboard() {
		if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
			return ClipManager.getFromClipboard((android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE), getContentResolver());
		} else {
			return ClipManager.getFromClipboard((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE), getContentResolver());
			
		}
	}
	
	public void end() {
		Boolean automaticPlayToXbmc = getAutomaticPlayToXbmc();
		if (automaticPlayToXbmc) {
			this.finish();
		}
	}

	private Boolean getAutomaticPlayToXbmc() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		Boolean automaticPlayToXbmc = preferences.getBoolean("pref_switch_direct_submit", false);
		return automaticPlayToXbmc;
	}

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_play_to_pulsar, container, false);
            return rootView;
        }
    }

}
