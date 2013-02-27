package phillykeyspots.frpapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class KeyspotActivity extends FragmentActivity {
	
	public String info;
	
	public void onCreate(Bundle saved){
		super.onCreate(saved);
		setContentView(R.layout.activity_keyspot);
		
		Intent intent = getIntent();
		info = intent.getStringExtra("Name");
		
		KeyspotInfoFragment fragment = new KeyspotInfoFragment();
		getSupportFragmentManager().beginTransaction().add(R.id.keyspot_fragment_container, fragment).commit();
	}
}
