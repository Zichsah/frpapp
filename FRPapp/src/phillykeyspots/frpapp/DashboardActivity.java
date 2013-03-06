package phillykeyspots.frpapp;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParserException;

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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.DatePicker.OnDateChangedListener;

@SuppressLint("DefaultLocale")
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		
		setUpFinderFragment();
		this.registerReceiver(dashreceive, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		Fragment frag = null;
		
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
	
	protected void onStart(){
		super.onStart();
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
		sPref = shared.getString("listPref", "Any");
		updateConnectedFlags();
	}
	
    protected void onDestroy() {
        super.onDestroy();
        if (dashreceive != null) {
            this.unregisterReceiver(dashreceive);
        }
    }

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
		
	public void search(View view){
		FinderFragment.mMap.clear();
		progress = ProgressDialog.show(dash, "", "Loading...");
		new DownloadXmlTask().execute(((EditText)findViewById(R.id.finder_edit)).getText().toString());
	}
	
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
	
	private class DownloadXmlTask extends AsyncTask<String, Void, String>{
		
		private String zip = null;
		
		@Override
		protected String doInBackground(String... params) {
			entries = keyspots.reload();
			zip = params[0];
			return null;
		}
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
	
	public void resourcesButton(View view){
		resources.switchTab(view.getId());
	}
	
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
		
		// JOML Methods
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
		
		public void checkjomldata(View view){
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
