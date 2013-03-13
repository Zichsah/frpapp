package phillykeyspots.frpapp;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import phillykeyspots.frpapp.EventsActivity;
import phillykeyspots.frpapp.R;
import phillykeyspots.frpapp.XmlParser.Entry;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.DatePicker.OnDateChangedListener;

/**
 * DashboardActivity is the activity that displays the fragments that make up the "Dashboard".
 * The Finder, Events, Resources, and Joml fragments are shown from here.
 * This is where the majority of the Application will take place and is the portal to all the information.
 * 
 * @author btopportaldev
 *
 */

@SuppressLint("DefaultLocale")
@SuppressWarnings("unused")
public class DashboardActivity extends FragmentActivity {
	
	private FinderFragment finder = new FinderFragment();
	private EventsFragment events = new EventsFragment();
	private ResourcesFragment resources = new ResourcesFragment();
	private JomlFragment joml = new JomlFragment();
	public static OnInfoWindowClickListener window_listener;
	private DashboardActivity dash = this;
	public static KeyspotLoader keyspots = null;
	private NetworkReceiver dashreceive = new NetworkReceiver();
	private String sPref = null;
	private static boolean wifiConnected = true;
	private static boolean mobileConnected = true;
	public static boolean refreshDisplay = true;
	private List<Entry> entries;
	private ProgressDialog progress;
	private HashMap<String, String> JOMLdata = new HashMap<String, String>();

	/**
	 * Sets up the Activity. Takes the button from the Main activity pressed 
	 * to select which of the Dashboard fragments to load. 
	 * @param savedInstanceState - all the data the app is currently storing.
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		
		setUpFinderFragment();
		//sets up the network receiver
		this.registerReceiver(dashreceive, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		Fragment frag = null;
		//gets the id of the button pushed on main activity and sets the fragment to display accordingly
		switch(Integer.parseInt(getIntent().getExtras().get("ID").toString())){
		case R.id.b_finder:
			frag = finder;
			break;
		case R.id.b_events:
			frag = events;
			break;
		case R.id.b_resources:
			frag = resources;
			break;
		case R.id.b_joml:
			frag = joml;
			break;
		}
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, frag).commit();
		
	}
	
	/**
	 * Sets up the downloading preferences
	 */
	
	protected void onStart(){
		super.onStart();
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
		sPref = shared.getString("listPref", "Any");
		updateConnectedFlags();
	}
	
	/**
	 * Takes down the receiver
	 */
	
    protected void onDestroy() {
        super.onDestroy();
        if (dashreceive != null) {
            this.unregisterReceiver(dashreceive);
        }
    }
    
    /**
     * Grabs information from phone to set up which networks are enabled
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
	 * Creates the function for when a map marker's info window is selected.
	 * Sends user to the Keyspot activity with the name of the Keyspot.
	 * Name of Keyspot is used to load the information on it and make a personal map.
	 * 
	 * Creates @KeyspotLoader which gathers Keyspot information off the web site.
	 */
    
	private void setUpFinderFragment(){
		window_listener = new OnInfoWindowClickListener(){

			public void onInfoWindowClick(Marker mark) {
				Intent intent = new Intent(dash, KeyspotActivity.class);
				intent.putExtra("Name", mark.getTitle());
				dash.startActivity(intent);
			}
			
		};
		keyspots = new KeyspotLoader();
	}
	
	/**
	 * Clears the Markers on the map off of it.
	 * Begins the Progress Dialog overlay which says "Loading...".
	 * Begins the @DownloadXmlTask which puts the Markers on the Map that correspond to the postal code provided.
	 * 
	 * @param view - The View of the Search Button.
	 */
	
	public void search(View view){
		FinderFragment.mMap.clear();
		progress = ProgressDialog.show(dash, "", "Loading...");
		new DownloadXmlTask().execute(((EditText)findViewById(R.id.finder_edit)).getText().toString());
	}
	
	/**
	 * Clears the Markers on the Map off of it.
	 * Begins the Progress Dialog overlay which says "Loading...".
	 * Gets the postal code of your current position using @Geocoder and @GoogleMap getMyLocation() function.
	 * Begins the @DownloadXmlTask which puts the Markers on the Map that correspond to the postal code of your location.
	 * 
	 * @param view - the View of the Use My Location Button.
	 */
	
	public void useLocation(View view){
		FinderFragment.mMap.clear();
		progress = ProgressDialog.show(dash, "", "Loading...");
		Geocoder coder = new Geocoder(dash);
		String coded = null;
		Location location = FinderFragment.mMap.getMyLocation();
		try{
			coded = coder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0).getPostalCode();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		new DownloadXmlTask().execute(coded);
	}
	
