package com.devtechdesign.gpshare.Images;

import java.io.File;
import java.util.ArrayList;

import com.devtechdesign.gpshare.Globals;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class UploadService extends Service {
	 private Handler handler;
	 private int filesToUploadCount = 0; 
	 private String [] filesToUpload; 
	 
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		 handler = new Handler();
	
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	 @Override 
	   public void onStart(Intent intent, int startId) { 
	      super.onStart(intent, startId); 
	      
         Bundle extras = intent.getExtras();
         filesToUpload = new String[1]; 
         filesToUpload =  extras.getStringArray("fileNames"); 
       
         try{
    		 uploadImages();
    		 }
    		 catch(Exception e)
    		 {
    			 
    		 }
	 }
	 
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	private void uploadImages() {
		
		//String newFolder = "/Geocam/Upload";
		
		//String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
		//File uploadFolder = new File(extStorageDirectory + newFolder);
		String[] list = filesToUpload; 
		final ArrayList <String> listToSent = new ArrayList<String>();
		for(int u = 0; u < list.length; u++)
		
		listToSent.add(list[u]);
		filesToUploadCount++;
			
		if (list.length > 0)
		createNotification(listToSent);
		
		//upload GPX after images finish
		//uploadGpx(); 
	}
	
	private void uploadGpx() {
		String newFolder = "/Geocam/Routes";
		String extStorageDirectory = Environment.getExternalStorageDirectory()
				.toString();
		File uploadFolder = new File(extStorageDirectory + newFolder);
		String[] list = uploadFolder.list();
		final ArrayList <String> listToSent = new ArrayList<String>();
		for(int u = 0; u < list.length; u++)
			if(list[u].charAt(0) != 'X')
			{
				listToSent.add(list[u]);
				filesToUploadCount++; 
			}
		if (list.length > 0)
		createNotification(listToSent);
	}
	
	private void createNotification(final ArrayList<String> files) {
		handler.post(new Runnable() {
		
			public void run() { 
				
				if(filesToUploadCount != 0)
				{
					new UploadPicture(getApplicationContext(), files, 0, handler).execute(0);
				}
				
			}
		});
    }
	
	public void makeToast(String message){
    	Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
	
	public void syncRouteDbs()
	{
		String [] routeList = Globals.getrouteList(); 
		
		for(int i = 0; i < routeList.length; i++)
		{
			
		}
	}
	
}
