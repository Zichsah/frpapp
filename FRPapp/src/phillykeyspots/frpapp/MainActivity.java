package phillykeyspots.frpapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	/**Called when the user clicks the KEYSPOT finder button */
	public void keyspot_finder(View view) {
		Intent kfinder_intent = new Intent(this, SearchForKeyspotActivity.class);
		startActivity(kfinder_intent);
	}
	
	/**Called when the user clicks the Training Events finder button */
	public void events_finder(View view){
		Intent event_intent = new Intent(this, FindEventsActivity.class);
		startActivity(event_intent);
	}
	
	/**Called when the user clicks the Resources finder button */
	public void resources_finder(View view) {
		Intent resource_intent = new Intent(this, FindResourcesActivity.class);
		startActivity(resource_intent);
	}
	
	/**Called when the user clicks the Join Our Mailing List button */
	public void joml_form(View view){
		Intent joml_intent = new Intent(this, JoinOurMailingListActivity.class);
		startActivity(joml_intent);
	}

}

