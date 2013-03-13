package phillykeyspots.frpapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * This fragment expands the JOML layout button for the DashboardActivity.
 * 
 * @author btopportal
 * @see DashboardActivity
 */

public class JomlFragment extends Fragment {
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_joml, container, false);
    }
}