	/**
	 * This class is what is used to Load the data and put the Markers on the Map.
	 * 
	 * @author btopportaldev
	 *
	 */
	private class DownloadXmlTask extends AsyncTask<String, Void, String>{
		
		private String zip = null;
		
		/**
		 * Gathers the List of Keyspots.
		 * Stores the postal code in the variable 'zip'.
		 */
		
		@Override
		protected String doInBackground(String... params) {
			entries = keyspots.reload();
			zip = params[0];
			return null;
		}
		
		/**
		 * Goes through all the Entries loaded and compares it's postal code to the one provided.
		 * If they match the amount of Keyspots goes up.
		 * The @Geocoder then finds the location of the Keyspot and creates a marker for it.
		 * Toasts are created to provide user with information addressing the results.
		 */
		
		protected void onPostExecute(String result){

			if (entries != null){
				Geocoder code = new Geocoder(dash);
				int count = 0;
				for (Entry entry : entries){
					if (entry.postal_code.equals(zip)){
						count++;
						try{
							List<Address> coded = code.getFromLocationName(entry.latitude + entry.longitude, 1);
							FinderFragment.mMap.addMarker(new MarkerOptions().position(new LatLng(coded.get(0).getLatitude(), coded.get(0).getLongitude())).title(entry.keyspot).snippet("Click for more info.").icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));
						}catch (Exception e){
							e.printStackTrace();
						}
					}
				}
				if (count != 0){
					Toast.makeText(dash,  Integer.valueOf(count).toString()+ " Keyspots Loaded", Toast.LENGTH_LONG).show();
				}
				else {
					Toast.makeText(dash, "No Keyspots Found", Toast.LENGTH_LONG).show();
				}
			}
			else{
				Toast.makeText(dash, "Problem Downloading Keyspots", Toast.LENGTH_LONG).show();	
			}
			progress.dismiss();
		}
		
	}
    
	/**
	 * Is a Network receiver used to help download needed information.
	 * 
	 * @author btopportaldev
	 *
	 */
	
	public class NetworkReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connMgr =
					(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			// Checks the user prefs and the network connection.
			if ("Wi-Fi".equals(sPref) && networkInfo != null&& networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				refreshDisplay = true;
				Toast.makeText(context, R.string.wifi_connected, Toast.LENGTH_SHORT).show();
			} else if ("Any".equals(sPref) && networkInfo != null) {
				refreshDisplay = true;
			} else {
				refreshDisplay = false;
				Toast.makeText(context, R.string.lost_connection, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	/**
	 * Tells the @ResourcesFragment to change the information in it's @WebView based on the button pressed.
	 * 
	 * @param view - The button pressed. The id is sent to fragment to change the webview.
	 */
	
	public void resourcesButton(View view){
		resources.switchTab(view.getId());
	}
	
	/**
	 * The showEventsbyZIP method grabs the zipcode
	 * that the user entered into the textbox( id : enter_zip_code) and displays a Toast asking '
	 * them to enter a zipcode if one attempts to search 
	 * without having entered one.
	 * The method then builds the query/URL string and passes
	 * it to EventsActivity through putExtra().
	 * 
	 * @author btopportal
	 * @param view - the search button (id: b_events) that was clicked
	 * @see EventsActivity
	 */
	
	public void showEventsbyZIP(View view) {
		EditText editText = (EditText) findViewById(R.id.enter_zip_code);
		String query = editText.getText().toString();
		if (!query.equals("")){
			Intent intent = new Intent(this, EventsActivity.class);
			String fullquery = "https://www.phillykeyspots.org/events.xml/?distance[postal_code]=" + query;
			intent.putExtra("EXTRA_MESSAGE", fullquery);
			startActivity(intent);
		} else {
			Toast.makeText(getBaseContext(), "Enter a zip-code", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * onRadioClicked method gets which filter button was selected and 
	 * attaches the appropriate taxonomy term to the query 
	 * URL and passed the complete url to the EventsActivity through
	 * the putExtra() method.
	 * 
	 * @author btopportal
	 * @param view - the radio button, either in by level or by topic filter, that was clicked 
	 * @see EventsActivity
	 */
	public void onRadioClicked(View view){
		//Which view is checked.
		boolean checked = ((RadioButton) view).isChecked();
		String query = "https://www.phillykeyspots.org/events.xml/term/";

		switch(view.getId()){
			case R.id.checkbx_all_levels:
				if(checked){
					query = query + "All%20Levels";}
				break;
			case R.id.checkbx_first_time:
				if(checked){
					query = query + "First-time";}
				break;
			case R.id.checkbx_beginner:
				if(checked){
					query = query + "Beginner";}
				break;
			case R.id.checkbx_intermediate:
				if(checked){
					query = query + "Intermediate";}
				break;
			case R.id.checkbx_advanced:
				if(checked){
					query = query + "Advanced";}
				break;
			case R.id.checkbx_tech_expert:
				if(checked){
					query = query + "Tech Expert";}
				break;
			case R.id.checkbx_web_access:
				if(checked){
					query = query + "Web%20Access";}
				break;
			case R.id.checkbx_computer_basics:
				if(checked){
					query = query + "Computer%20Basics";}
				break;
			case R.id.checkbx_internet_basics:
				if(checked){
					query = query + "Internet%20Basics";}
				break;
			case R.id.checkbx_ms_office:
				if(checked){
					query = query + "MS%20Office";}
				break;
			case R.id.checkbx_social_media:
				if(checked){
					query = query + "Social%20Media";}
				break;
			case R.id.checkbx_job_search:
				if(checked){
					query = query + "Job%20Search";}
				break;
		}
		Intent intent = new Intent(this, EventsActivity.class);
		intent.putExtra("EXTRA_MESSAGE", query);
		startActivity(intent);
	}
	
	/**
	 * openPanel() controls the accordion style
	 * functionality of the events filter. It gets the id of 
	 * the clicked button and sets it's corresponding linearlayout's visibility
	 * to visible and sets the visibility of the other layouts to gone. The linearlayouts
	 * are named as panel1, panel2, panel3 with their buttons as b_panel1, b_panel2 etc.
	 * 
	 * @author btopportal
	 * @param view - the 'filter by' button that was clicked
	 */
	// Events view filter accordion style 
		public void openPanel(View view){
			LinearLayout p1 = (LinearLayout)findViewById(R.id.panel1);
			LinearLayout p2 = (LinearLayout)findViewById(R.id.panel2);
			LinearLayout p3 = (LinearLayout)findViewById(R.id.panel3);
			LinearLayout p4 = (LinearLayout)findViewById(R.id.panel4);
			switch(view.getId()){
				case R.id.b_panel1:
					p1.setVisibility(View.VISIBLE);
					p2.setVisibility(View.GONE);
					p3.setVisibility(View.GONE);
					p4.setVisibility(View.GONE);
					break;
				case R.id.b_panel2:	
					readyCalendar();
					p2.setVisibility(View.VISIBLE);
					p1.setVisibility(View.GONE);
					p3.setVisibility(View.GONE);
					p4.setVisibility(View.GONE);
					break;
				case R.id.b_panel3:
					p3.setVisibility(View.VISIBLE);
					p1.setVisibility(View.GONE);
					p2.setVisibility(View.GONE);
					p4.setVisibility(View.GONE);
					break;
				case R.id.b_panel4:
					p4.setVisibility(View.VISIBLE);
					p1.setVisibility(View.GONE);
					p2.setVisibility(View.GONE);
					p3.setVisibility(View.GONE);
					break;
			}
		}
		
		/**
		 * readyCalenda() sets the byDate section up for use. It gets today's 
		 * date and initializes the calendar then registers if the user
		 * changes the date and sets the new date as the selected date instead of 
		 * today's date.
		 * 
		 * This method is only called when the user clicks on the byDate button 
		 * also known as panel 2 in openPanel()
		 * 
		 * @author btopportal 
		 */
		
		@SuppressLint("NewApi")
		private void readyCalendar() {
			Calendar calendar = Calendar.getInstance(); //Set the date
			DatePicker eventstart = (DatePicker) findViewById(R.id.eventdatepicker);
			CalendarView calview = eventstart.getCalendarView();
			calview.setShowWeekNumber(false);
			//Events date picker initialization with current date
			eventstart.init(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener(){
				public void onDateChanged(DatePicker picker, int year, int month, int dayOfMonth){
					String selecteddate = Integer.toString(year) + "-" + Integer.toString(month+1) + "-" + Integer.toString(dayOfMonth);
					Toast.makeText(getBaseContext(), selecteddate, Toast.LENGTH_SHORT).show();
				}				
			});
		}
		
		/**
		 * submitDate is picked when a user clicks on the 
		 * Submit button in the ByDate section. It grabs the 
		 * selected date on the calendar and attaches it to the end of
		 * the url query then passes it to the EventsActivity via the 
		 * putExtra() method.
		 * 
		 * @author btopportal
		 * @param view - the submit button for the date section
		 * @see EventsActivity
		 */
		
		public void submitDate(View view){
			DatePicker picker = (DatePicker) findViewById(R.id.eventdatepicker);
			int yyyy = picker.getYear();
			int mm = picker.getMonth();
			int dd = picker.getDayOfMonth();
			String querydate = "https://www.phillykeyspots.org/events.xml" + String.format (Locale.US,"/%d-%02d-%02d",yyyy,mm,dd);
			Intent intent = new Intent(this, EventsActivity.class);
			intent.putExtra("EXTRA_MESSAGE", querydate);
			startActivity(intent);
		}
		
		/**
		 * onJomlRadioClicked handles clicks for the JOML
		 * form. It switches between the three options for the mailing
		 * list and depending on the radio button clicked it adds gets
		 * the relevant query value and adds it to a hashmap as a key-value
		 * pair.
		 * 
		 * @author btopportal
		 * @param view - the radio button clicked
		 */
		public void onJomlRadioClicked(View view){
			boolean checked = ((RadioButton) view).isChecked();
			String jomloptions = null;
			switch(view.getId()){
				case(R.id.joml1):
					if(checked){
						jomloptions = "frpmail";
					}
					break;
				case(R.id.joml2):
					if(checked){
						jomloptions = "computertraining";
					}
					break;
				case(R.id.joml3):
					if(checked){
						jomloptions = "events";
					}
					break;
			}
			JOMLdata.put("jomloption", jomloptions);
		}
		
		/**
		 * checkJomlData() checks the information that the user entered ensuring
		 * that the email, firstname and lastname fields are filled out.If checked
		 * the information is added to the hashmap as keyvalue pairs. If not a toast
		 * pops up asking them to enter thre required information.
		 * 
		 * If all required values have been added to the hashmap the postJomlData()
		 * method is then called 
		 *
		 * @author btopportal
		 * @param view - the submit form data button
		 * @see postJomlData()
		 */
		
		public void checkJomlData(View view){
			EditText emailText = (EditText) findViewById(R.id.joml_eaddress);
			EditText fnameText = (EditText) findViewById(R.id.joml_fname);
			EditText lnameText = (EditText) findViewById(R.id.joml_lname);
			EditText zipcodeText = (EditText) findViewById(R.id.joml_zipcode);
			EditText phoneText = (EditText) findViewById(R.id.joml_phone);
			
			String email = emailText.getText().toString();
			String fname = fnameText.getText().toString();
			String lname = lnameText.getText().toString();
			String zipcode = zipcodeText.getText().toString();
			String phone = phoneText.getText().toString();
			
			if (email.matches("")){
				Toast.makeText(getBaseContext(), "Enter email", Toast.LENGTH_SHORT).show();
				return;
			} else {
				JOMLdata.put("email", email);
			}
			
			if (fname.matches("") ){
				Toast.makeText(getBaseContext(), "Enter first name", Toast.LENGTH_SHORT).show();
				return;
			} else {
				JOMLdata.put("fname", fname);
			}
			
			if (lname.matches("")){
				Toast.makeText(getBaseContext(), "Enter last name", Toast.LENGTH_SHORT).show();
				return;
			} else {
				JOMLdata.put("lname", lname);
			}
			
			if (zipcode.matches("")){
				Toast.makeText(getBaseContext(), "Enter zipcode", Toast.LENGTH_SHORT).show();
				return;
			} else {
				JOMLdata.put("zipcode", zipcode);
			}
			
			if (phone.matches("")){
				Toast.makeText(getBaseContext(), "Enter phone", Toast.LENGTH_SHORT).show();
				return;
			} else {
				JOMLdata.put("phone", phone);
			}
			
			if (JOMLdata.size() < 6) {
				Toast.makeText(getBaseContext(), "Choose an option" , Toast.LENGTH_SHORT).show();
			}
			else {
				postJomlData(JOMLdata);
			}
		}
		
		/**
		 * postJomlData() gets the values in the hashmap based on their
		 * keys then passes them to the JOMLActivity
		 * 
		 * @author btopportal
		 * @param userdata
		 * @see JOMLActivity
		 */
		private void postJomlData(HashMap<String, String> userdata) {
			Intent intent = new Intent(this, JOMLActivity.class);
			intent.putExtra("joml_email", userdata.get("email"));
			intent.putExtra("joml_fname", userdata.get("fname"));
			intent.putExtra("joml_lname", userdata.get("lname"));
			intent.putExtra("joml_zipcode", userdata.get("zipcode"));
			intent.putExtra("joml_phone", userdata.get("phone"));
			intent.putExtra("joml_option", userdata.get("jomloption"));
			startActivity(intent);
		}
}
