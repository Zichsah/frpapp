package phillykeyspots.frpapp;

import android.os.Bundle;
import android.app.Activity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
/**
 * The AboutUsActivity is a static page that displays 
 * information about phillyKEYSPOTS in an accordion style.
 * 
 * @author btopportal
 */
public class AboutUsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		
		/**
		 * Making TextView links clickable
		 * @author btopportal
		 */
		TextView panel5 = (TextView) findViewById(R.id.about5);
		panel5.setMovementMethod(LinkMovementMethod.getInstance());
	}
	
	public void changePanelsAboutus(View view){
		TextView p1 = (TextView)findViewById(R.id.about1);
		TextView p2 = (TextView)findViewById(R.id.about2);
		TextView p3 = (TextView)findViewById(R.id.about3);
		TextView p4 = (TextView)findViewById(R.id.about4);
		TextView p5 = (TextView)findViewById(R.id.about5);
		switch(view.getId()){
		case(R.id.b_about1):
			p1.setVisibility(View.VISIBLE);
			p2.setVisibility(View.GONE);
			p3.setVisibility(View.GONE);
			p4.setVisibility(View.GONE);
			p5.setVisibility(View.GONE);
			break;
		case(R.id.b_about2):
			p2.setVisibility(View.VISIBLE);
			p3.setVisibility(View.GONE);
			p4.setVisibility(View.GONE);
			p5.setVisibility(View.GONE);
			p1.setVisibility(View.GONE);
			break;
		case(R.id.b_about3):
			p3.setVisibility(View.VISIBLE);
			p4.setVisibility(View.GONE);
			p5.setVisibility(View.GONE);
			p1.setVisibility(View.GONE);
			p2.setVisibility(View.GONE);
			break;
		case(R.id.b_about4):
			p4.setVisibility(View.VISIBLE);
			p5.setVisibility(View.GONE);
			p1.setVisibility(View.GONE);
			p2.setVisibility(View.GONE);
			p3.setVisibility(View.GONE);
			break;
		case(R.id.b_about5):
			p5.setVisibility(View.VISIBLE);
			p1.setVisibility(View.GONE);
			p2.setVisibility(View.GONE);
			p3.setVisibility(View.GONE);
			p4.setVisibility(View.GONE);
		}
		
	}

}
