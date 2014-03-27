package com.devtechdesign.gpshare.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class GetSingleImg {
	
public static Bitmap GetSingleImg(String imgName, int requiredSize, String path){
		
		Bitmap bmp = null; 
		File root = Environment.getExternalStorageDirectory(); 
		BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inSampleSize = 8;
        
		// will results in a much smaller image than the original
	
		options.inSampleSize = 8;  
		 
	
		 File imgFile = new File(path + imgName); 
		 
			
		 bmp = decodeFileImg(imgFile, requiredSize); 
	
		// do something with bitmap } \
		
		return bmp;
	}
	
	//decodes image and scales it to reduce memory consumption private 
			private static Bitmap decodeFileImg(File f, int requiredSize){     
							try {       
								//Decode image size        
								BitmapFactory.Options o = new BitmapFactory.Options();      
								o.inJustDecodeBounds = true;    
								BitmapFactory.decodeStream(new FileInputStream(f),null,o); 
								//The new size we want to scale to      
								final int REQUIRED_SIZE = requiredSize;         
								//Find the correct scale value. It should be the power of 2.  
								int width_tmp=o.outWidth, height_tmp=o.outHeight;       
								int scale=1;  
				      while(true){     
				    	  if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)    
				            break;            
				    	  width_tmp/=2;        
				    	  height_tmp/=2;         
				    	  scale*=2;  
				      }      
				      //Decode with inSampleSize     
				      BitmapFactory.Options o2 = new BitmapFactory.Options(); 
				      o2.inSampleSize=scale;    
				      return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);  
				      } catch (FileNotFoundException e) 
				      {}     
				return null; 
			}
			
	
}
