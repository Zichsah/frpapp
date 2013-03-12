package phillykeyspots.frpapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
/**
 * The ContactUsActivity is the page that lists
 * all the contact numbers for the partners and 
 * the philly311. This content is hard-coded into
 * the page and is not dynamic
 * 
 * @author btopportal
 */
public class ContactUsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_us);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/**
		 * Inflate the menu; this adds items to the action bar
		 * if it is present
		 */
		getMenuInflater().inflate(R.menu.activity_contact_us, menu);
		return true;
	}

}
