package phillykeyspots.frpapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

public class KeyspotActivity extends FragmentActivity {
	
	public String info;
	
	public void onCreate(Bundle saved){
		super.onCreate(saved);
		setContentView(R.layout.activity_keyspot);
		
		Intent intent = getIntent();
		info = intent.getStringExtra("Name");
		
		getSupportFragmentManager().beginTransaction().add(R.id.keyspot_fragment_container, new KeyspotInfoFragment()).commit();
	}
	public void changeFragment(View view){
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		switch (view.getId()){
		case (R.id.keyspot_info_button):
			ft.replace(R.id.keyspot_fragment_container, new KeyspotInfoFragment());
			break;
		case (R.id.keyspot_map_button):
			ft.replace(R.id.keyspot_fragment_container, new KeyspotMapFragment());
			break;
		}
		ft.commit();
	}
}
