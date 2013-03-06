package phillykeyspots.frpapp;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;

/**
 * Fragment that contains the GoogleMap.
 * Sets up the map with options and inflates the layout for the whole fragment.
 * 
 * @author btopportaldev
 *
 */

public class FinderFragment extends Fragment {
	
	private SupportMapFragment frag;
	public static GoogleMap mMap;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		GoogleMapOptions options = new GoogleMapOptions();
		options.camera(new CameraPosition(new LatLng(39.95, -75.17), 12, 0, 0)).compassEnabled(false).mapType(GoogleMap.MAP_TYPE_NORMAL).rotateGesturesEnabled(false).tiltGesturesEnabled(false).scrollGesturesEnabled(true).zoomControlsEnabled(false).zoomGesturesEnabled(true);
		frag = SupportMapFragment.newInstance(options);
        getChildFragmentManager().beginTransaction().add(R.id.map, frag).commit();

		setUpIfNeeded();
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_finder, container, false);
	}
	
	public void onResume(){
		super.onResume();
		setUpIfNeeded();
	}
	
	private void setUpIfNeeded(){
		if (mMap == null){
			mMap = frag.getMap();
			if(mMap != null){
				mMap.setOnInfoWindowClickListener(DashboardActivity.window_listener);
				mMap.setMyLocationEnabled(true);
			}
		}
	}
}
