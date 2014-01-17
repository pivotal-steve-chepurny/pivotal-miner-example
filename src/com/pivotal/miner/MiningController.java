package com.pivotal.miner;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.pivotal.miner.MinerService.LocalBinder;

import android.util.Log;

public class MiningController {
    private static boolean mBound = false;
	private static MinerService mService;
	public static boolean isBackgrounded = false;
	
	public int curScreenPos=0;
	
    private static ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i("LC", "Main: onServiceConnected()");
			LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound=true;
            Log.i("LC", "Main: Service Connected");
		}

		public void onServiceDisconnected(ComponentName name) {  mBound=false;   }
    };
    
    public static ServiceConnection getServiceConnection() {
    	return mConnection;
    }

	public static void startMining() {
		Log.i("LC", "Main: startMining()");
		mService.startMiner();
	}
	
	public static void stopMining() 
	{
		Log.i("LC", "Main: stopMining()");
		mService.stopMiner();
		
	}  
	
	public static MinerService getMinerService() {
		return mService;
	}
	
	public static boolean isBound() {
		return mBound;
	}
	
	public static void setIsBound(boolean isBound) {
		mBound = isBound;
	}
}
