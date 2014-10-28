package com.reemplazable.playtopulsar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.reemplazable.playtopulsar.handler.URLFactory;

public class PlayToPulsarActivity extends ActionBarActivity {

	private CheckXbmcConnectionActivityHandler checkXbmcConnectionhandler;
	private PlayToXbmcHandler playToXbmcHandler;
	private static final String TAG = "PlayToPulsarActivity";
	
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
    
//	@Override
//	protected void onPostCreate(Bundle savedInstanceState) {
//		super.onPostCreate(savedInstanceState);
//	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String host = preferences.getString("pref_host_direction", "");
		Log.d(TAG, "host: " + host);
		if (host.length() <= 0) {
			Toast toast = Toast.makeText(getApplicationContext(), R.string.toast_need_host, Toast.LENGTH_LONG);
			toast.show();
			launchSettingsActivity();
		} else {
			this.checkXbmcConnectionhandler = new CheckXbmcConnectionActivityHandler(this);
			checkXbmcConnectionhandler.checkConnection();
			this.playToXbmcHandler = new PlayToXbmcHandler(this);
			Uri uri = getUri(getIntent());
			if (uri != null) {
				TextView text = (TextView) findViewById(R.id.textXBMCUri);
				text.setText(uri.toString());
			}
		}
	}
	
	private Uri getUri(Intent intent) {
		String action = intent.getAction();
		Uri uri = null;
		if (Intent.ACTION_SEND.equals(action)) {
			String stringExtra = intent.getStringExtra(Intent.EXTRA_TEXT);
			uri = Uri.parse(getSharedText(stringExtra));
		}
		if (Intent.ACTION_VIEW.equals(action)) {
			uri = intent.getData();
		}
		return uri;
	}
	
	public String getSharedText(String stringExtra) {
		Pattern pattern = Pattern.compile("(http|https).*");
		Log.d(TAG, "StringExtra: " + stringExtra);
		Matcher matcher = pattern.matcher(stringExtra);
		matcher.find();
		return matcher.group(0);
	}

	private void launchSettingsActivity() {
		Intent i = new Intent(this, SettingsActivity.class);
		startActivity(i);
	}
	
	public void activateConnection() {
		ToggleButton xbmcStatus = (ToggleButton) findViewById(R.id.toggleButton1);
		xbmcStatus.setChecked(true);
		final Uri uri = getUri(getIntent());
		if (uri != null) {
			Button button = (Button) findViewById(R.id.sendToXBMCButton);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					playToXbmcHandler.playToXbmc(uri, URLFactory.getURL(uri));
				}
			});
			button.setEnabled(true);
		}
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
