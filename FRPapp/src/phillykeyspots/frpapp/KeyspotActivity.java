package phillykeyspots.frpapp;

import java.util.List;

import phillykeyspots.frpapp.XmlParser.Entry;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

/**
 * Activity for individual Keyspots.
 * Holds both the info fragment and the map fragment.
 * 
 * @author btopportaldev
 *
 */

public class KeyspotActivity extends FragmentActivity {
	
	private String info;
	private Entry entry;
	
	/**
	 * Gathers from intent the name of the selected keyspot.
	 * Loads the info fragment.
	 */
	public void onCreate(Bundle saved){
		super.onCreate(saved);
		setContentView(R.layout.activity_keyspot);
		
		Intent intent = getIntent();
		info = intent.getStringExtra("Name");
		getSupportFragmentManager().beginTransaction().add(R.id.keyspot_fragment_container, new KeyspotInfoFragment()).commit();
		getEntry();
	}
	
	/**
	 * When a tab button is pressed with switches to that fragment.
	 * 
	 * @param view - button used ot decide which fragment to load.
	 */
	public void changeFragment(View view){
		((Button)view).setTextColor(getResources().getColor(R.color.orange1645C));
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		switch (view.getId()){
		case (R.id.keyspot_info_button):
			ft.replace(R.id.keyspot_fragment_container, new KeyspotInfoFragment());
			((Button)findViewById(R.id.keyspot_map_button)).setTextColor(Color.WHITE);
			break;
		case (R.id.keyspot_map_button):
			ft.replace(R.id.keyspot_fragment_container, new KeyspotMapFragment());
			((Button)findViewById(R.id.keyspot_info_button)).setTextColor(Color.WHITE);
			break;
		}
		ft.commit();
	}
	
	/**
	 * Gathers all the Keyspots information from the DashboardActivity.
	 * Compares the name of selected Keyspot to list to locate the rest of the information.
	 * 
	 * @return - the Entry of the selected Keyspot.
	 */
	
	public Entry getEntry(){
		entry = null;
		List<Entry> entries = DashboardActivity.keyspots.getEntries();
		for(Entry list: entries){
			if(list.keyspot.equals(info)){
				entry = list;
				break;
			}
		}
		return entry;
	}
	
	/**
	 * Gets location of Keyspot using @Geocoder.
	 * Tells phone to use GoogleNavigation Application to get directions.
	 * 
	 * @param view - Get Directions Button 
	 */
	
	public void getDirections(View view){
		Geocoder coder = new Geocoder(this);
		Address address = null;
		try{
			address = coder.getFromLocationName(entry.latitude+entry.longitude, 1).get(0);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		Uri uri = Uri.parse("google.navigation:q="+address.getLatitude()+","+address.getLongitude());
		startActivity(new Intent(Intent.ACTION_VIEW, uri));
	}
}
