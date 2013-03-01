package phillykeyspots.frpapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

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
		((Button)view).setTextColor(Color.BLUE);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		switch (view.getId()){
		case (R.id.keyspot_info_button):
			ft.replace(R.id.keyspot_fragment_container, new KeyspotInfoFragment());
			((Button)findViewById(R.id.keyspot_map_button)).setTextColor(Color.BLACK);
			break;
		case (R.id.keyspot_map_button):
			ft.replace(R.id.keyspot_fragment_container, new KeyspotMapFragment());
			((Button)findViewById(R.id.keyspot_info_button)).setTextColor(Color.BLACK);
			break;
		}
		ft.commit();
	}
	public void getDirections(View view){
		
	}
}
