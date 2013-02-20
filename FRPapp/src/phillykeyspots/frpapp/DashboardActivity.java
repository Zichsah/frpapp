package phillykeyspots.frpapp;

import phillykeyspots.frpapp.DatePickerFragment;
import phillykeyspots.frpapp.EventsActivity;
import phillykeyspots.frpapp.KEYSPOTSActivity;
import phillykeyspots.frpapp.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

@SuppressLint("DefaultLocale")
public class DashboardActivity extends FragmentActivity {
	
	private FinderFragment finder = new FinderFragment();
	private EventsFragment events = new EventsFragment();
	private ResourcesFragment resources = new ResourcesFragment();
	private JomlFragment joml = new JomlFragment();
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.fragment_container);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		switch(Integer.parseInt(getIntent().getExtras().get("ID").toString())){
		case R.id.b_finder:
			mViewPager.setCurrentItem(0);
			break;
		case R.id.b_events:
			mViewPager.setCurrentItem(1);
			break;
		case R.id.b_resources:
			mViewPager.setCurrentItem(2);
			break;
		case R.id.b_joml:
			mViewPager.setCurrentItem(3);
			break;
		}
	}
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch(position){
			case(0):
				return finder;
			case(1):
				return events;
			case(2):
				return resources;
			case(3):
				return joml;
			}
			return null;
		}

		@Override
		public int getCount() {
			return 4;
		}
	}
		
	public void search(View view){
			
	}
	
	public void showDatePickerDialog(View view) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getSupportFragmentManager(),"datePicker");
	}
	
	public void showEventsbyZIP(View view) {
		Intent intent = new Intent(this, EventsActivity.class);
		EditText editText = (EditText) findViewById(R.id.enter_zip_code);
		String query = editText.getText().toString();
		String fullquery = "https://www.phillykeyspots.org/events.xml/?distance[postal_code]=" + query;
		intent.putExtra("EXTRA_MESSAGE", fullquery);
		startActivity(intent);
	}
	
	public void onRadioClicked(View view){
		//Which view is checked.
		boolean checked = ((RadioButton) view).isChecked();
		String query = "https://www.phillykeyspots.org/events.xml/term/";

		switch(view.getId()){
			case R.id.checkbx_all_levels:
				if(checked){
					query = query + "All%20Levels";}
				break;
			case R.id.checkbx_first_time:
				if(checked){
					query = query + "First-time";}
				break;
			case R.id.checkbx_beginner:
				if(checked){
					query = query + "Beginner";}
				break;
			case R.id.checkbx_intermediate:
				if(checked){
					query = query + "Intermediate";}
				break;
			case R.id.checkbx_advanced:
				if(checked){
					query = query + "Advanced";}
				break;
			case R.id.checkbx_tech_expert:
				if(checked){
					query = query + "Tech Expert";}
				break;
			case R.id.checkbx_web_access:
				if(checked){
					query = query + "Web%20Access";}
				break;
			case R.id.checkbx_computer_basics:
				if(checked){
					query = query + "Computer%20Basics";}
				break;
			case R.id.checkbx_internet_basics:
				if(checked){
					query = query + "Internet%20Basics";}
				break;
			case R.id.checkbx_ms_office:
				if(checked){
					query = query + "MS%20Office";}
				break;
			case R.id.checkbx_social_media:
				if(checked){
					query = query + "Social%20Media";}
				break;
			case R.id.checkbx_job_search:
				if(checked){
					query = query + "Job%20Search";}
				break;
		}
		Intent intent = new Intent(this, EventsActivity.class);
		intent.putExtra("EXTRA_MESSAGE_KEYSPOTS", query);
		startActivity(intent);
	}
	
	public void showKEYSPOTS(View view) {
		Intent intent = new Intent(this, KEYSPOTSActivity.class);
		String fullquery = "https://www.phillykeyspots.org/keyspots.xml"; 
		intent.putExtra("EXTRA_MESSAGE_KEYSPOTS", fullquery);
		startActivity(intent);
	}

}
