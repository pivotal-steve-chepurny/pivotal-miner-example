package com.ltcminer.miner;
import static com.ltcminer.miner.Constants.*;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.ltcminer.miner.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class NewsFragment extends Fragment {

	WebView _webview;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.activity_news, container, false);
		
		SharedPreferences settings = getActivity().getSharedPreferences(PREF_TITLE, 0);
		SharedPreferences.Editor editor = settings.edit();
		
//		TextView tv = (TextView) rootView.findViewById(R.id.news_textView_news);
//		tv.setText("");
//		tv=(TextView) rootView.findViewById(R.id.news_textView_status);
		
		
		_webview = (WebView) rootView.findViewById(R.id.news_webview);
		String summary = "";
		_webview.loadData(summary, "text/html", null);
		
		// Check for network
		ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		
		// On Network
		if (networkInfo != null && networkInfo.isConnected()) {
//	        tv.setText("Downloading");
	        //tv=(TextView) rootView.findViewById(R.id.news_textView_news);
			//tv.setText(downloadURL(DEFAULT_NEWS_URL));
			
			_webview.loadData("Downloading", "text/html", null);
			new DownloadNewsTask().execute(null,null,null);
	    } 
		
		// Off Network
		else {
//	        tv = (TextView) rootView.findViewById(R.id.news_textView_status);
//	        tv.setText("No Network");
			_webview.loadData("No Network", "text/html", null);
	    }
		
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(getActivity(),
				       	R.array.nav, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		if(settings.getBoolean(PREF_NEWS_RUN_ONCE, false)==false)
		{
			// news has been run once, turn goto news on first app run off
			editor.putBoolean(PREF_NEWS_RUN_ONCE, true);
			editor.commit();
		}
		return rootView;
	}
	
	private String downloadURL(String url_string)  {
		Log.i("LC", "in downloadURL");
	    InputStream is = null;
	    InputStreamReader isr=null;
	    BufferedReader br = null;
	    String response=null;
	   
	    char[] buffer = new char[DEFAULT_NEWS_DL_BUFFER];
	    
	    try {
	    	URL url = new URL(url_string);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(10000 /* milliseconds */);
	        conn.setConnectTimeout(15000 /* milliseconds */);
	        conn.setRequestMethod("GET");
	        conn.setDoInput(true);
	        conn.connect();
	        int response_code = conn.getResponseCode();
	        Log.i("LC", "NewsActivity:downloadURL:response"+response_code);
	        
	        if(response_code==200) // OK
	        {
	        	is = conn.getInputStream();
	 	        Reader reader = new InputStreamReader(is, "UTF-8");        
	 	        reader.read(buffer);
	 	        response = new String(buffer);
	 	        
	 	        //find "EOF" and remove anything after, (including "EOF")
	 	        int EOF = response.indexOf("EOF");
	 	        response=String.valueOf(response.toCharArray(), 0, EOF);
	 	        
	 	        Log.i("LC","Response="+response);
	        }
	        else // something has gone wrong
	        {
	        	response="ERROR";
	        }

	    } catch (Exception e) {
	    Log.i("LC", "NewsActivity:downloadURL:Exception1: "+e.toString());
	    e.printStackTrace();
	    
	    
	    // Makes sure that the InputStream is closed after the app is
	    // finished using it.
	    } finally {
	        if (is != null) {
	            try {
					is.close();
				} catch (IOException e) {
					Log.i("LC", "NewsActivity:downloadURL:Exception2: "+e.getMessage());
				}
	        } 
	    }
	    return response;
	}
	
	private class DownloadNewsTask extends AsyncTask<Object, String, String> {

		protected String doInBackground(Object... params) {
			// TODO Auto-generated method stub
			return downloadURL(DEFAULT_NEWS_URL);
		}

		
		protected void onPostExecute(String result) {
			if(result == null) {
				return;
			}
			SharedPreferences settings = getActivity().getSharedPreferences(PREF_TITLE, 0);
			SharedPreferences.Editor editor = settings.edit();
			TextView tv_status = (TextView) getView().findViewById(R.id.news_textView_status);
			String saved_news = settings.getString("PREF_NEWS", "");
			
			// Can't retrieve news 
			if (result.equals("ERROR")==true) {	
				tv_status.setText("Unable to update"); // Set status
				
				// No news saved, set news
				if(saved_news.equals("")==true) { 
					_webview.loadData("No saved news", "text/html", null);
				} 
				
				else { _webview.loadData(saved_news, "text/html", null); }	 // Load the saved news
			}
			
			// News downloaded, if different from saved, update
			else 
			{
				tv_status.setText("Up to date");
				_webview.loadData(result, "text/html", null);
			
			if(saved_news.equals(result)==false) {
				editor.putString(PREF_NEWS, result); }
			
			}
			super.onPostExecute(result);
		}
	}

}
