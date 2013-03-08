package phillykeyspots.frpapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return true;
    }
	
	 // Handles the user's menu selection.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_aboutus:
        	Intent aboutusActivity = new Intent(getBaseContext(), AboutUsActivity.class);
            startActivity(aboutusActivity);
            return true;
        case R.id.menu_contactus:
        	Intent contactusActivity = new Intent(getBaseContext(), ContactUsActivity.class);
        	startActivity(contactusActivity);
        	return true;
        default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	public void dashboard(View view){
		int id = view.getId();
		Intent intent = new Intent(this, DashboardActivity.class);
		intent.putExtra("ID", id);
		startActivity(intent);
	}

}

