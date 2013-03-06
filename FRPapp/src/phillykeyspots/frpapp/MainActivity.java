package phillykeyspots.frpapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * This is where the Application starts.
 * It loads a fragment that holds the Buttons
 * 
 * @author btopportaldev
 *
 */

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	/**
	 * Starts the DashboardActivity.
	 * Takes the information of the button that is pressed to tell which Fragment to load.
	 * 
	 * @param view - the Button that was pressed.
	 */
	
	public void dashboard(View view){
		int id = view.getId();
		Intent intent = new Intent(this, DashboardActivity.class);
		intent.putExtra("ID", id);
		startActivity(intent);
	}

}

