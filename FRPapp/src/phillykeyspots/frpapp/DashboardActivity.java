package phillykeyspots.frpapp;

import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

@SuppressLint("DefaultLocale")
public class DashboardActivity extends FragmentActivity {
	
	public static OnInfoWindowClickListener window_listener;
	private DashboardActivity dash = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		
		Fragment fragment = null;
		
		window_listener = new OnInfoWindowClickListener(){
			
			public void onInfoWindowClick(Marker mark){
				//need to add information of selected mark to KeyspotActivity.
				Intent intent = new Intent(dash, KeyspotActivity.class);
				dash.startActivity(intent);
			}
			
		};
		
		switch(Integer.parseInt(getIntent().getExtras().get("ID").toString())){
		case R.id.b_finder:
			fragment = new FinderFragment();
			break;
		case R.id.b_events:
			fragment = new EventsFragment();
			break;
		case R.id.b_resources:
			fragment = new ResourcesFragment();
			break;
		case R.id.b_joml:
			fragment = new JomlFragment();
			break;
		}
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
	}
		
	public void search(View view){
			FinderFragment.mMap.clear();
			/* 
			 * Collect data from https
			 * Sort data based on zip code (make a list?)
			 * display markers based on sorted data
			 * */
			FinderFragment.mMap.addMarker(new MarkerOptions().position(new LatLng(39.96, -75.17)).title("Keyspot Name").snippet("Click for more info").icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));
	}

}
