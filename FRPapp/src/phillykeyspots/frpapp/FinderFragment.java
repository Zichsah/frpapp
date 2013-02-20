package phillykeyspots.frpapp;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;

public class FinderFragment extends Fragment {
	
	private SupportMapFragment frag;
	private OnInfoWindowClickListener window_click;
	public GoogleMap mMap;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		GoogleMapOptions options = new GoogleMapOptions();
		options.camera(new CameraPosition(new LatLng(39.95, -75.17), 12, 0, 0)).compassEnabled(false).mapType(GoogleMap.MAP_TYPE_NORMAL).rotateGesturesEnabled(false).tiltGesturesEnabled(false).scrollGesturesEnabled(true).zoomControlsEnabled(false).zoomGesturesEnabled(true);
		frag = SupportMapFragment.newInstance(options);
        getChildFragmentManager().beginTransaction().add(R.id.map, frag).commit();

		window_click = new OnInfoWindowClickListener(){

			@Override
			public void onInfoWindowClick(Marker arg0) {
				// TODO Auto-generated method stub
			}
			
		};
		setUpIfNeeded();
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_finder, container, false);
	}
	
	public void search(View view){
    	// TODO collect zipcodes and display respective markers
    	// TODO refresh map
    	mMap.clear();
    	mMap.addMarker(new MarkerOptions().position(new LatLng(39.96, -75.17)).title("Keyspot Name").snippet("Click for more info").icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));
    }
	
	public void onResume(){
		super.onResume();
		setUpIfNeeded();
	}
	
	private void setUpIfNeeded(){
		if (mMap == null){
			mMap = frag.getMap();
			if(mMap != null){
				mMap.setOnInfoWindowClickListener(window_click);
			}
		}
	}
}
