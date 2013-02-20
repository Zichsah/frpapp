package phillykeyspots.frpapp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;
import org.xmlpull.v1.XmlPullParserException;

import phillykeyspots.frpapp.R;
import phillykeyspots.frpapp.HTTPSettingsActivity;
import phillykeyspots.frpapp.XmlParser;
import phillykeyspots.frpapp.XmlParser.Entry;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

public class KEYSPOTSActivity extends FragmentActivity {
	public static final String WIFI = "Wi-Fi";
	public static final String ANY = "Any";
	public static String URL; 
	
	//Connections
	private static boolean wifiConnected = false;
	private static boolean mobileConnected = true;
	//Display refresh
	public static boolean refreshDisplay = true;
	//User's current network setting preferences
	public static String sPref = null;
	
	// The BroadcastReceiver that tracks network connectivity changes.
    private NetworkReceiver receiver = new NetworkReceiver();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent_evnt = getIntent();
        //String evnt_msg = intent_evnt.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String evnt_msg = intent_evnt.getStringExtra("EXTRA_MESSAGE_KEYSPOTS");
        URL = evnt_msg;      
        // Register BroadcastReceiver to track connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);
    }
	
	// Refreshes the display if the network connection and the preference settings allow it.
    @Override
    public void onStart() {
        super.onStart();
        // Gets the user's network preference settings
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // Retrieves a string value for the preferences. The second parameter is the default value 
        sPref = sharedPrefs.getString("listPref", "Any");
        updateConnectedFlags();
        // Only loads the page if refreshDisplay is true. Otherwise, keeps previous display. 
        if (refreshDisplay) {
            loadPage();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    // Checks the network connection and sets the wifiConnected and mobileConnected variables accordingly.
    private void updateConnectedFlags() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }
	
	//Use AsyncTask
	public void loadPage(){
		if((sPref.equals(ANY)) && (wifiConnected || mobileConnected)){
			new DownloadXMLTask().execute(URL);
		} else if ((sPref.equals(WIFI)) && (wifiConnected)){
			new DownloadXMLTask().execute(URL);
		} else {
			showErrorPage();
		}
	}
	
	// Displays an error if the app is unable to load content.
    private void showErrorPage() {
        setContentView(R.layout.activity_keyspots);
        // The specified network connection is not available. Displays error message.
        WebView myWebView = (WebView) findViewById(R.id.webviewkeyspots);
        myWebView.loadData(getResources().getString(R.string.connection_error),
                "text/html", null);
    }

    // Populates the activity's options menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.http_feed, menu);
        return true;
    }

    // Handles the user's menu selection.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.settings:
                Intent settingsActivity = new Intent(getBaseContext(), HTTPSettingsActivity.class);
                startActivity(settingsActivity);
                return true;
        case R.id.refresh:
                loadPage();
                return true;
        default:
                return super.onOptionsItemSelected(item);
        }
    }
    
	//Asynctask implementation
	private class DownloadXMLTask extends AsyncTask<String, Void, String>{
		@Override
		protected String doInBackground(String... urls) {
			try {
				return loadXmlFromNetwork(urls[0]);
			} catch (IOException e) {
				return getResources().getString(R.string.connection_error);
			} catch (XmlPullParserException e) {
				return getResources().getString(R.string.xml_error);
			}
		}
		@Override
		protected void onPostExecute(String result) {
			setContentView(R.layout.activity_keyspots);
			//Display the HTML string in the UI via a WebView
			WebView eventsWebView = (WebView) findViewById(R.id.webviewkeyspots);
			eventsWebView.loadData(result, "text/html", null);
		}
	}
	
	private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
		InputStream stream = null;
		//Instantiate the parser
		XmlParser eventsparser = new XmlParser();
		List<Entry> entries = null;
		
		Calendar rightNow = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat("MMM dd h:mmaa", Locale.US);
		
		StringBuilder htmlString = new StringBuilder();
		htmlString.append("<b>" + getResources().getString(R.string.page_title_keyspots) + "</b><br/>");
		htmlString.append("<em>" + getResources().getString(R.string.updated) + " " + formatter.format(rightNow.getTime()) + "</em>");
		
		try {
			stream = downloadUrl(urlString);
			entries = eventsparser.parse(stream);
			//close input stream after application is finished with it.
		} finally {
			if (stream != null){
				stream.close();
			}
		}
		
		//Return the list of entries
		for (Entry entry : entries){
			htmlString.append("<span>Fetching from : " + URL + " <br/>");
			htmlString.append("KEYSPOT: " + entry.keyspot + "<br/>");
			htmlString.append("Managing_partner: " + entry.managing_partner + "<br/>");
			htmlString.append("Contact: " + entry.contact + "<br/>");
			htmlString.append("City: " + entry.city + "<br/>");
			htmlString.append("Postal_code: " + entry.postal_code + "<br/>");
			htmlString.append("Street: " + entry.street + "<br/>");
			htmlString.append("Hours: " + entry.hours + "<br/>");
			htmlString.append("Workstations: " + entry.workstations + "<br/>");
			htmlString.append("Restrictions: " + entry.restrictions + "<br/>");
			htmlString.append("Wi-fi: " + entry.wi_fi + "<br/>");
			htmlString.append("Latitude: " + entry.latitude + "<br/>");
			htmlString.append("Longitude: " + entry.longitude + "<br/><br/>");
			
		}
		return htmlString.toString();
	}
	
	private InputStream downloadUrl(String urlString) throws IOException {
		URL url = new URL(urlString);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setReadTimeout(10000 /*milliseconds*/);
		conn.setConnectTimeout(15000 /*milliseconds*/);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		conn.connect();
		InputStream stream = conn.getInputStream();
        return stream;
	}
	
   // Monitor the connection
   public class NetworkReceiver extends BroadcastReceiver {
       @Override
       public void onReceive(Context context, Intent intent) {
           ConnectivityManager connMgr =
                   (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
           NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
           // Checks the user prefs and the network connection.
           if (WIFI.equals(sPref) && networkInfo != null
                   && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
               refreshDisplay = true;
               Toast.makeText(context, R.string.wifi_connected, Toast.LENGTH_SHORT).show();
           } else if (ANY.equals(sPref) && networkInfo != null) {
               refreshDisplay = true;
               // Otherwise, the app can't download content--there is no network connection (mobile or Wi-Fi)
           } else {
               refreshDisplay = false;
               Toast.makeText(context, R.string.lost_connection, Toast.LENGTH_SHORT).show();
           }
       }
   }
}
