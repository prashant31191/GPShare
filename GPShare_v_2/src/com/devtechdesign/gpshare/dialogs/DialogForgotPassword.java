package com.devtechdesign.gpshare.dialogs;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.devtechdesign.gpshare.Globals;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.data.db.RouteDatabase.AllRoutes;
import com.devtechdesign.gpshare.elements.aRoute;
import com.devtechdesign.gpshare.elements.aUser;
import com.devtechdesign.gpshare.utility.SoapInterface;

public class DialogForgotPassword extends Dialog implements SoapInterface {

	Context mContext;
	Dialog routeTagDialog;
	String currentPersonId = "";
	Boolean facebookTokenValidated = false;
	String routeId, routeKey, currentImgPath;
	Transactions TransactionSet;

	public DialogForgotPassword(final Context context) {

		super(context);

		LayoutInflater factory = LayoutInflater.from(context);

		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		final View txtEmail = factory.inflate(R.layout.fb_route_description_dialog, null);
		alert.setTitle("Type Email Address");
		// alert.setMessage("Enter Pin :");
		alert.setView(txtEmail);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, int whichButton) {
				// String value = input.getText().toString();

				EditText etEmail = (EditText) txtEmail.findViewById(R.id.txtDescription);
				final String decryptedEmail = ((EditText) etEmail).getText().toString();

				try {
					new AsyncTask<Object, Object, Object>() {

						@Override
						protected void onPostExecute(Object result) {
							Toast.makeText(context, "Email Sent", 2).show();
							dialog.dismiss();
							super.onPostExecute(result);
						}

						@Override
						protected Object doInBackground(Object... arg0) {
							aUser tempUser = null;
							try {
								tempUser = Transactions.getPassworFromServer(Transactions.encrypt(decryptedEmail));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Transactions.sendEmail(decryptedEmail, decryptedEmail, "Your GPShare Password is -->> " + tempUser.getDecryptedPassword(), "",
									"GPShare Password Retrieval");
							return null;
						}
					}.execute("");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
		}).show();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}
}
