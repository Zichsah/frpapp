package phillykeyspots.frpapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;
/**
 * This Activity gets the information for a single event that was clicked
 * in the list and displays it on the screen 
 * 
 * @author btopportal
 * @see EventsActivity
 */
public class SingleEventItemActivity extends Activity {
	/**
	 * When the activity is created the required values are gotten 
	 * through the getStringExtra method. The strings are then used to populate
	 * the layout for the page according to their respective textViews
	 * 
	 * @author btopportal
	 * @param savedInstanceState - all the data that the app has saved
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_single_event_item);
		Intent intent_singleitem = getIntent();
		
        String title = intent_singleitem.getStringExtra("title");
        
        String date = intent_singleitem.getStringExtra("date");
        
        String keyspot = intent_singleitem.getStringExtra("keyspot");
        
        String address = intent_singleitem.getStringExtra("address");
        
        String partner = intent_singleitem.getStringExtra("partner");
        
        String other_info = intent_singleitem.getStringExtra("other_info");
        
        TextView e_title = (TextView) findViewById(R.id.e_title);
        e_title.setText(title);
        
        TextView e_date = (TextView) findViewById(R.id.e_date);
        e_date.setText(date);
        
        TextView e_keyspot = (TextView) findViewById(R.id.e_keyspot);
        e_keyspot.setText(keyspot);
        
        TextView e_address = (TextView) findViewById(R.id.e_address);
        e_address.setText(address);
        
        TextView e_partner = (TextView) findViewById(R.id.e_partner);
        e_partner.setText(partner);
        
        TextView e_other_info = (TextView) findViewById(R.id.e_other_info);
        e_other_info.setText(other_info);
	}
	
}
