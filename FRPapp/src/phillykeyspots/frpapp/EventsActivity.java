package phillykeyspots.frpapp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * EventsActivity displays a list of events that are returned from the 
 * get request to the phillykeyspots.org site. The activity also handles
 * the fetching of the results by obtaining the query passed from Dashboard
 * activity through the putExtra() method.The activity uses the parsed xml returned 
 * by the entry class and breaks it down according to certain tags then 
 * feeds these strings to the a list adapter which in turn populates the layout.
 * 
 * @author btopportal
 * @see DashboardActivity#showEventsbyZIP(View)
 * @see DashboardActivity#onRadioClicked(View)
 * @see DashboardActivity#submitDate(View)
 * 
 */

public class EventsActivity extends Activity {
	/**
	 * wifiConnected and mobileConnected are booleans that change according to the network the user is currently connected to
	 * The URL string holds the query passed from DashboardActivity
	 * refreshDisplay is a boolean that when true causes the display to be refreshed thus reloads content
	 * visited is a boolean that prevents the screen from being reloaded after the user clicks 'back' after viewing a single list item
	 * sPref is a string that holds the user's current network settings i.e either WIFI or ANY
	 * reveiver is a network receiver that tracks any changed to network connectivity
	 * eventTitles is a hashmap that holds the parsed xml for each event
	 * progress is a loading dialog the displays a "loading events" whenever information is being fetched
	 * 
	 * @author btopportal
	 */
	
	public static final String WIFI = "Wi-Fi";
	public static final String ANY = "Any";
	public static String URL; 
	private static boolean wifiConnected = false;
	private static boolean mobileConnected = true;
	public static boolean refreshDisplay = true;
	public static boolean visited = false;
	public static String sPref = null;
    private NetworkReceiver receiver = new NetworkReceiver();
    public ArrayList<HashMap<String, String>> eventTitles = new ArrayList<HashMap<String, String>>();
    
