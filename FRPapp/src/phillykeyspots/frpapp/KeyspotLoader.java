package phillykeyspots.frpapp;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import phillykeyspots.frpapp.XmlParser;
import phillykeyspots.frpapp.XmlParser.Entry;

/**
 * Class that gathers the information from the web site for the Keyspots.
 * 
 * @author btopportaldev
 *
 */

public class KeyspotLoader {

	private List<Entry> entries;
	private XmlParser parser;
	private InputStream stream = null;
	private URL url;
	
	public KeyspotLoader(){
		reload("19104");
	}
	
	/**
	 * Connects to the web site and parses the information into enties.
	 * 
	 * @return A List of Entries.
	 */
	public List<Entry> reload(String zip){
		entries = null;
		parser = new XmlParser();
		try{
			url = new URL("https://www.phillykeyspots.org/keyspot-mobile-map.xml/"+zip+"_2");
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /*milliseconds*/);
			conn.setConnectTimeout(15000 /*milliseconds*/);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			stream = conn.getInputStream();
			entries = parser.parse(stream);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		finally {
			if (stream != null){
				try {
					stream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return entries;
	}
	
	public List<Entry> getEntries(){
		return entries;
	}
}
