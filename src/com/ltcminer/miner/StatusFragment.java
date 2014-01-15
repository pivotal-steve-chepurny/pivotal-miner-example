package com.ltcminer.miner;

import static com.ltcminer.miner.Constants.*;

import java.text.DecimalFormat;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.ltcminer.miner.R;

public class StatusFragment extends Fragment {
	private static int updateDelay=1000;
	String unit = " h/s";
	
	Handler statusHandler = new Handler() { };
	
	final Runnable rConsole = new Runnable() {
        public void run() { 
        	//Log.i("LC", "StatusActivity:updateConsole:"+MiningController.getMinerService().console.getConsole());
			if (!MiningController.isBackgrounded && getView() != null) {
				TextView txt_console = (TextView) getView().findViewById(R.id.status_textView_console);
				txt_console.setText(MiningController.getMinerService().cString);
				txt_console.invalidate();
        	}
        } 
    };
        
    final Runnable rSpeed = new Runnable() {
       public void run() { 
    	   // Log.i("LC", "StatusActivity:updateSpeed");
    	   if(!MiningController.isBackgrounded && getView() != null) {
           	TextView tv_speed = (TextView) getView().findViewById(R.id.status_textView_speed);
           	DecimalFormat df = new DecimalFormat("#.##");
           	tv_speed.setText(df.format(MiningController.getMinerService().speed*1000)+unit);
    	   }
       } 
    };
    final Runnable rAccepted = new Runnable() {
       public void run() {
    	   // Log.i("LC", "StatusActivity:updateAccepted");
    	   if(!MiningController.isBackgrounded && getView() != null) {
               
            TextView txt_accepted = (TextView) getView().findViewById(R.id.status_textView_accepted);
            txt_accepted.setText(String.valueOf(MiningController.getMinerService().accepted));
    	   }
       } 
    };
    final Runnable rRejected = new Runnable() {
        public void run() { 
        	if(!MiningController.isBackgrounded && getView() != null) {
                
        	// Log.i("LC", "StatusActivity:updateRejected");
             TextView txt_rejected = (TextView) getView().findViewById(R.id.status_textView_rejected);
             txt_rejected.setText(String.valueOf(MiningController.getMinerService().rejected));
        	}
        } 
     };
     final Runnable rStatus = new Runnable() {
         public void run() {
        	//  Log.i("LC", "StatusActivity:updateStatus");
        	 if(!MiningController.isBackgrounded && getView() != null) {
                 
              TextView txt_status = (TextView) getView().findViewById(R.id.status_textView_status);
              txt_status.setText(MiningController.getMinerService().status);
        	 }
         } 
      };
      final Runnable rBtnStart= new Runnable() { 
    	  public void run() { 
    		  if(!MiningController.isBackgrounded && getView() != null) {
    	           
    		 // Log.i("LC", "StatusActivity: Miner stopped, changing button to start");
    		  Button b = (Button) getView().findViewById(R.id.status_button_startstop);
    		  b.setText(getString(R.string.main_button_start));
    		  b.setEnabled(true);
    		  }
    	  }
      };
      final Runnable rBtnStop= new Runnable() { 
    	  public void run() { 
    		  if(!MiningController.isBackgrounded && getView() != null) {
    	           
    		  //Log.i("LC", "StatusActivity: Miner stopped, changing button to stop");
    		  Button b = (Button) getView().findViewById(R.id.status_button_startstop);
    		  b.setText(getString(R.string.main_button_stop));
    		  b.setEnabled(true);
    		  }
    	  }
      };
       
       

	Thread updateThread = new Thread () { 
		public void run() {	
			Log.i("LC", "StatusActivity: Update thread started");
			// wait for service to bind
			while (!MiningController.isBound())
			{
				try {
					sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					Log.i("LC", "StatusActivity:updateThread: Interrupted");
				}
			}
			
			// If the service is running make sure the button is changed to "Stop Mining" and vice versa
			if(MiningController.getMinerService().running==true) { statusHandler.post(rBtnStop); }
			else { statusHandler.post(rBtnStart); }

			while (MiningController.isBound())	{	
				try {
					sleep(updateDelay);
				} catch (InterruptedException e) {
					Log.i("LC", "StatusActivity:updateThread: Interrupted");
				}
				
			statusHandler.post(rConsole);
			statusHandler.post(rSpeed);
			statusHandler.post(rAccepted);
			statusHandler.post(rRejected);
			statusHandler.post(rStatus);
			if(MiningController.getMinerService().running==true) { statusHandler.post(rBtnStop); }
			else {statusHandler.post(rBtnStart);
			}
		} }
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.activity_status, container, false);
		
		SharedPreferences settings = getActivity().getSharedPreferences(PREF_TITLE, 0);
    	
    	Log.i("LC", "Status: onCreate");
    	
//    	Intent intent = new Intent(getActivity().getApplicationContext(), MinerService.class);
//    	getActivity().startService(intent);
//    	getActivity().bindService(intent, MiningController.getServiceConnection(), Context.BIND_AUTO_CREATE);
    	
    	Button btn_startStop = (Button) rootView.findViewById(R.id.status_button_startstop);
    	
    	//Setup nav spinner
    	ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.nav, android.R.layout.simple_spinner_item);
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
    		
    	// Set Button Click Listener
        btn_startStop.setOnClickListener(new Button.OnClickListener() {
    
			public void onClick(View v) {
				Button b = (Button) v;
				
				if (b.getText().equals(getString(R.string.status_button_start))==true){
					MiningController.startMining();
					b.setText(getString(R.string.main_button_stop));
				}
				else{
					MiningController.stopMining();
					b.setText(getString(R.string.status_button_start));
				}
			}

        });
        

        updateThread.start();
		return rootView;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	
        
//        // Launch news on first run
//    	if(settings.getBoolean(PREF_NEWS_RUN_ONCE, false)==false)
//    	{
//    		intent = new Intent(getActivity().getApplicationContext(), NewsActivity.class);
//			startActivity(intent);
//    	}
	}
	
	

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
		super.onPause();
	}


	@Override
	public void onResume() {
		SharedPreferences settings = getActivity().getSharedPreferences(PREF_TITLE, 0);
        if (settings.getBoolean(PREF_BACKGROUND, DEFAULT_BACKGROUND)==true)
        {
        	TextView tv_background = (TextView) getView().findViewById(R.id.status_textView_background);
        	tv_background.setText("RUN IN BACKGROUND");
        }
		super.onResume();
	}



	@Override
	public void onStop() {
		if(updateThread.isAlive()==true) { updateThread.interrupt(); }
		super.onStop();
	}
	
}
