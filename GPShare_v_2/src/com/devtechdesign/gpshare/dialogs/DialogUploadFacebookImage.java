package com.devtechdesign.gpshare.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.devtechdesign.gpshare.facebook.FaceBookLogin;
import com.devtechdesign.gpshare.facebook.SessionStore;
import com.devtechdesign.gpshare.facebook.UploadFaceBookImage;
import com.devtechdesign.gpshare.utility.Utility;
import com.facebook.android.Util;

public class DialogUploadFacebookImage extends Dialog {

	Context mContext; 
	String currentImgPath,actualImageForUpload; 
	
	public DialogUploadFacebookImage(Context context,final String currentImgPath, final String actualImageForUpload) {
		super(context);
		
		this.currentImgPath = currentImgPath; 
		this.actualImageForUpload =actualImageForUpload; 
		mContext = context;
		if (!Utility.mFacebook.isSessionValid()) {

	      
	    	//  dialog = ProgressDialog.show(mContext, "", getString(R.string.please_wait), true, true);
	          new AlertDialog.Builder(mContext)
	                  .setTitle("Login")
	                  .setMessage("Log into Facebook")
	                  .setPositiveButton("Login",
	                          new DialogInterface.OnClickListener() {
	                              @Override
	                              public void onClick(DialogInterface dialog, int which) {
	                              dialog.dismiss(); 
	                              Intent facebookLogin = new Intent(mContext, FaceBookLogin.class); 
	                              facebookLogin.putExtra("currentImgPath", currentImgPath); 
	                             
	                              mContext.startActivity(facebookLogin); 
	                             
	                              }
	                          })
	                  .setNegativeButton("Cancel",
	                          new DialogInterface.OnClickListener() {
	                              @Override
	                              public void onClick(DialogInterface dialog, int which) {
	                               dialog.dismiss(); 
	                              }

	                          }).setOnCancelListener(new DialogInterface.OnCancelListener() {
	                      @Override
	                      public void onCancel(DialogInterface d) {
	                    	  d.dismiss();
	                      }
	                  }).show();
	          
	} else {

	Intent intent = new Intent(mContext, UploadFaceBookImage.class);
	intent.putExtra("currentImgPath", currentImgPath); 
	intent.putExtra("actualImageForUpload", actualImageForUpload); 
	mContext.startActivity(intent); 

	}
	// Intent intent = new Intent(mContext, FriendsList.class);
	//mContext.startActivity(intent); 

	//showPhoneContactListDialog(); 
	//showFacebookFriendDialog(); 
	}
}
