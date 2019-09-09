package org.knoesis.url.extractions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpConnection;
import org.json.JSONException;
import org.json.JSONObject;
import org.knoesis.url.extractions.*;

/**
 * The objective of this class is to use the API
 * http://api.longurl.org/v2/expand
 * to expand the short URLs to the corresponding original URLs.
 * This class is coded because the previous class did not do recursive expansion of URLs
 * 
 * 
 * @author pavan
 *
 */
public class APIURLResolver implements Extractor<String> {
	Log LOG = LogFactory.getLog(APIURLResolver.class);

	@Override
	public String extract(Object text) {
		// TODO Auto-generated method stub
		HttpURLConnection conn;
		BufferedReader rd;
		String result="";
		String longUrl=(String)text;
		String line;
		try {
			URL url = new URL("http://api.longurl.org/v2/expand?format=json&url="+(String)text.toString());
			conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while((line = rd.readLine()) != null){
				result+=line;
			}
			JSONObject json = new JSONObject(result);
			longUrl = (String)json.get("long-url");
			rd.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			//TODO: Log Stuff here 
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return longUrl;
	}



	public static void main(String[] args) {
		Extractor resolver = new APIURLResolver();
		try {
			Scanner reader = new Scanner(new File(args[0]));
			FileWriter write = new FileWriter(new File("modiAnalysis/url_analysis/longUrls"));
			FileWriter writeDomains = new FileWriter(new File("modiAnalysis/url_analysis/longUrls_domains"));
			while(reader.hasNext()){
				String shortUrl = reader.nextLine();
				String longUrl = (String) resolver.extract(shortUrl);
				URL l = new URL(longUrl);
				System.out.println(shortUrl+"\t"+longUrl);
				write.append(shortUrl+"\t"+longUrl+"\n");
				writeDomains.append(l.getHost()+"\n");
			}
			write.close();
			writeDomains.close();
		} catch (FileNotFoundException e) {
			// TODO L
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	@Override
	public void process(URLModel urlModel) {
		try {
			urlModel.setOriginalURL(new URL(this.extract(urlModel.getShortURL().toString())));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
