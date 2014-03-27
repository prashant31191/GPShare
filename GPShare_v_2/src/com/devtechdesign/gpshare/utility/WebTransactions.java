package com.devtechdesign.gpshare.utility;

import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.devtechdesign.gpshare.elements.aRoute;

public class WebTransactions implements SoapInterface {

	static HttpTransportSE				androidHttpTransport	= new HttpTransportSE(GPShare_Svc_Url);
	static SoapSerializationEnvelope	envelope				= new SoapSerializationEnvelope(SoapEnvelope.VER11);

	public static boolean writeFacebookGraphRouteFile(aRoute cRoute) {
		JSONObject jsonRoute = new JSONObject();
		try {
			jsonRoute.put("routeId", cRoute.getRouteId());
			jsonRoute.put("routeKey", cRoute.getRouteKey());
			jsonRoute.put("routeDistance", cRoute.getDistance());
			jsonRoute.put("duration", cRoute.getElapsedTime());
			jsonRoute.put("description", cRoute.getDescription());
		} catch (JSONException e2) { 
			e2.printStackTrace();
		}

		SoapObject request = new SoapObject(NAMESPACE, writeFacebookGraphRouteFileMethodName);

		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		request.addProperty("aRouteString", jsonRoute.toString());
		try {

			androidHttpTransport.call(writeFacebookGraphRouteFile, envelope);
			String response = envelope.getResponse().toString();
			System.out.println("response: " + response);
			if (response.equals("True")) {
				return true;
			}

		} catch (SoapFault e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		return false;

	}

	public static boolean updatePublicProfileBool(String personId, String currentRankDisplayStatus) {

		SoapObject request = new SoapObject(NAMESPACE, updatePublicProfileBoolMethodName);

		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;
		request.addProperty("personId", personId);
		request.addProperty("currentRankDisplayStatus", currentRankDisplayStatus);
		try {

			androidHttpTransport.call(updatePublicProfileBoolAction, envelope);
			String response = envelope.getResponse().toString();

			if (response.equals("true")) {
				return true;
			}

		} catch (SoapFault e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		return false;

	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}
}
