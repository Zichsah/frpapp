package phillykeyspots.frpapp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class ResourcesFragment extends Fragment {

	private ResourcesLoader loader;
	private WebView view;
	private ProgressDialog progress;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resources, container, false);
    }
	public void onStart(){
		super.onStart();
		view = (WebView)getActivity().findViewById(R.id.resources_webview);
	}
	
	public void onResume(){
		super.onResume();
		progress = ProgressDialog.show(getActivity(), "", "Loading...");
		loader = new ResourcesLoader();
		loader.execute();
	}
	private void loadOurData(String data){
		view.loadData(data, "text/html", null);
	}
	
	public void switchTab(int ID){
		switch(ID){
		case (R.id.resources_career):
			loadOurData(loader.career);
			break;
		case (R.id.resources_computer_training):
			loadOurData(loader.computer_training);
			break;
		case (R.id.resources_public_services):
			loadOurData(loader.public_services);
			break;
		}
	}
	
	public class ResourcesLoader extends AsyncTask<String, Void, String>{

		public String career, computer_training, public_services;
		private Document doc;
		
		private void Load(String url){
			try{
				doc = Jsoup.connect(url).get();	
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		@Override
		protected String doInBackground(String... params) {
			if(doc == null){
				Load("https://www.phillykeyspots.org/resources");
			}
			return null;
		}
		
		protected void onPostExecute(String result){
			career = doc.select("div#career_list").get(0).html();
			computer_training = doc.select("div#computer_training_list").get(0).html();
			public_services = doc.select("div#public_services_list").get(0).html();
			loadOurData(career);
			progress.dismiss();
		}
	}
}
