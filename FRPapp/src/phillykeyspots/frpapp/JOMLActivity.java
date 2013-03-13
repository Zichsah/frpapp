package phillykeyspots.frpapp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.TextView;

/**
 * This Activity displays a form for the user to join the 
 * FRP mailing list. Is uses a http POST method to send information
 * to the server and displays a success message if information is successfully sent
 * 
 * @author btopportal
 *
 */

public class JOMLActivity extends Activity {

	public List< NameValuePair > jomlPairs = new ArrayList < NameValuePair > (6);
	public ProgressDialog progress;
	/**
	 * When the activity is created the layout for the page is loaded and 
	 * the data passed to the activity is fetched thought getStringExtra
	 * @author btopportal
	 * @param savedInstanceState - all the data that the application has saved
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		progress = ProgressDialog.show(JOMLActivity.this,"","Sending",true);
		Intent intent_evnt = getIntent();
        String email = intent_evnt.getStringExtra("joml_email");
        String fname = intent_evnt.getStringExtra("joml_fname");
        String lname = intent_evnt.getStringExtra("joml_lname");
        String zipcode = intent_evnt.getStringExtra("joml_zipcode");
        String phone = intent_evnt.getStringExtra("joml_phone");
        String option = intent_evnt.getStringExtra("joml_option");
        
        pairJomlData(email,fname,lname,zipcode,phone,option);
	}
	/**
	 * pairJomlData puts data entered into a list array that contains basic name value pairs
	 * and then calls the postJomlFormData which send the information via HTTP requests
	 * 
	 * @author btopportal
	 * @param email
	 * @param fname
	 * @param lname
	 * @param zipcode
	 * @param phone
	 * @param option
	 * @see JOMLActivity#postJomlData(String)
	 */
	public void pairJomlData(String email, String fname, String lname, String zipcode, String phone, String option) {
		jomlPairs.add(new BasicNameValuePair("submitted[email]", email));
		jomlPairs.add(new BasicNameValuePair("submitted[first_name]", fname));
		jomlPairs.add(new BasicNameValuePair("submitted[last_name]", lname));
		jomlPairs.add(new BasicNameValuePair("submitted[zip_code]", zipcode));
		jomlPairs.add(new BasicNameValuePair("submitted[phone_]", phone));
		jomlPairs.add(new BasicNameValuePair("submitted[kind]", option));
		jomlPairs.add(new BasicNameValuePair("details[sid]",""));
		jomlPairs.add(new BasicNameValuePair("details[page_num]","1"));
		jomlPairs.add(new BasicNameValuePair("details[page_count]","1"));
		jomlPairs.add(new BasicNameValuePair("details[finished]","0"));
		jomlPairs.add(new BasicNameValuePair("form_build_id","form-yMLXWyHB8cnM9vzPMSjfFO2_BsfWFCss2B_dDq2TUVM"));
		jomlPairs.add(new BasicNameValuePair("form_token","F34-ENkLzPv8tz_vlyQm94zId3oHoBywZcycUp-DFFs"));
		jomlPairs.add(new BasicNameValuePair("form_id","webform_client_form_78"));
		jomlPairs.add(new BasicNameValuePair("op","Submit"));
		
		new postJomlFormData().execute("https://www.phillykeyspots.org/content/join-our-mailing-list");
	}
	
	/**
	 * postJomlFormData uses an AsyncTask to run the POST request in the 
	 * background, and displays a success message if the POST was successful
	 * 
	 * @author btopportal
	 */
	private class postJomlFormData extends AsyncTask<String,Void,String>{
		
		@Override
		protected String doInBackground(String...urls){
			try {
				return postJomlData(urls[0]);
			} catch (IOException e) {
				return getResources().getString(R.string.connection_error);
			} catch (XmlPullParserException e) {
				return getResources().getString(R.string.xml_error);
			}
		}
		@Override
		protected void onPostExecute(String result){
			progress.dismiss();
			setContentView(R.layout.activity_joml);
			TextView myTextView = (TextView) findViewById(R.id.jomlResponse);
	        myTextView.setText(result);
		}
	}
	
	private String postJomlData(String urlString) throws XmlPullParserException, IOException {		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(urlString);
		String responseBody = "Please submit your information again";
		try {
			httppost.setEntity(new UrlEncodedFormEntity(jomlPairs));
			try {
				HttpResponse response = httpclient.execute(httppost);
				responseBody = EntityUtils.toString(response.getEntity());
				return responseBody = "Thanks for joining our mailing list " + jomlPairs.get(1).getValue() + ". The email has been sent to the respective partners.";
			} catch (ClientProtocolException e){
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return responseBody;
	}

}