    public ProgressDialog progress;
	/**
	 * OnCreate() starts the progress bar once hte activity starts,
	 * It fetches the message passed from the previous activity via putExtra()
	 * and sets it as the value of the URL string.
	 * @author btopportal
	 * @param savedInstanceState - all the data the app is currently storing.
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = ProgressDialog.show(EventsActivity.this,"","Loading Events!",true);
        Intent intent_evnt = getIntent();
        String evnt_msg = intent_evnt.getStringExtra("EXTRA_MESSAGE");
        URL = evnt_msg;      
        
        /**
         * Register BroadcastReceiver to track connection changes
         * 
         * @author btopportal
         */
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);
    }
	
	/**
	 * onStart() refreshes the display if the network connection and the preference settings allow it.
	 * @author btopportal
	 */

    @Override
    public void onStart() {
        super.onStart();
        /**
         * Get's the user's network preference settings
         * @author btopportal
         */
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        /**
         * Retrieves a string value for the preferences. The second parameter is the default value
         * @author
         */
        sPref = sharedPrefs.getString("listPref", "Any");
        updateConnectedFlags();
        /**
         * Only loads the page if refreshDisplay is true. Otherwise, keeps previous display
         * @author btopportal
         */
        if (refreshDisplay && !visited) {
        	visited = true;
            loadPage();
        }
    }
    
    /**
     * onDestroy() cleans up and frees memory when user leaves the EventsActivity
     * Stops the progress dialog
     * @author btopportal
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        visited = false;
        if (receiver != null) {
        	progress.dismiss();
            this.unregisterReceiver(receiver);
        }
    }
    
    /**
     * updateConnectedFlags() checks the network connection and sets the wifiConnected and mobileConnected variables accordingly
     * @author btopportal
     */
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
	
	/**
	 * loadPage() uses AsyncTask to perform the fetching of events in the background
	 * if the user is on wifi or on mobile othewise calls the showErrorPage method 
	 * and displays an error message
	 * 
	 * @author btopportal
	 * @see AsyncTask
	 * @see EventsActivity#showErrorPage()
	 */
	public void loadPage(){
		if((sPref.equals(ANY)) && (wifiConnected || mobileConnected)){
			new DownloadXMLTask().execute(URL);
		} else if ((sPref.equals(WIFI)) && (wifiConnected)){
			new DownloadXMLTask().execute(URL);
		} else {
			showErrorPage();
		}
	}
	
	/**
	 * showErrorPage() displays an error if the app is unable to load content
	 * because the specified network connection is not available.
	 * @author btopportal
	 */

    private void showErrorPage() {
        setContentView(R.layout.activity_events);
        progress.dismiss();
        Toast.makeText(getBaseContext(), getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Populates the activity's options menu when the activity
     * is created using the http_feed menu layout
     * @author btopportal
     */
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.http_feed, menu);
        return true;
    }
    
    /**
     * Handles the user's menu selection
     * 1.Settings - opens up a page for the user to set whether to download
     * when on wifi or on any network
     * 2.Refresh - when clicked it reloads the page and refreshed the content
     * @author btopportal
     * @param item - the the menu item selected
     */

    @Override    // Handles the user's menu selection.
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
    
    /**
     * DownloadXMLTask implements the Asyntask which allows fetching to 
     * be done in the background and gets the result of the background activity in the 
     * form of a string
     * @author btopportal
     * @param String - the is the url used as the query
     * @param Void - the is the progress units which are unused in this method
     * @param String - the results, a string containing the date & time the events were last updated.
     * @see AsyncTask
     * @see XmlParser
     * @see Entry
     */
    
	private class DownloadXMLTask extends AsyncTask<String, Void, String>{
		/**
		 * doInBackground() allows activities to run in the background 
		 * while the progress bar is displayed to the user
		 * 
		 * @author btopportal
		 * @param urls - a string array containing the query
		 * @return loadXmlFromNetwork() - returned when connection to resource has been established
		 * @return IOException() - returned when a connection error occurs 		
		 * @return XmlPullParserException() - returned when connection is established but there is an error in the xml structure
		 */
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
		/**
		 * onPostExecute() changes the view to the layout for the list
		 * and calls the loadList() method to load events into the list structure
		 * and stops the loading progress bar
		 * 
		 * @author btopportal
		 * @param result - the date and time string returned from loadXmlFromNetwork
		 * @see EventsActivity#loadList()
		 * @see EventsActivity#loadXmlFromNetwork(String)
		 */
		@Override
		protected void onPostExecute(String result) {
			progress.dismiss();
			setContentView(R.layout.activity_events);
			loadList();
		}
	}
	
	/**
	 * loadList() uses a list adapter to inflate the list on the layout,
	 * with data that it gets from the eventTitles hashmap. Using the keys, the listadapter
	 * puts the values into their corresponding textviews in the layout.
	 * loadList() also initiates a listener for each list item so that when it is clicked
	 * it opens up a new activity and displays more information.
	 * @author btopportal
	 */
	public void loadList() {
		/**
		 * adapter is a ListAdapter that is set using a SimpleAdapter which takes the parameters
		 * Only the title, date and Keyspot name are displayed in the list thought, the other values
		 * populate TextViews however the visibility of those text views are set to gone on the main 
		 * list. These values are shown when the user selects a single list item
		 * 
		 * @author btopportal
		 * @param this - the current activity
		 * @param eventTitles - the hashmap holding the data
		 * @param String[] - a string array containing the keys of values we want
		 * @param int[] - an integer array of numeric values corresponding to the unique id's of the textviews to input data into
		 * @see SimpleAdapter
		 */
		ListAdapter adapter = new SimpleAdapter(this,eventTitles,
				R.layout.activity_events,
				new String[]{"title","date","keyspot","address","partner","other_info"},
				new int[]{R.id.title,R.id.date,R.id.keyspot,R.id.e_k_address,R.id.e_partner,R.id.e_other_info});
		/**
		 * Selecting a single ListView item
		 */

		ListView lv = (ListView)findViewById(android.R.id.list);
		lv.setAdapter(adapter);
			/**
			 * Listening to a single list item click
			 */
			lv.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				/**
				 * Getting the corresponding values for the list item from it's TextViews
				 */
				String title = ((TextView) view.findViewById(R.id.title)).getText().toString();
				String date = ((TextView) view.findViewById(R.id.date)).getText().toString();
				String keyspot = ((TextView) view.findViewById(R.id.keyspot)).getText().toString();
				String address = ((TextView) view.findViewById(R.id.e_k_address)).getText().toString();
				String partner = ((TextView) view.findViewById(R.id.e_partner)).getText().toString();
				String other_info = ((TextView) view.findViewById(R.id.e_other_info)).getText().toString();
				/**
				 * Start the SingleEventItemActivity and pass the values to it through putExtra() to display
				 * the full information for the selected event
				 */
				Intent in = new Intent(getApplicationContext(), SingleEventItemActivity.class);
				in.putExtra("title", title);
				in.putExtra("date", date);
				in.putExtra("keyspot", keyspot);
				in.putExtra("address", address);
				in.putExtra("partner", partner);
				in.putExtra("other_info", other_info);
				startActivity(in);
			}
		});
	}
	/**
	 * loadXmlFromNetwork sends out the get request and uses an XmlParser to parse the response
	 * from the server, then pulls data from the parsed xml and puts it into the eventTitles hashmap.
	 * 
	 * @author btopportal
	 * @param urlString - the url to be used as the query
	 * @see Entry
	 * @see XmlParser
	 * @return htmlString - a string the show the last date and time the results were updated
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
		InputStream stream = null;
		
		/**
		 * Instantiate the parser
		 */
		XmlParser eventsparser = new XmlParser();
		List<Entry> entries = null;
		
		Calendar rightNow = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat("MMM dd h:mmaa", Locale.US);
		
		StringBuilder htmlString = new StringBuilder();
		htmlString.append("<b>" + getResources().getString(R.string.page_title_events) + "</b><br/>");
		htmlString.append("<em>" + getResources().getString(R.string.updated) + " " + formatter.format(rightNow.getTime()) + "</em>");
		
		try {
			stream = downloadUrl(urlString);
			entries = eventsparser.parse(stream);
			/**
			 * Close the input stream after application is finished with it
			 */
		} finally {
			if (stream != null){
				stream.close();
			}
		}
		
		/**
		 * Return the list of entries <i>(these are the events)</i>
		 */
		for (Entry entry : entries){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("title", entry.topics + " : " + entry.type + " >> " + entry.title);
			map.put("date",entry.training_dates);
			map.put("keyspot", entry.keyspot);
			map.put("address", entry.street + ",\n" + entry.city + ",\n" + entry.province + ", " + entry.postal_code);
			map.put("partner", entry.managing_partner + "\nContact : " + entry.contact + "\nEmail : " + entry.email);
			map.put("other_info", "Ideal for : \n" + entry.level + "\nMore Info : \n" + entry.more_info);
			/**
			 * Adding the arraylist to the hashmap
			 */
			eventTitles.add(map);
		}
		
		return htmlString.toString();
	}

	/**
	 * Declare the type of httpconnection in this case "GET"
	 * @author btopportal
	 * @param urlString - the query url
	 * @return stream - the input stream
	 * @throws IOException
	 */
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
	
	/**
	 * Monitors the user connection and allows the page to be refreshed only
	 * if the user is on wifi or any other network otherwise if a refresh attempt is made
	 * a connection error message is displayed
	 * @author btopportal
	 */
   public class NetworkReceiver extends BroadcastReceiver {
       @Override
       public void onReceive(Context context, Intent intent) {
           ConnectivityManager connMgr =
                   (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
           NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
           /**
            * Checks the user preferences and hte network connection
            */
           if (WIFI.equals(sPref) && networkInfo != null
                   && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
               refreshDisplay = true;
               Toast.makeText(context, R.string.wifi_connected, Toast.LENGTH_SHORT).show();
           } else if (ANY.equals(sPref) && networkInfo != null) {
               refreshDisplay = true;
           } else {
               refreshDisplay = false;
               Toast.makeText(context, R.string.lost_connection, Toast.LENGTH_SHORT).show();
           }
       }
   }
}
