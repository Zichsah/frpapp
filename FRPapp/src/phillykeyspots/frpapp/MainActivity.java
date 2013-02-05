package phillykeyspots.frpapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void dashboard(View view){
		int id = view.getId();
		Intent intent = new Intent(this, DashboardActivity.class);
		intent.putExtra("ID", id);
		startActivity(intent);
	}

}

