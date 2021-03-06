package phillykeyspots.frpapp;

import java.util.List;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import phillykeyspots.frpapp.XmlParser.Entry;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment to show individual Keyspots on a map.
 * Has the Get Directions Button.
 * 
 * @author btopportaldev
 *
 */

public class KeyspotMapFragment extends Fragment{

	private SupportMapFragment frag;
	private GoogleMap mMap;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		
		GoogleMapOptions options = new GoogleMapOptions();
		options.camera(new CameraPosition(new LatLng(39.95, -75.17), 12, 0, 0)).compassEnabled(false).mapType(GoogleMap.MAP_TYPE_NORMAL).rotateGesturesEnabled(false).tiltGesturesEnabled(false).scrollGesturesEnabled(true).zoomControlsEnabled(false).zoomGesturesEnabled(true);
		frag = SupportMapFragment.newInstance(options);
        getChildFragmentManager().beginTransaction().add(R.id.keyspot_map_container, frag).commit();
        
		return inflater.inflate(R.layout.fragment_keyspot_map, container, false);
	}
	
	public void onResume(){
		super.onResume();
		setUpIfNeeded();
		updateInfo();
	}
	
	/**
	 * Adds the marker onto the map using the entry's latitude and longitude.
	 * Turns on 'MyLocation'
	 */
	
	private void updateInfo(){
		Entry entry = ((KeyspotActivity)getActivity()).getEntry();
		Geocoder coder = new Geocoder(getActivity());
		try{
			List<Address> coded = coder.getFromLocationName(entry.latitude+entry.longitude, 1);
			mMap.setMyLocationEnabled(true);
			mMap.addMarker(new MarkerOptions().position(new LatLng(coded.get(0).getLatitude(), coded.get(0).getLongitude())).title(entry.keyspot).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void setUpIfNeeded(){
		if (mMap == null){
			mMap = frag.getMap();
			if(mMap != null){
				
			}
		}
	}
	
}
