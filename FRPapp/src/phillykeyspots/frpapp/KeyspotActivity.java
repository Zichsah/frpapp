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

public class KeyspotActivity extends FragmentActivity {
	
	private String info;
	private Entry entry;
	
	public void onCreate(Bundle saved){
		super.onCreate(saved);
		setContentView(R.layout.activity_keyspot);
		
		Intent intent = getIntent();
		info = intent.getStringExtra("Name");
		getSupportFragmentManager().beginTransaction().add(R.id.keyspot_fragment_container, new KeyspotInfoFragment()).commit();
		getEntry();
	}
	
	public void changeFragment(View view){
		((Button)view).setTextColor(Color.BLUE);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		switch (view.getId()){
		case (R.id.keyspot_info_button):
			ft.replace(R.id.keyspot_fragment_container, new KeyspotInfoFragment());
			((Button)findViewById(R.id.keyspot_map_button)).setTextColor(Color.BLACK);
			break;
		case (R.id.keyspot_map_button):
			ft.replace(R.id.keyspot_fragment_container, new KeyspotMapFragment());
			((Button)findViewById(R.id.keyspot_info_button)).setTextColor(Color.BLACK);
			break;
		}
		ft.commit();
	}
	public Entry getEntry(){
		entry = null;
		List<Entry> entries = DashboardActivity.keyspots.getEntries();
		for(Entry list: entries){
			if(list.keyspot.equals(info)){
				entry = list;
			}
		}
		return entry;
	}
	
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
