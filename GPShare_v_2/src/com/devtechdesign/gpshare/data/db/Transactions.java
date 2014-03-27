package com.devtechdesign.gpshare.data.db;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;

import dev.tech.gpsharelibs.DTD;
import dev.tech.util.AuthOb;
import dev.tech.util.GLibTrans;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;

import com.devtechdesign.gpshare.GPShare;
import com.devtechdesign.gpshare.Globals;
import com.devtechdesign.gpshare.data.db.RouteDatabase.AllRoutes;
import com.devtechdesign.gpshare.data.db.RouteDatabase.Images;
import com.devtechdesign.gpshare.data.db.RouteDatabase.Places;
import com.devtechdesign.gpshare.data.db.RouteDatabase.Routes;
import com.devtechdesign.gpshare.data.web.WebTrans;
import com.devtechdesign.gpshare.elements.Image;
import com.devtechdesign.gpshare.elements.Route;
import com.devtechdesign.gpshare.elements.WeatherDay;
import com.devtechdesign.gpshare.elements.aPlace;
import com.devtechdesign.gpshare.elements.aRank;
import com.devtechdesign.gpshare.elements.aRoute;
import com.devtechdesign.gpshare.elements.aUser;
import com.devtechdesign.gpshare.elements.friendList;
import com.devtechdesign.gpshare.elements.friendRoute;
import com.devtechdesign.gpshare.elements.requestedFriend;
import com.devtechdesign.gpshare.utility.Base64;
import com.devtechdesign.gpshare.utility.DateNow;
import com.devtechdesign.gpshare.utility.GCMHelper;
import com.devtechdesign.gpshare.utility.SoapInterface;
import com.google.android.gcm.GCMRegistrar;

public class Transactions extends RouteDataBaseHelper implements SoapInterface {

	public final String PERSON_ID = "personId";

	public Transactions(Context context) {
		super(context);
	}

	public static DecimalFormat twoDForm = new DecimalFormat("#.##");
	public static SharedPreferences sharedPreferences;
	public static Editor editor;
	public String PROPERTY_REG_ID = "registration_id";
	public ArrayList<aPlace> placeListArray = new ArrayList<aPlace>();

	public void getQueryStringVariabls() {

	}

	public static String insertFbRecord(String userName, String currentImgPath) {
		// private void register(String user, String password, String validate)
		// {

		try {
			Image fetchedImage = Transactions.getaImageaFromPhoneDbImgPath(currentImgPath);
			String pmX = fetchedImage.getimgX();
			String pmY = fetchedImage.getimgY();

			SoapObject request = new SoapObject(NAMESPACE, TAGGED_IMG_RECORD_INSERT_METHOD_NAME);
			request.addProperty("UserName", userName);
			request.addProperty("PlaceName", fetchedImage.getimgPlace());
			request.addProperty("PmX", pmX);
			request.addProperty("PmY", pmY);
			request.addProperty("ImgUrl", currentImgPath);
			request.addProperty("personId", DTD.getPersonId());
			request.addProperty("createDate", fetchedImage.getimgCreateDate());

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url);

			try {

				androidHttpTransport.call(TAGGED_IMG_RECORD_INSERT_ACTION, envelope);
				System.out.println("envelope.getResponse().toString() " + envelope.getResponse().toString());
				return envelope.getResponse().toString();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void updateCurrentRouteSeg(String currentRouteDateKey, double lat, double lng, String routeName, String speed) {
		// mDb.beginTransaction();

		try {
			aRoute currentRoute = null;

			try {
				currentRoute = GPShare.transact.getARouteDynParam(AllRoutes.ALLROUTES_KEY + " = " + "'" + currentRouteDateKey + "'").get(0);
			} catch (IndexOutOfBoundsException e) {
			}

			try {
				String currentXyString = currentRoute.getXyString();

				// /Xystring Definition
				// /x,y,datetime,altitude,speed
				ContentValues xyStringUpdate = new ContentValues();
				currentXyString += lat + "," + lng + "," + GLibTrans.getDateTime() + "," + "" + "," + speed + ",";

				xyStringUpdate.put("allroutes_xy_string", currentXyString);

				if (routeName != null && !routeName.equals("")) {
					xyStringUpdate.put(AllRoutes.ALLROUTES_NAME, routeName);
				}

				mDb.update(AllRoutes.ALLROUTESS_TABLE_NAME, xyStringUpdate, AllRoutes.ALLROUTES_KEY + " = '" + currentRouteDateKey + "'", null);

			} finally {
				// mDb.endTransaction();
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<Image> getImagesFromPhone(int beginning, int end) {
		Cursor mCursor = Transactions.mDb.query(RouteDatabase.Images.IMAGE_TABLE_NAME, new String[] { Images.IMAGE_PATH, Images.IMAGE_PLACE_NAME, Images.IMAGE_ID, Images.IMAGE_X,
				Images.IMAGE_Y, Images.IMAGE_CREATEDATE, Images.IMAGE_EARTH_GALLERY, Images._ID }, null, null, null, null, null);

		int imgCount = mCursor.getCount();
		ArrayList<Image> imgList = new ArrayList<Image>();
		if (mCursor.moveToLast()) {

			for (int i = 0; i < imgCount; i++) {

				if ((i > beginning && i < end) || i == 0) {

					Image newImage = new Image();
					newImage.setImgType(OFFLINE_IMG);
					newImage.setId(i);
					System.out.println(" Images._ID" + mCursor.getColumnIndex(Images._ID));
					newImage.setImageId(mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_ID)));
					newImage.setImgCreateDate(mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_CREATEDATE)));
					newImage.setImgPath(mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_PATH)));
					newImage.setImgX(mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_X)));
					newImage.setImgY(mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_Y)));
					boolean earthGalBool = mCursor.getInt(mCursor.getColumnIndex(Images.IMAGE_EARTH_GALLERY)) > 0;
					newImage.setImgEarthGal(earthGalBool);

					imgList.add(newImage);
				}
				mCursor.moveToPrevious();
			}
		}
		return imgList;
	}

	public static void updateFirstSegOfRoute(double lat, double totalDistance, double lng, String speed, int elapsedTime, double pointCount, String firstSegRouteDateKey) {
		try {
			// mDb.beginTransaction();
			// /UPdate Route Stats at the First segment only.
			ContentValues routeUpdateStats = new ContentValues();
			routeUpdateStats.put("allroutes_x", String.valueOf(lat));
			routeUpdateStats.put("allroutes_y", String.valueOf(lng));
			routeUpdateStats.put("allroutes_distance", String.valueOf(totalDistance));
			routeUpdateStats.put("allroutes_pointCount", String.valueOf(pointCount));
			routeUpdateStats.put("allroutes_topSpeed", String.valueOf(speed));
			routeUpdateStats.put("allroutes_elapsedTime", elapsedTime);
			// routeUpdateStats.put(AllRoutes.ALLROUTES_NAME, routeName);

			mDb.update(AllRoutes.ALLROUTESS_TABLE_NAME, routeUpdateStats, AllRoutes.ALLROUTES_KEY + " = '" + firstSegRouteDateKey + "'", null);

		} catch (NullPointerException e) {

			// mDb.endTransaction();
		}
	}

	// public Transactions(Context context)
	// {
	// mDbHelper = new RouteDataBaseHelper(context);
	// mDb = mDbHelper.getWritableDatabase();
	// mContext = context;
	//
	// }

	public static String determineDataType(Image image) {
		switch (image.getImgType()) {
		case ONLINE_IMG:

			return "online";
		case OFFLINE_IMG:

			return "local";
		}
		return null;
	}

	public static void updatePlaceRouteCountByPlaceId(String placeName, int routeCount) {
		mDb.beginTransaction();
		try {

			ContentValues args = new ContentValues();
			args.put(Places.PLACES_Route_Count, routeCount);
			mDb.update(Places.PLACES_TABLE_NAME, args, Places.PLACES_NAME + " = '" + placeName + "'", null);
			mDb.setTransactionSuccessful();

		} finally {
			mDb.endTransaction();
		}
	}

	public static void updateCurrentRoutePlace(aRoute newRoute) {

		ContentValues args = new ContentValues();
		args.put(AllRoutes.ALLROUTES_PLACE, newRoute.getPlaceName());

		if (!newRoute.getRouteName().equals("")) {

			args.put(AllRoutes.ALLROUTES_NAME, newRoute.getRouteName());
		}

		mDb.update(AllRoutes.ALLROUTESS_TABLE_NAME, args, AllRoutes.ALLROUTES_SegmentKey + " = '" + newRoute.getSegmentKey() + "'", null);

	}

	public static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	public static ArrayList<WeatherDay> weatherAryY;

	public ArrayList<WeatherDay> getWeatherReport() {
		String x = null;
		String y = null;
		boolean currenLocationKnown = false;
		try {
			String[] xyAry = Globals.getcurrentCoords().split(",");
			x = xyAry[0];
			y = xyAry[1];
			currenLocationKnown = true;
		} catch (NullPointerException e) {

		}
		if (currenLocationKnown) {
			HttpClient httpclient = new DefaultHttpClient();

			// Prepare a request object 38.99,-77.02 " + x + "," + y + "
			HttpGet httpget = new HttpGet(
					"http://graphical.weather.gov/xml/sample_products/browser_interface/ndfdBrowserClientByDay.php?listLatLon=38.99,-77.02&format=24+hourly&numDays=7");

			// Execute the request
			HttpResponse response;
			try {
				response = httpclient.execute(httpget);
				// Examine the response status

				// Get hold of the response entity
				HttpEntity entity = response.getEntity();
				// If the response does not enclose an entity, there is no need
				// to worry about connection release

				if (entity != null) {

					// A Simple JSON Response Read
					InputStream instream = entity.getContent();
					String result = convertStreamToString(instream);

					// parse xml response
					weatherAryY = parseDocument(result);

					// Closing the input stream will trigger connection release
					instream.close();
				}

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return weatherAryY;
	}

	public static ArrayList<WeatherDay> getCurrentAddress() {
		String x = null;
		String y = null;
		boolean currenLocationKnown = false;
		try {
			String[] xyAry = Globals.getcurrentCoords().split(",");
			x = xyAry[0];
			y = xyAry[1];
			currenLocationKnown = true;
		} catch (NullPointerException e) {

		}
		if (currenLocationKnown) {
			HttpClient httpclient = new DefaultHttpClient();

			HttpGet httpget = new HttpGet("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=33.8670522,151.1957362");

			// Execute the request
			HttpResponse response;
			try {
				response = httpclient.execute(httpget);
				// Examine the response status

				// Get hold of the response entity
				HttpEntity entity = response.getEntity();
				// If the response does not enclose an entity, there is no need
				// to worry about connection release

				if (entity != null) {

					// A Simple JSON Response Read
					InputStream instream = entity.getContent();
					String result = convertStreamToString(instream);

					// parse xml response
					// weatherAryY = parseDocument(result);

					// Closing the input stream will trigger connection release
					instream.close();
				}

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return weatherAryY;
	}

	public static org.w3c.dom.Document loadXMLFromString(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
	}

	static String outputString = "";

	private ArrayList<WeatherDay> parseDocument(String xml) throws Exception {
		outputString = "";
		Document doc = loadXMLFromString(xml);
		doc.getDocumentElement().normalize();

		NodeList nList = doc.getElementsByTagName("temperature");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");

		String[] icons = new String[7];
		String[] minTemps = new String[7];
		String[] maxTemps = new String[7];
		String[] days = new String[7];
		int minTempCount = 0;
		int maxTempCount = 0;
		int iconCount = 0;
		int dayCount = 0;

		NodeList listDates = doc.getElementsByTagName("time-layout");
		NodeList dateNodelist = ((org.w3c.dom.Node) listDates.item(0)).getChildNodes();

		int totalDays = dateNodelist.getLength();

		for (int s = 0; s < totalDays; s++) {

			Node currentNode = (Node) dateNodelist.item(s);

			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {

				Node dayElementNode = currentNode.getFirstChild();
				if (currentNode.getNodeName().equals("start-valid-time")) {
					days[dayCount] = dayElementNode.getNodeValue();
					dayCount++;
				}
			}
		}

		NodeList listParameters = doc.getElementsByTagName("conditions-icon");
		NodeList nodeList = ((org.w3c.dom.Node) listParameters.item(0)).getChildNodes();

		int totalIcons = nodeList.getLength();

		for (int s = 0; s < totalIcons; s++) {

			Node currentNode = (Node) nodeList.item(s);

			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {

				Node iconElementNode = currentNode.getFirstChild();
				if (!currentNode.getNodeName().equals("name")) {
					icons[iconCount] = iconElementNode.getNodeValue();
					iconCount++;
				}
			}
		}

		NodeList temperatureNodes = doc.getElementsByTagName("temperature");
		for (int i = 0; i < temperatureNodes.getLength(); i++) {
			NodeList tempNodeList = ((org.w3c.dom.Node) temperatureNodes.item(i)).getChildNodes();
			int tempCount = tempNodeList.getLength();

			for (int s = 0; s < tempCount; s++) {

				Node currentNode = (Node) tempNodeList.item(s);
				String tempNodeAttribute = currentNode.getParentNode().getAttributes().item(0).getNodeValue();

				if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
					// node.getAttributes().item(0).getNodeValue().equals("maximum")
					Node tempElementNode = currentNode.getFirstChild();
					if (!currentNode.getNodeName().equals("name")) {
						if (tempNodeAttribute.equals("minimum")) {
							try {
								minTemps[minTempCount] = tempElementNode.getNodeValue();
								minTempCount++;
							} catch (NullPointerException e) {
								minTemps[minTempCount] = "";
								minTempCount++;
							}
						} else {
							try {
								maxTemps[maxTempCount] = tempElementNode.getNodeValue();
								maxTempCount++;
							} catch (NullPointerException e) {
								maxTemps[maxTempCount] = "";
								maxTempCount++;
							}
						}
					}
				}
			}
		}

		ArrayList<WeatherDay> weatherAry = new ArrayList<WeatherDay>();
		for (int i = 0; i < 7; i++) {
			java.util.Date newDate = sdf.parse(days[i].substring(0, 10));

			WeatherDay wd = new WeatherDay();
			wd.maxTemp = maxTemps[i];
			wd.minTemp = minTemps[i];
			wd.icon = icons[i];
			wd.dayDate = days[i];
			weatherAry.add(wd);
		}

		return weatherAry;
	}

	public static String correctUrl(String url) {
		if (!url.equals("")) {
			if (!url.substring(0, 4).equals("http")) {
				url = "http://" + url;
			}
		}
		return url;
	}

	public static String getCityStateInfo(String x, String y) {
		String locationString = null;

		HttpClient httpclient = new DefaultHttpClient();
		// Prepare a request object 38.99,-77.02 " + x + "," + y + "
		// listLatLon=38.99,-77.02&format=24+hourly&numDays=7
		HttpGet httpget = new HttpGet("http://maps.google.com/maps/geo?q=" + x + "," + y + "&output=csv&sensor=false");

		// Execute the request
		HttpResponse response;
		try {

			response = httpclient.execute(httpget);

			// Examine the response status

			// Get hold of the response entity
			HttpEntity entity = response.getEntity();
			// If the response does not enclose an entity, there is no need
			// to worry about connection release

			if (entity != null) {

				// A Simple JSON Response Read
				InputStream instream = entity.getContent();
				locationString = convertStreamToString(instream);

				// Closing the input stream will trigger connection release
				instream.close();
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return locationString;
	}

	static String[] maxTemps;
	static String[] iconImgs;
	static int minTempCount;
	static int maxCount;

	public static void doSomething(Node node) {
		// do something with the current node instead of System.out

		maxTemps = new String[7];
		minTempCount = 0;
		maxCount = 0;

		NodeList nodeList = ((org.w3c.dom.Node) node).getChildNodes();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node currentNode = (Node) nodeList.item(i);

			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				// calls this method for all the children which is Element

				if (node.getNodeName().equals("temperature") && node.getAttributes().item(0).getNodeValue().equals("maximum")) {
					// node.getAttributes().item(0).getNodeValue() == "maximum"

					if (currentNode.getNodeName().equals("value")) {
						node.getAttributes().item(0);
						Node e = currentNode.getFirstChild();
						maxTemps[minTempCount] = e.getNodeValue();

						outputString += "  " + e.getNodeValue() + "\n";
						minTempCount++;
					}
				}

				if (node.getNodeName().equals("temperature") && node.getAttributes().item(0).getNodeValue().equals("minimum")) {
					// node.getAttributes().item(0).getNodeValue() == "maximum"

					if (currentNode.getNodeName().equals("value")) {
						node.getAttributes().item(0);
						Node e = currentNode.getFirstChild();
						maxTemps[maxCount] = e.getNodeValue();

						outputString += "  " + e.getNodeValue() + "\n";
						maxCount++;
					}
				}
			}
			// doSomething(currentNode);
		}
	}

	private static String getTagValue(String sTag, Element eElement) {

		NodeList nlList = ((Document) eElement).getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0);

		return nValue.getNodeValue();
	}

	public static ArrayList<aPlace> fetchPlacesOnline(String personId) {

		ArrayList<aPlace> myPlaces = new ArrayList<aPlace>();
		SoapObject request = new SoapObject(NAMESPACE, GETPLACE_METHOD_NAME);
		request.addProperty("personId", personId);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

		envelope.dotNet = true;
		SoapObject result = null;
		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url);
		envelope.setOutputSoapObject(request);

		try {

			androidHttpTransport.call(GET_PLACES_SOAP_ACTION, envelope);
			SoapObject b = (SoapObject) envelope.getResponse();

			int propertyCount = b.getPropertyCount();

			for (int i = 0; i < propertyCount; i++) {
				SoapObject input = (SoapObject) b.getProperty(i);

				// **Placelist Definition**
				// placeId = temp[0];
				// placeName = temp[1];
				// placeRouteCount = temp[2];
				String RouteCount = "";

				try {
					String placeId = input.getProperty("PlaceId").toString();
					String placeName = input.getProperty("PlaceName").toString();
					try {
						RouteCount = input.getProperty("RouteCount").toString();
					} catch (NullPointerException e) {
					}

					String[] properties = new String[3];
					properties[0] = placeId;
					properties[1] = placeName;
					properties[2] = RouteCount;

					myPlaces.add(new aPlace(properties));
				} catch (NullPointerException e) {

				} catch (NoSuchElementException e) {

				}
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
		return myPlaces;
	}

	public static String[] getImagePlacesNotNull(String args) {

		ArrayList<String> aryNames = new ArrayList<String>();
		Cursor mCursor = mDb.query(Images.IMAGE_TABLE_NAME, new String[] { Images.IMAGE_PLACE_NAME }, args, null, null, null, Images.IMAGE_PLACE_NAME + " ASC");

		int placeCount = mCursor.getCount();
		String[] placeList = new String[placeCount];
		try {
			if ((placeCount > 0) && (mCursor.moveToFirst())) {
				// code for binding all of the coordinates to a listview
				for (int i = 0; i < placeCount; i++) {
					String placeName = mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_PLACE_NAME));
					if (!aryNames.contains(placeName)) {
						aryNames.add(placeName);
					}
					mCursor.moveToNext();
				}
			}
		} finally {
			mCursor.close();
		}
		String[] sAry = new String[aryNames.size()];
		for (int i = 0; i < aryNames.size(); i++) {
			sAry[i] = aryNames.get(i);
		}
		return sAry;
	}

	public static String[] getPlaces(String args) {

		Cursor mCursor = mDb.query(Places.PLACES_TABLE_NAME, new String[] { Places.PLACES_NAME, Places.PLACES_ID }, args, null, null, null, Places.PLACES_NAME + " ASC");

		int placeCount = mCursor.getCount();
		String[] placeList = new String[placeCount];
		try {
			if ((placeCount > 0) && (mCursor.moveToFirst())) {
				// code for binding all of the coordinates to a listview
				for (int i = 0; i < placeCount; i++) {
					placeList[i] = mCursor.getString(mCursor.getColumnIndex(Places.PLACES_NAME));
					mCursor.moveToNext();
				}
			}
		} finally {
			mCursor.close();
		}
		return placeList;
	}

	public static ArrayList<String> getPlacesAry(String args) {

		Cursor mCursor = mDb.query(Places.PLACES_TABLE_NAME, new String[] { Places.PLACES_NAME, Places.PLACES_ID }, args, null, null, null, Places.PLACES_NAME + " ASC");
		ArrayList<String> places = new ArrayList<String>();
		int placeCount = mCursor.getCount();
		try {
			if ((placeCount > 0) && (mCursor.moveToFirst())) {
				for (int i = 0; i < placeCount; i++) {
					places.add(mCursor.getString(mCursor.getColumnIndex(Places.PLACES_NAME)));
					mCursor.moveToNext();
				}
			}
		} finally {
			mCursor.close();
		}
		return places;
	}

	public static ArrayList<friendList> getFriends(String personId, String friendConfirmedBool) {
		ArrayList<friendList> myFriends = new ArrayList<friendList>();
		String[] currentFriends;
		SoapObject request = new SoapObject(NAMESPACE, GETFRIENDS_METHOD_NAME);
		// String user = loadPreferences("email");

		request.addProperty("personId", personId);
		request.addProperty("friendBool", friendConfirmedBool);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 3000);

		try {

			androidHttpTransport.call(GETFRIENDS_SOAP_ACTION, envelope);

			SoapObject b = (SoapObject) envelope.getResponse();
			int propertyCount = b.getPropertyCount();

			for (int i = 0; i < propertyCount; i++) {
				SoapObject input = (SoapObject) b.getProperty(i);
				String CreateDate = nullCheckToString(input.getProperty("CreateDate"));
				String FacebookId = nullCheckToString(input.getProperty("FacebookId"));
				String FirstName = nullCheckToString(input.getProperty("FirstName"));
				String FriendFaceBookId = nullCheckToString((input.getProperty("FriendFaceBookId")));
				String FriendFirstName = nullCheckToString(input.getProperty("FriendFirstName"));
				String FriendId = nullCheckToString(input.getProperty("FriendId"));
				String FriendLastName = nullCheckToString(input.getProperty("FriendLastName"));
				String FriendsPhoneNumber = nullCheckToString(input.getProperty("FriendsPhoneNumber"));
				String FriendsProfilePic = nullCheckToString(input.getProperty("FriendsProfilePic"));
				String LastName = nullCheckToString(input.getProperty("LastName"));
				String PersonId = nullCheckToString(input.getProperty("PersonId"));
				String ProfilePic = nullCheckToString(input.getProperty("ProfilePic"));
				String friendShipConfirmed = nullCheckToString(input.getProperty("friendShipConfirmed"));
				myFriends.add(new friendList(CreateDate, FacebookId, FirstName, FriendFaceBookId, FriendFirstName, FriendId, FriendLastName, null, FriendsPhoneNumber,
						FriendsProfilePic, LastName, PersonId, ProfilePic, friendShipConfirmed, FirstName + " " + LastName));

			}
		} catch (InterruptedIOException e) {
			e.printStackTrace();
			// handle timeout here...
		} catch (Exception e) {
			e.printStackTrace();

		}
		return myFriends;
	}

	private static String nullCheckToString(Object ob) {
		if (ob == null || ob.toString().equals("null")) {
			return "";
		}
		return ob.toString();
	}

	public void getMSRImages() {
		SoapObject request = new SoapObject(MSRNameSpace, MRSgetTileByLatLonMethodName);

		Double lon = -122.5;
		Double lat = 37.5;

		Double x = Double.valueOf(lat);
		Double y = Double.valueOf(lon);

		request.addProperty("theme", 2);

		int xInt = (int) (x * 1E6);
		int yInt = (int) (y * 1E6);

		request.addProperty("lat", lat);
		request.addProperty("lon", lon);
		request.addProperty("scale", "Scale2m");
		request.addProperty("displayPixWidth", 150);
		request.addProperty("displayPixHeight", 150);

		// request.addProperty("scale", 2);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(MSRMapsUrl, 3000);

		try {

			androidHttpTransport.call(MSRMapsAction, envelope);

			SoapObject b = (SoapObject) envelope.getResponse();
			int propertyCount = b.getPropertyCount();
			for (int i = 0; i < propertyCount; i++) {

				SoapObject input = (SoapObject) b.getProperty(i);
				PropertyInfo h = new PropertyInfo();

				String[] properties = new String[input.getPropertyCount()];

				for (int s = 0; s < input.getPropertyCount(); s++) {

					input.getPropertyInfo(s, h);
					properties[s] = h.toString();

				}

			}
		} catch (InterruptedIOException e) {

			// handle timeout here...
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("xx: " + e.getMessage());
		}
	}

	public ArrayList<aUser> getUsers() {
		ArrayList<aUser> userList = new ArrayList<aUser>();
		String[] currentFriends;
		SoapObject request = new SoapObject(NAMESPACE, getUsersMethodName);
		// String user = loadPreferences("email"); ;;;;;;;;

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 3000);

		try {

			androidHttpTransport.call(getUsersAction, envelope);

			SoapObject b = (SoapObject) envelope.getResponse();
			int propertyCount = b.getPropertyCount();
			for (int i = 0; i < propertyCount; i++) {
				SoapObject input = (SoapObject) b.getProperty(i);
				PropertyInfo h = new PropertyInfo();

				String[] properties = new String[input.getPropertyCount()];

				for (int s = 0; s < input.getPropertyCount(); s++) {
					input.getPropertyInfo(s, h);
					properties[s] = h.toString();
				}

				userList.add(new aUser());

			}

			currentFriends = new String[userList.size()];

			for (int r = 0; r < userList.size(); r++) {

				aUser users = userList.get(r);

				currentFriends[r] = users.getEncryptedUsername();

			}
		} catch (InterruptedIOException e) {

			// handle timeout here...
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("xx: " + e.getMessage());
		}
		return userList;
	}

	// public ArrayList<taggedaRoute> getRouteTags(Context mContext, String
	// personId, String aknoledgedBool) {
	// ArrayList<taggedaRoute> myRouteTags = new ArrayList<taggedaRoute>();
	// String[] currentFriends;
	// SoapObject request = new SoapObject(NAMESPACE, Tags_MethodName);
	// // String user = loadPreferences("email");
	//
	// request.addProperty("personId", personId);
	// request.addProperty("aknoledgedBool", aknoledgedBool);
	//
	// SoapSerializationEnvelope envelope = new
	// SoapSerializationEnvelope(SoapEnvelope.VER11);
	// envelope.dotNet = true;
	// envelope.setOutputSoapObject(request);
	//
	// HttpTransportSE androidHttpTransport = new
	// HttpTransportSE(GPShare_Svc_Url, 3000);
	//
	// try {
	//
	// androidHttpTransport.call(Get_Tags_Action, envelope);
	//
	// SoapObject b = (SoapObject) envelope.getResponse();
	//
	// int propertyCount = b.getPropertyCount();
	//
	// for (int i = 0; i < propertyCount; i++) {
	//
	// SoapObject input = (SoapObject) b.getProperty(i);
	//
	// PropertyInfo h = new PropertyInfo();
	//
	// String[] properties = new String[input.getPropertyCount()];
	//
	// for (int s = 0; s < input.getPropertyCount(); s++) {
	// input.getPropertyInfo(s, h);
	//
	// properties[s] = h.toString();
	// }
	// myRouteTags.add(new taggedaRoute(properties));
	//
	// }
	// for (int r = 0; r < myRouteTags.size(); r++) {
	//
	// taggedaRoute friends = myRouteTags.get(r);
	//
	// }
	//
	// } catch (SocketTimeoutException e) {
	// System.out.println("SocketTimeoutException: getRouteTags: " +
	// e.getMessage());
	// myRouteTags = null;
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// System.out.println("Exception: getRouteTags: " + e.getMessage());
	// Toast.makeText(mContext, "Connection Timed Out", 1).show();
	// }
	// return myRouteTags;
	// }

	public ArrayList<friendRoute> getRoutesByPlaceId(String placeId) {
		ArrayList<friendRoute> routesPerPlace = new ArrayList<friendRoute>();
		System.out.println("placeId: " + placeId);
		SoapObject request = new SoapObject(NAMESPACE, getRoutesAndPlaceMarksPlaceIdMethodName);
		// String user = loadPreferences("email");

		request.addProperty("placeId", placeId);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 4000);

		try {

			androidHttpTransport.call(getRoutesAndPlaceMarksPlaceIdAction, envelope);

			SoapObject b = (SoapObject) envelope.getResponse();
			System.out.println("envelope.getresponse: " + envelope.getResponse());
			int propCount = b.getPropertyCount();

			if (propCount > 0) {

				for (int i = 0; i < propCount; i++) {
					SoapObject sB = (SoapObject) b.getProperty(i);

					try {

						for (int ii = 0; ii < sB.getPropertyCount(); ii++) {
							SoapObject sbb = (SoapObject) sB.getProperty(ii);
							System.out.println("sB.getPropertyAsString(\"picUrl\"): " + sbb.getPropertyAsString("picUrl"));
						}
					} catch (NullPointerException e) {
						System.out.println("aPlaceMark Prop was null!");
					}

					try {

					} catch (NullPointerException e) {
						System.out.println("aVwRoutePlace Prop was null!");
					}

					// String RouteId = sB.getProperty("RouteId").toString();
					// String CreateDate =
					// sB.getProperty("CreateDate").toString();
					// String Distance = "";
					// try {
					// Distance = sB.getProperty("Distance").toString();
					// } catch (NullPointerException e) {
					// }
					// String PlaceName =
					// sB.getProperty("PlaceName").toString();
					//
					// String[] properties = new String[14];
					// properties[0] = "";
					// properties[1] = PlaceName;
					// properties[2] = CreateDate;
					// properties[3] = "";
					// properties[4] = "";
					// properties[5] = "";
					// properties[6] = "";
					// properties[7] = Distance;
					// properties[8] = "";
					// properties[9] = "";
					// properties[10] = "";
					// properties[11] = "";
					// properties[12] = "";
					// properties[13] = RouteId;
					//
					// routesPerPlace.add(new friendRoute(properties));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getARouteById error: " + e.getStackTrace());
		}
		return routesPerPlace;
	}

	public static ArrayList<aRoute> getARouteById(String routeId, String createDate)

	{
		ArrayList<aRoute> mySingleRoute = new ArrayList<aRoute>();
		String[] currentFriends;
		SoapObject request = new SoapObject(NAMESPACE, ARouteMethod_Name);
		// String user = loadPreferences("email");

		request.addProperty("routeId", routeId);
		request.addProperty("createDate", createDate);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 4000);

		try {
			String distance = "";
			String topSpeed = "";
			androidHttpTransport.call(GetRoute_Action, envelope);
			aRoute cRoute = new aRoute(null);
			SoapObject b = (SoapObject) envelope.getResponse();
			System.out.println("envelope.getResponse(): " + envelope.getResponse());
			String xyString = b.getProperty("xyString").toString();
			String PersonId = b.getProperty("PersonId").toString();
			String PlaceId = b.getProperty("PlaceId").toString();
			String RouteId = b.getProperty("RouteId").toString();
			String CreateDate = b.getProperty("CreateDate").toString();
			String placeName = b.getProperty("PlaceName").toString();

			try {
				distance = String.valueOf(Transactions.convertMetersPrefUom(Double.valueOf(b.getProperty("Distance").toString()), GPShare.getUOM()));
			} catch (NullPointerException e) {
			}
			try {
				topSpeed = b.getProperty("TopSpeed").toString();
			} catch (NullPointerException e) {
			}

			cRoute.setPlaceName(placeName);
			cRoute.setDistance(distance);
			cRoute.setTopSpeed(topSpeed);
			cRoute.setCreateDate(CreateDate);
			cRoute.setPlaceId(PlaceId);
			cRoute.setRouteId(RouteId);
			cRoute.setPersonId(PersonId);
			cRoute.setXyString(xyString);

			mySingleRoute.add(cRoute);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getARouteById error: " + e.getStackTrace());
		}
		return mySingleRoute;
	}

	private static double convertMetersToKm(double distance) {
		// TODO Auto-generated method stub
		return distance / 1000;
	}

	public static aUser getProfileStats(String personId) {
		SoapObject request = new SoapObject(NAMESPACE, getProfileStatsMethodName);
		// String user = loadPreferences("email");

		request.addProperty("personId", personId);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		aUser aUser = null;
		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 4000);

		try {

			androidHttpTransport.call(GETPROFILE_STATS_SOAP_ACTION, envelope);
			aUser = new aUser();
			SoapObject b = (SoapObject) envelope.getResponse();
			;
			String routecount = b.getProperty("routeCount").toString();
			String totalDistance = b.getProperty("totalDistance").toString();
			String friendCount = b.getProperty("friendCount").toString();
			String placeCount = b.getProperty("placeCount").toString();
			String imageCount = b.getProperty("placeMarkCount").toString();

			aUser.setTotalDistance(String.valueOf(convertMetersPrefUom(Double.valueOf(totalDistance), GPShare.getUOM())) + " " + GPShare.UNIT_OF_MEASURE_TXT_DIST);
			aUser.setTotalImageCount(imageCount);
			aUser.setTotalFriendcount(friendCount);
			aUser.setRouteCount(routecount);
			aUser.setPlaceCount(placeCount);
			aUser.setTotalImages(imageCount);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getARouteById error: " + e.getStackTrace());
		}
		return aUser;
	}

	

	

	public static ArrayList<aRank> getDistanceRanks() {
		ArrayList<aRank> Rankings = new ArrayList<aRank>();

		SoapObject request = new SoapObject(NAMESPACE, getDistanceRanksMethodName);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 4000);

		try {

			androidHttpTransport.call(getDistanceRanks_action, envelope);

			SoapObject b = (SoapObject) envelope.getResponse();

			int propertyCount = b.getPropertyCount();

			for (int i = 0; i < propertyCount; i++) {

				SoapObject input = (SoapObject) b.getProperty(i);
				String personId = input.getProperty("PersonId").toString();

				String firstName = input.getProperty("FirstName").toString();
				String lastName = input.getProperty("LastName").toString();
				String distance = input.getProperty("totalDistance").toString();
				String ProfilePic = input.getProperty("ProfilePic").toString();

				String[] properties = new String[5];
				properties[0] = personId;
				properties[1] = distance;
				properties[2] = firstName + " " + lastName;
				properties[3] = ProfilePic;

				Rankings.add(new aRank(properties));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return Rankings;
	}

	public static String insertRouteOnline(String personIdSvc, String userName, String placeName, String createDate, String xyStringSvc, aRoute aRouteOb, String taggedUserName,
			String taggedUserFaceBookId, String distance, String routeName, String topSpeed) {

		// private void register(String user, String password, String validate)
		// {
		String routeId = null;

		SoapObject request = new SoapObject(NAMESPACE, INSERT_ROUTE_METHOD_NAME);
		request.addProperty("personIdSvc", personIdSvc);
		request.addProperty("userNameSvc", userName);
		request.addProperty("placeIdSvc", placeName);
		request.addProperty("createDateSvc", createDate);
		request.addProperty("xyStringSvc", xyStringSvc);
		request.addProperty("taggedUserName", taggedUserName);
		request.addProperty("taggedUserFaceBookId", taggedUserFaceBookId);
		request.addProperty("distance", distance);
		request.addProperty("routeName", routeName);
		request.addProperty("topSpeed", topSpeed);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 3000);

		try {

			androidHttpTransport.call(INSERT_ROUTE_ACTION, envelope);

			routeId = envelope.getResponse().toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return routeId;
	}

	public String backupPhoneImages() {

		// private void register(String user, String password, String validate)
		// {
		String response = null;
		ArrayList<Image> imgAry = getImageaFromPhoneDb("");

		for (int i = 0; i < imgAry.size(); i++) {
			Image img = imgAry.get(i);

			SoapObject request = new SoapObject(NAMESPACE, backupPhoneImagesMethodName);
			request.addProperty("imageId", img.getimageId());
			request.addProperty("imgPath", img.getimgPath());
			request.addProperty("imgPlace", img.getimgPlace());
			request.addProperty("imgCreateDate", img.getimgCreateDate());
			request.addProperty("imgX", img.getimgX());
			request.addProperty("imgY", img.getimgY());
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

			envelope.dotNet = true;

			envelope.setOutputSoapObject(request);

			HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 3000);

			try {

				androidHttpTransport.call(backupPhoneImagesAction, envelope);

				response = envelope.getResponse().toString();

				// update the phone route id with the corresponding online route
				// id that is returned.
				if (response != null) {
					if (!response.equals("")) {
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return response;
	}

	public String updateSettings(String personId) {

		// private void register(String user, String password, String validate)
		// {
		String routeId = null;

		SoapObject request = new SoapObject(NAMESPACE, updateSettingsMethodName);
		request.addProperty("personId", personId);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 3000);

		try {

			androidHttpTransport.call(updateSettingsAction, envelope);

			routeId = envelope.getResponse().toString();

			// update the phone route id with the corresponding online route id
			// that is returned.
			if (routeId != null) {
				if (routeId != "") {

				}
			}

		} catch (Exception e) {

			e.printStackTrace();

			System.out.println("*********insertRouteOnline Error: " + e.getMessage());
		}

		return routeId;
	}

	public static ArrayList<aUser> login(Context mContext, String user, String password) {
		String response = "";

		// Element svcUserName = new Element().createElement(NAMESPACE,
		// "userName");
		// svcUserName.addChild(Node.TEXT, "test");
		// svcUserName.setNamespace(NAMESPACE);
		// Element svcPassword = new Element().createElement(NAMESPACE,
		// "password");
		// svcPassword.addChild(Node.TEXT, "testPassword");
		// svcPassword.setNamespace(NAMESPACE);

		// envelope.headerOut = new Element[2];
		// envelope.headerOut[0] = svcUserName;
		// envelope.headerOut[1] = svcPassword;

		ArrayList<aUser> userList = new ArrayList<aUser>();

		SoapObject request = new SoapObject(NAMESPACE, LOGIN2_METHOD_NAME2);

		request.addProperty("userName", user);
		request.addProperty("passWord", password);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url);

		try {
			androidHttpTransport.call(LOGIN2_SOAP_ACTION, envelope);
			aUser newUser = new aUser();
			response = envelope.toString();
			SoapObject b = (SoapObject) envelope.getResponse();

			int propertyCount = b.getPropertyCount();

			for (int i = 0; i < propertyCount; i++) {

				SoapObject input = (SoapObject) b.getProperty(i);
				String encryptedUn = input.getProperty("UserName").toString();
				String encryptedPw = input.getProperty("PassWord").toString();
				String personId = input.getProperty("PersonId").toString();
				String profilePic = input.getProperty("ProfilePic").toString();
				String firstLastName = input.getProperty("FirstName").toString() + " " + input.getProperty("LastName").toString();

				newUser.setPersonId(personId);
				newUser.setEncryptedPassword(encryptedPw);
				newUser.setEncryptedUsername(encryptedUn);
				newUser.setFirstLastName(firstLastName);
				userList.add(newUser);

			}
		} catch (Exception e) {
			e.printStackTrace();
			// Toast.makeText(context,
			// "Couldn't connect to web service for some reason!", 2).show();

		}
		if (response == null || response == "") {

			// Toast.makeText(context,
			// "Couldn't connect to web service for some reason!!", 2).show();

		} else if (response.equals("false")) {

			// Toast.makeText(context,
			// "Invalid username or password. Please register!", 2).show();
		}

		return userList;
	}

	public static aUser getPassworFromServer(String userName) {

		aUser currentUser = null;

		SoapObject request = new SoapObject(NAMESPACE, getPWFromUserName_methodName);

		request.addProperty("userName", userName);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url);

		try {

			androidHttpTransport.call(getPWFromUserName_action, envelope);

			String response = envelope.toString();
			SoapObject b = (SoapObject) envelope.getResponse();

			String encryptedPassword = b.getProperty("PassWord").toString();
			String decryptedPassword = Transactions.decrypt(encryptedPassword);
			currentUser = new aUser();
			currentUser.setEncryptedPassword(encryptedPassword);
			currentUser.setDecryptedPassword(decryptedPassword);
			currentUser.setDecryptedUsername(userName);

		} catch (Exception e) {
			e.printStackTrace();

		}
		return currentUser;
	}

	public String updateFbIdForExistUser(String personId, String fbId) {
		// private void register(String user, String password, String validate)
		// {
		String response = null;

		SoapObject request = new SoapObject(NAMESPACE, updateFbIdforExistUserMethodName);
		request.addProperty("personId", personId);
		request.addProperty("fbId", fbId);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 3000);

		try {

			androidHttpTransport.call(updateFbIdforExistUserAction, envelope);
			response = envelope.getResponse().toString();

		} catch (Exception e) {

		}
		return response;
	}

	public static requestedFriend doesUserExist(String requestedUserName) {

		SoapObject request = new SoapObject(NAMESPACE, doesUserExist_Method_Name);
		request.addProperty("requestedUserName", requestedUserName);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		requestedFriend requestedFriend = new requestedFriend();
		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 3000);

		try {
			androidHttpTransport.call(doesUserExistAction, envelope);
			SoapObject input = (SoapObject) envelope.getResponse();

			// int propertyCount = b.getPropertyCount();

			// SoapObject input = (SoapObject) b.getProperty(0);
			// for (int i = 0; i < propertyCount; i++) {

			String phoneNumber = input.getProperty("PhoneNumber").toString();
			String personId = input.getProperty("PersonId").toString();
			String profilePic = input.getProperty("ProfilePic").toString();
			String firstLastName = input.getProperty("FirstName").toString() + " " + input.getProperty("LastName").toString();

			requestedFriend.setProfileImgUrl(profilePic);
			requestedFriend.setPersonId(personId);
			requestedFriend.setFirstLast(firstLastName);
			requestedFriend.setPhoneNumber(phoneNumber);
			// }

		} catch (Exception e) {

			e.printStackTrace();

		}
		return requestedFriend;
	}

	public static String createUser(AuthOb authOb) {
		// private void register(String user, String password, String validate)
		// {
		String response = null;

		JSONObject jsonRoute = new JSONObject();
		try {
			jsonRoute.put("fbImageUrl", authOb.getFbImageUrl());
			jsonRoute.put("fbId", authOb.getFbId());
			jsonRoute.put("personId", authOb.getPersonId());
			jsonRoute.put("email", authOb.getEmail());
			jsonRoute.put("phoneNumber", authOb.getPhoneNumber());
			jsonRoute.put("firstName", authOb.getFirstName());
			jsonRoute.put("lastName", authOb.getLastName());
			jsonRoute.put("passWord", authOb.getPassWord());
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		SoapObject request = new SoapObject(NAMESPACE, CreateUser_METHOD_NAME);
		request.addProperty("aAuthObString", jsonRoute.toString());
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 3000);

		try {

			androidHttpTransport.call(Insert_CreateUser_Action, envelope);
			response = envelope.getResponse().toString();

		} catch (Exception e) {

		}
		return response;
	}

	public static boolean registUserGcm(String regId, Context context, String personId) {
		// private void register(String user, String password, String validate)
		// {
		String response = null;
		JSONObject jsonRoute = new JSONObject();
		try {
			jsonRoute.put("personId", personId);
			jsonRoute.put("userGcmKey", regId);

		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		SoapObject request = new SoapObject(NAMESPACE, registerUserGcmMethodName);
		request.addProperty("aAuthObString", jsonRoute.toString());
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 3000);

		try {

			androidHttpTransport.call(registUserGcmAction, envelope);
			response = envelope.getResponse().toString();

			GCMRegistrar.setRegisteredOnServer(context, true);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean sendNotification(String regId, String message, String personId, String data, String notificationTypeCode, Context context) {
		// private void register(String user, String password, String validate)
		// {
		String response = null;
		JSONObject jsonRoute = new JSONObject();

		try {
			jsonRoute.put("regId", regId);
			jsonRoute.put("message", message);
			jsonRoute.put("personId", personId);
			jsonRoute.put("notificationTypeCode", notificationTypeCode);
			jsonRoute.put("data", data);

		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		SoapObject request = new SoapObject(NAMESPACE, sendNotificationMethodName);
		request.addProperty("gcmOb", jsonRoute.toString());
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 3000);

		try {

			androidHttpTransport.call(sendNotificationAction, envelope);
			response = envelope.getResponse().toString();

			if (response.equals("NotRegistered")) {
				GCMHelper gcmHelper = new GCMHelper(context);
				gcmHelper.unRegisterReRegister();
			}
			System.out.println("response.sendnotification: " + response);
			// GCMRegistrar.setRegisteredOnServer(context, true);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean registerGcmRegIdOnline(String regId, String personId) {
		// private void register(String user, String password, String validate)
		// {
		String response = null;

		SoapObject request = new SoapObject(NAMESPACE, registerGcmMethodName);
		request.addProperty("personId", personId);
		request.addProperty("regId", regId);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 3000);

		try {

			androidHttpTransport.call(registerGcmAction, envelope);
			response = envelope.getResponse().toString();

			System.out.println("response.sendnotification: " + response);
			// GCMRegistrar.setRegisteredOnServer(context, true);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * in if you need username or password stored as global variable you can use
	 * LoadPreferences(key),where key = "mail" if you need retrieve username and
	 * key = "pass" if you need retrieve password
	 */
	public static String LoadPreferences(String key) {
		String value = sharedPreferences.getString(key, "");
		return value;
	}

	public static String insertTag(String taggerPersonId, String taggedPersonId, String tagTypeId, String taggedUserFaceBookId, String tagId) {
		String response = null;

		SoapObject request = new SoapObject(NAMESPACE, Insert_Tag_Method);

		request.addProperty("taggerPersonId", taggerPersonId);
		request.addProperty("taggedPersonId", taggedPersonId);
		request.addProperty("taggedUserFaceBookId", taggedUserFaceBookId);
		request.addProperty("tagTypeId", tagTypeId);
		request.addProperty("tagId", tagId);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 3000);

		try {

			androidHttpTransport.call(Insert_Tag_Action, envelope);

			response = envelope.getResponse().toString();

		} catch (Exception e) {

			e.printStackTrace();

			System.out.println("*********Error1: " + e.getMessage());
		}

		return response;
	}

	public String acknowledgeTag(String tagId, String taggedUserId) {
		String response = null;

		SoapObject request = new SoapObject(NAMESPACE, AcknoledgeTag_METHOD_NAME);

		request.addProperty("tagId", tagId);
		request.addProperty("taggedUserId", taggedUserId);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 3000);

		try {

			androidHttpTransport.call(AcknowledgeTagAction, envelope);

			response = envelope.getResponse().toString();

		} catch (Exception e) {

			e.printStackTrace();

		}

		return response;
	}

	public static String createErrorLog(String userId) {
		String logString = null;
		Process mLogcatProc = null;
		BufferedReader reader = null;
		try {
			mLogcatProc = Runtime.getRuntime().exec(new String[] { "logcat", "-d", "AndroidRuntime:E [Your Log Tag Here]:V *:S" });
			reader = new BufferedReader(new InputStreamReader(mLogcatProc.getInputStream()));
			String line;
			final StringBuilder log = new StringBuilder();
			log.append("Error Log: for USER ID: " + userId + " PHONE: " + Build.MODEL + " DATE: " + DateNow.getCurrentDateTime() + "\n");
			String separator = System.getProperty("line.separator");
			while ((line = reader.readLine()) != null) {
				log.append(line);
				log.append(separator);
			}
			// do whatever you want with the log. I'd recommend using Intents to
			// create an email
			logString = log.toString();
		} catch (IOException e) {
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {

				}
		}
		return logString;
	}

	public static String insertErrorLog(String userName, String errorLog) {
		String response = null;
		errorLog = errorLog.replace(":", "\n");
		SoapObject request = new SoapObject(NAMESPACE, Insert_ErrorLog_Method);

		request.addProperty("userName", userName);
		request.addProperty("errorLog", errorLog);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 3000);

		try {

			androidHttpTransport.call(Insert_ErrorLog_Action, envelope);

			response = envelope.getResponse().toString();

		} catch (Exception e) {

			e.printStackTrace();

		}

		return response;
	}

	public static String deleteOnlineRoute(String routeId) {
		String response = null;

		SoapObject request = new SoapObject(NAMESPACE, Deteleroute_Action);

		request.addProperty("routeId", routeId);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 3000);

		try {

			androidHttpTransport.call(DeleteRouteMethod, envelope);

			response = envelope.getResponse().toString();

		} catch (Exception e) {

			e.printStackTrace();

		}

		return response;
	}

	public String insertObjRouteOnline(String userName, String placeName, String createDate, String xyStringSvc) throws JSONException {
		// private void register(String user, String password, String validate)
		// {

		String routeId = null;

		SoapObject request = new SoapObject(NAMESPACE, INSERT_ROUTE_METHOD_NAME);
		request.addProperty("routeObject", createJsonObFromXyString(userName, placeName, xyStringSvc));

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

		envelope.dotNet = true;

		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 3000);

		try {

			androidHttpTransport.call(INSERT_ROUTE_ACTION, envelope);

			routeId = envelope.getResponse().toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return routeId;
	}

	public static String createNewRoute(String xyString, String currentPlace, String uploadedBool, String taggerUserName, String routeId, String startDateTime,
			String currentTrackBool, String currentRouteSegmentDateKey, String distance) {

		mDb.beginTransaction();

		try {

			ContentValues allroutesRecordToAdd = new ContentValues();
			allroutesRecordToAdd.put("allroutes_id", routeId);
			allroutesRecordToAdd.put("allroutes_key", startDateTime);
			allroutesRecordToAdd.put("allroutes_place", currentPlace);
			allroutesRecordToAdd.put("allroutes_xy_string", xyString);
			allroutesRecordToAdd.put("allroutes_createDate", startDateTime);
			allroutesRecordToAdd.put("allroutes_uploaded", uploadedBool);
			allroutesRecordToAdd.put("allroutes_taggerUsername", taggerUserName);
			allroutesRecordToAdd.put("allroutes_currentTrackBool", currentTrackBool);
			allroutesRecordToAdd.put(AllRoutes.ALLROUTES_distance, distance);
			allroutesRecordToAdd.put(AllRoutes.ALLROUTES_startDateTime, startDateTime);
			allroutesRecordToAdd.put(AllRoutes.ALLROUTES_SegmentKey, currentRouteSegmentDateKey);
			mDb.insert(AllRoutes.ALLROUTESS_TABLE_NAME, null, allroutesRecordToAdd);

			// update coordinate counter with current coordinate count with out
			// actually querying the database.
			// updateCoordCounter(coordinateCount);

			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
		}
		return startDateTime;
	}

	public String insertNewRoutePhoneDb(String routeFilePath, String route_key, String currentPlace) {

		mDb.beginTransaction();
		try {

			ContentValues routeRecordToAdd = new ContentValues();
			routeRecordToAdd.put("route_file_path", routeFilePath);
			routeRecordToAdd.put("route_key", route_key);
			routeRecordToAdd.put("route_place_id", currentPlace);

			mDb.insert(Routes.ROUTES_TABLE_NAME, null, routeRecordToAdd);

			mDb.setTransactionSuccessful();

		} finally {
			mDb.endTransaction();
		}
		return route_key;
	}

	public static void updateCurrentRouteRecord(String routeKey, String currentTrackBool) {
		mDb.beginTransaction();
		try {

			ContentValues args = new ContentValues();
			args.put("allroutes_currentTrackBool", currentTrackBool);
			mDb.update(AllRoutes.ALLROUTESS_TABLE_NAME, args, "allroutes_key=" + "'" + routeKey + "'", null);
			mDb.setTransactionSuccessful();

		} finally {
			mDb.endTransaction();
		}
	}

	public static void updateUploadBool(String routeSegKey, String uploadedBool) {

		mDb.beginTransaction();
		try {

			ContentValues args = new ContentValues();
			args.put("allroutes_uploaded", uploadedBool);
			mDb.update(AllRoutes.ALLROUTESS_TABLE_NAME, args, AllRoutes.ALLROUTES_SegmentKey + " = " + "'" + routeSegKey + "'", null);
			mDb.setTransactionSuccessful();

		} finally {
			mDb.endTransaction();
		}
	}

	public static boolean updateRouteId(String routeSegKey, String onlineRouteId) {
		boolean responseBool = false;
		mDb.beginTransaction();
		try {

			ContentValues args = new ContentValues();
			args.put("allroutes_id", onlineRouteId);
			mDb.update(AllRoutes.ALLROUTESS_TABLE_NAME, args, AllRoutes.ALLROUTES_SegmentKey + " =" + "'" + routeSegKey + "'", null);
			mDb.setTransactionSuccessful();
			responseBool = true;
		} finally {
			mDb.endTransaction();
		}
		return responseBool;
	}

	// ///retiredpublic static String encrypt(String toEncrypt)
	// ///retired{
	// ///retired String encryptedString = null;
	// ///retired try {
	// ///retired encryptedString =
	// Crypto.encrypt("SuezQ4691827x!elephent$Ballz", toEncrypt);
	// ///retired System.out.println("XXXXXXXXXencryptedString: " +
	// encryptedString);
	// ///retired } catch (Exception e) {
	// ///retired // TODO Auto-generated catch block
	// ///retired e.printStackTrace();
	// ///retired }
	// ///retired return encryptedString;
	// ///retired }

	// ///retired
	// ///retiredpublic static String decrypt(String toDecrypt)
	// ///retired{
	// ///retired String decryptedString = null;

	// ///retired try {
	// ///retired System.out.println("XXXXXXXXXencryptedString: " + toDecrypt);
	// ///retired decryptedString =
	// Crypto.decrypt("SuezQ4691827x!elephent$Ballz", toDecrypt);
	// ///retired System.out.println("XXXXXXXXXdecryptedString: " +
	// decryptedString);
	// ///retired } catch (Exception e) {
	// ///retired // TODO Auto-generated catch block
	// ///retired e.printStackTrace();
	// ///retired }

	// ///retired return decryptedString;
	// ///retired }

	public static String decrypt(String text) throws Exception {

		String key = "SuezQ4691827x!elephent$Ballz";
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] keyBytes = new byte[16];
		byte[] b = key.getBytes("UTF-8");
		int len = b.length;
		if (len > keyBytes.length)
			len = keyBytes.length;
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

		byte[] results = cipher.doFinal(Base64.decode(text, 0));
		return new String(results, "UTF-8");
	}

	public static String encrypt(String text) throws Exception {

		String key = "SuezQ4691827x!elephent$Ballz";
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] keyBytes = new byte[16];
		byte[] b = key.getBytes("UTF-8");
		int len = b.length;
		if (len > keyBytes.length)
			len = keyBytes.length;
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

		byte[] results = cipher.doFinal(text.getBytes("UTF-8"));

		return Base64.encodeToString(results, 0);
	}

	public static ArrayList<aRoute> getRoutesFromPhoneDb(String userName, String tagBool, String uploadedBool, String currentTrackBool, String whereClause) {
		ArrayList<aRoute> routeArrayList = new ArrayList<aRoute>();
		String sqlArg = "";

		if (tagBool.equals("true")) {// not equals blank
			sqlArg = " not null";

		} else {// equal blank
			sqlArg = " = ''";
		}

		if (whereClause.equals("")) {
			whereClause = "allroutes_uploaded = " + "'" + uploadedBool + "' and " + "allroutes_taggerUsername " + sqlArg + " and allroutes_currentTrackBool" + " = '"
					+ currentTrackBool + "'";
		}

		Cursor cursor = mDb.query(RouteDatabase.AllRoutes.ALLROUTESS_TABLE_NAME, null, whereClause, null, null, null, RouteDatabase.AllRoutes.ALLROUTES_ID + " desc");

		routeArrayList.clear();

		// int routeCount = mCursor.getCount();

		// if((routeCount > 0) && (mCursor.moveToFirst()))
		// {

		// code for binding all of the coordinates to a listview

		// for(int i = 0; i < routeCount; i++)
		// {
		// String routeAry[] = new String[15];

		// aRoute currentRoute = new aRoute(null);

		// currentRoute.setUserName(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_TaggerUserName)));
		// currentRoute.setPlaceName(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_PLACE)));
		// currentRoute.setCreateDate(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_CREATE_DATE)));
		// currentRoute.setXyStringSvc(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_XYString)));
		// currentRoute.setRouteKey(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_KEY)));
		// currentRoute.setAllroutes_x(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_X)));
		// currentRoute.setAllroutes_y(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_Y)));
		// currentRoute.setAllroutes_distance(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_distance)));
		// currentRoute.setAllroutes_pointCount(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_pointCount)));
		// currentRoute.setAllroutes_topSpeed(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_topSpeed)));
		// currentRoute.setAllroutes_totalFall(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_totalFall)));
		// currentRoute.setAllroutes_totalRise(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_totalRise)));
		// currentRoute.setSegmentKey(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_SegmentKey)));
		// currentRoute.setRouteName(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_NAME)));

		// int elapsedTime = 0;

		// try{
		// elapsedTime =
		// Integer.valueOf(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_ElapsedTime)));
		// }catch (NumberFormatException e)
		// {

		// }
		// currentRoute.setAllroute_elapsedTime(String.valueOf(elapsedTime));
		// routeArrayList.add(new aRoute(routeAry));
		// mCursor.moveToNext();
		// }

		return parseRouteCursor(cursor);
	}

	public static int getRouteCount() {

		Cursor cursor = mDb.query(RouteDatabase.AllRoutes.ALLROUTESS_TABLE_NAME, null, null, null, null, null, null);
		return cursor.getCount();
	}

	public static ArrayList<aRoute> getAllRoutesFromPhoneDb() {
		ArrayList<aRoute> routeArrayList = new ArrayList<aRoute>();
		String sqlArg = "";

		Cursor cursor = mDb.query(RouteDatabase.AllRoutes.ALLROUTESS_TABLE_NAME, null, null, null, null, null, null);

		routeArrayList.clear();

		int routeCount = cursor.getCount();
		;

		if ((routeCount > 0) && (cursor.moveToFirst())) {

			// code for binding all of the coordinates to a listview

			for (int i = 0; i < routeCount; i++) {
				aRoute currentRoute = new aRoute(new String[0]);

				String routeAry[] = new String[15];
				currentRoute.setPlaceName(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_PLACE)));
				currentRoute.setCreateDate(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_CREATE_DATE)));
				currentRoute.setXyString(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_XYString)));
				currentRoute.setRouteKey(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_KEY)));
				currentRoute.setX((cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_X))));
				currentRoute.setY((cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_Y))));
				currentRoute.setDistance(cFNull(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_distance))));
				currentRoute.setPointCount(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_pointCount)));
				currentRoute.setElapsedTime(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_ElapsedTime)));
				currentRoute.setTopSpeed(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_topSpeed)));
				currentRoute.setSegmentKey(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_SegmentKey)));
				currentRoute.setRouteName(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_NAME)));
				int elapsedTime = 0;

				try {
					elapsedTime = Integer.valueOf(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_ElapsedTime)));
				} catch (NumberFormatException e) {
					//
				}
				currentRoute.setElapsedTime(String.valueOf(elapsedTime));

				routeArrayList.add(currentRoute);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return routeArrayList;
	}

	public static ArrayList<aRoute> parseRouteCursor(Cursor cursor) {
		ArrayList<aRoute> routeArrayList = new ArrayList<aRoute>();

		int routeCount = cursor.getCount();

		if ((routeCount > 0) && (cursor.moveToFirst())) {

			// code for binding all of the coordinates to a listview

			for (int i = 0; i < routeCount; i++) {
				aRoute currentRoute = new aRoute(new String[0]);

				String routeAry[] = new String[15];
				currentRoute.setDataType(OFFLINE_DATA);
				currentRoute.setPlaceName(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_PLACE)));
				currentRoute.setCreateDate(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_CREATE_DATE)));
				currentRoute.setXyString(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_XYString)));
				currentRoute.setRouteKey(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_KEY)));
				currentRoute.setX((cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_X))));
				currentRoute.setY((cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_Y))));
				currentRoute.setDistance(cFNull(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_distance))));
				currentRoute.setPointCount(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_pointCount)));
				currentRoute.setElapsedTime(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_ElapsedTime)));
				currentRoute.setTopSpeed(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_topSpeed)));
				currentRoute.setSegmentKey(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_SegmentKey)));
				currentRoute.setRouteName(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_NAME)));
				int elapsedTime = 0;

				System.out.println("cursor.getString(cursor.getColumnIndex(AllRoutes.allroutes_id): " + cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_ID)));
				try {
					elapsedTime = Integer.valueOf(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_ElapsedTime)));
				} catch (NumberFormatException e) {
					//
				}
				currentRoute.setElapsedTime(String.valueOf(elapsedTime));

				routeArrayList.add(currentRoute);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return routeArrayList;
	}

	public static String cFNull(String nullVar) {
		if (nullVar == null) {
			return "0.0";
		} else {
			return nullVar;
		}
	}

	public static String convertMillisecondsToMinutes(int milliSeconds) {
		int seconds = (int) (milliSeconds / 1000) % 60;
		int minutes = (int) ((milliSeconds / (1000 * 60)) % 60);
		int hours = (int) ((milliSeconds / (1000 * 60 * 60)) % 24);
		String totalTime = String.valueOf(hours) + ":" + String.valueOf(minutes) + ":" + String.valueOf(seconds);
		return totalTime;
	}

	public static ArrayList<aRoute> getRoutesWithFullXyString() {
		ArrayList<aRoute> returnAry = new ArrayList<aRoute>();

		ArrayList<aRoute> distinctAryRoutes = getDestictRoutes();
		for (int i = 0; i < distinctAryRoutes.size(); i++) {
			aRoute currentRoute = distinctAryRoutes.get(i);
			ArrayList<aRoute> routesWithXyStrings = selectRoutesViaSql("Select * from " + AllRoutes.ALLROUTESS_TABLE_NAME + " where " + AllRoutes.ALLROUTES_SegmentKey + " = "
					+ "'" + currentRoute.getSegmentKey() + "'");
			aRoute newroute = new aRoute(new String[0]);

			String concatenatedXyStrings = "";
			for (int ii = 0; ii < routesWithXyStrings.size(); ii++) {
				aRoute cRoute = routesWithXyStrings.get(ii);
				concatenatedXyStrings += cRoute.getXyString();
			}

			newroute = routesWithXyStrings.get(0);
			newroute.setXyString(concatenatedXyStrings);
			returnAry.add(newroute);
		}

		return returnAry;
	}

	private static ArrayList<aRoute> getDestictRoutes() {
		return selectRoutesViaSql("Select Distinct " + AllRoutes.ALLROUTES_SegmentKey + " from " + AllRoutes.ALLROUTESS_TABLE_NAME + " where " + AllRoutes.ALLROUTES_ID
				+ " is null or " + AllRoutes.ALLROUTES_ID + " = '';");
	}

	public static ArrayList<aRoute> selectRoutesViaSql(String sql) {
		Cursor cursor = mDb.rawQuery(sql, null);
		ArrayList<aRoute> routeArrayList = new ArrayList<aRoute>();
		try {

			int routeCount = cursor.getCount();

			if ((routeCount > 0) && (cursor.moveToFirst())) {
				for (int i = 0; i < routeCount; i++) {
					aRoute newRoute = new aRoute(new String[0]);
					String routeId = null;
					// routeAry[0] = "";
					// routeAry[1] = "";
					// routeAry[2]= "";
					// routeAry[3]= "";
					// routeAry[4]=
					// cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_KEY));
					// routeAry[5]=
					// cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_X));
					// routeAry[6]=
					// cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_Y));
					try {
						newRoute.setDistance(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_distance)));
					} catch (IllegalStateException e) {

					}
					try {
						newRoute.setRouteId(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_ID)));

					} catch (IllegalStateException e) {

					}
					try {
						newRoute.setXyString(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_XYString)));

					} catch (IllegalStateException e) {
					}
					try {
						newRoute.setElapsedTime(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_ElapsedTime)));

					} catch (IllegalStateException e) {
					}
					// routeAry[8]=
					// cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_pointCount));
					// routeAry[9]=
					// cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_topSpeed));
					// routeAry[10]=
					// cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_totalRise));
					// routeAry[11]=
					// cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_totalFall));

					// int elapsedTime = 0;

					// try{
					// elapsedTime =
					// Integer.valueOf(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_ElapsedTime)));
					// }catch (NumberFormatException e)
					// {

					// }

					// routeAry[12]= String.valueOf(elapsedTime);
					try {
						newRoute.setSegmentKey(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_SegmentKey)));
					} catch (IllegalStateException e) {
					}
					try {
						newRoute.setRouteName(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_NAME)));
					} catch (IllegalStateException e) {
					}
					try {
						newRoute.setPlaceName((cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_PLACE))));
					} catch (IllegalStateException e) {
					}
					try {
						newRoute.setTopSpeed((cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_topSpeed))));
					} catch (IllegalStateException e) {
					}
					try {
						newRoute.setCreateDate((cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_CREATE_DATE))));
					} catch (IllegalStateException e) {
					}
					routeArrayList.add(newRoute);
					cursor.moveToNext();
				}
			}
		} finally {
			cursor.close();
			// sqDB.close();
		}
		return routeArrayList;
	}

	public static ArrayList<aRoute> selectRoutesViaSqlDistinct(String sql) {
		Cursor cursor = mDb.rawQuery(sql, null);

		ArrayList<aRoute> routeArrayList = new ArrayList<aRoute>();

		int routeCount = cursor.getCount();

		if ((routeCount > 0) && (cursor.moveToFirst())) {
			for (int i = 0; i < routeCount; i++) {
				aRoute newRoute = new aRoute(new String[0]);

				try {
					newRoute.setRouteId(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_ID)));

				} catch (IllegalStateException e) {
				}
				try {
					newRoute.setSegmentKey(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_SegmentKey)));
				} catch (IllegalStateException e) {
				}
				try {
					newRoute.setRouteName(cursor.getString(cursor.getColumnIndex(AllRoutes.ALLROUTES_NAME)));
				} catch (IllegalStateException e) {
				}

				routeArrayList.add(newRoute);
				cursor.moveToNext();
			}
		}
		cursor.close();

		return routeArrayList;
	}

	public static aRoute getLastSegmentOfRoute(String segKey) {
		ArrayList<aRoute> aRouteForDetails = selectRoutesViaSql("Select " + AllRoutes.ALLROUTES_SegmentKey + "," + AllRoutes.ALLROUTES_distance + "," + AllRoutes.ALLROUTES_PLACE
				+ "," + AllRoutes.ALLROUTES_NAME + " from " + AllRoutes.ALLROUTESS_TABLE_NAME + " where " + AllRoutes.ALLROUTES_SegmentKey + " = '" + segKey + "'");
		return aRouteForDetails.get(aRouteForDetails.size() - 1);
	}

	public static String[] getRouteStringArrayFromPhoneDb(String userName, String tagBool, String uploadedBool) {
		String sqlArg = "";

		if (tagBool.equals("true")) {// not equals blank
			sqlArg = " not null";

		} else {// equal blank
			sqlArg = " = ''";
		}

		Cursor mCursor = mDb.query(AllRoutes.ALLROUTESS_TABLE_NAME, null, "allroutes_uploaded = " + "'" + uploadedBool + "' and " + "allroutes_taggerUsername " + sqlArg, null,
				null, null, null);

		int routeCount = mCursor.getCount();
		String[] routeAry = new String[routeCount];

		if ((routeCount > 0) && (mCursor.moveToFirst())) {
			// code for binding all of the coordinates to a listview
			for (int i = 0; i < routeCount; i++) {
				routeAry[i] = mCursor.getString(mCursor.getColumnIndex(AllRoutes.ALLROUTES_KEY));
				mCursor.moveToNext();
			}
		}
		mCursor.close();
		return routeAry;
	}

	public static String getAllRecordsFromTable(String tableName) {
		Cursor mCursor = mDb.query(tableName, null, null, null, null, null, null);
		mCursor.getColumnNames();
		String rowValues = "";
		int rowCount = mCursor.getCount();
		try {
			String[] columnNames = mCursor.getColumnNames();

			for (int i = 0; i < columnNames.length; i++) {

				rowValues += "|\t\t\t\t\t\t|" + fixStringLength(columnNames[i]);

			}

			rowValues += "\n";
			// 11-22 11:51:20.052: I/System.out(21556): ColumnHeaders:
			// |_id|allroutes_place|allroutes_id|allroutes_name|allroutes_key|allroutes_x|allroutes_y|allroutes_xy_string|allroutes_createDate|allroutes_uploaded|allroutes_taggerUsername|allroutes_elapsedTime|allroutes_startDateTime|allroutes_endDateTime|allroutes_distance|allroutes_pointCount|allroutes_topSpeed|allroutes_totalRise|allroutes_totalFall|allroutes_currentTrackBool|allroutes_segmentKey
			if ((rowCount > 0) && (mCursor.moveToFirst())) {

				// code for binding all of the coordinates to a listview/

				for (int i = 0; i < rowCount; i++) {
					// create one row to display in the log
					for (int ii = 0; ii < columnNames.length; ii++) {

						// DO NOT DISPLAY XY STRING...ITS TOO LONG
						if (!columnNames[ii].toString().equals("allroutes_xy_string")) {
							rowValues += "|\t\t\t\t\t\t|" + fixStringLength(mCursor.getString(ii));
							System.out.println("columnNames[ii].toString(): " + columnNames[ii].toString());
							System.out.println("mCursor.getString(ii): " + mCursor.getString(ii));
						}

					}
					rowValues += "\n";
					mCursor.moveToNext();
				}
			}
		} finally {
			mCursor.close();
		}
		return rowValues;
	}

	private static String fixStringLength(String text) {
		if (text != null)
			while (text.length() < 40) {
				text += " ";
			}
		return text;
	}

	public static String getTimeFromMilliseconds(int milliSeconds) {
		int seconds = (int) (milliSeconds / 1000) % 60;
		int minutes = (int) ((milliSeconds / (1000 * 60)) % 60);
		int hours = (int) ((milliSeconds / (1000 * 60 * 60)) % 24);
		String totalTime = String.valueOf(hours) + ":" + String.valueOf(minutes) + ":" + String.valueOf(seconds);
		return totalTime;
	}

	public static void initializeRouteVariables() {
		// initialize routeVariables
		Route.setrecordCount("");
		Route.setrecordCount("");
		Route.setRoutaverageSpeed("");
		Route.setRouttopSpeed("");
		Route.setRouttotalTime("");
		Route.settotalVert("");
		Route.setRoutdistance("");
		Route.setfall("");
		Route.setrise("");
	}

	public static String getARoute(String routeKey) {
		String sqlArg = "";

		String xyString = "";
		Cursor mCursor = mDb.query(RouteDatabase.AllRoutes.ALLROUTESS_TABLE_NAME, null, AllRoutes.ALLROUTES_KEY + " = " + "'" + routeKey + "'", null, null, null, null);

		int routeCount = mCursor.getCount();

		if ((routeCount > 0) && (mCursor.moveToFirst())) {
			// code for binding all of the coordinates to a listview
			for (int i = 0; i < routeCount; i++) {
				xyString = mCursor.getString(mCursor.getColumnIndex(AllRoutes.ALLROUTES_XYString));

				mCursor.moveToNext();
			}
		}
		mCursor.close();
		return xyString;
	}

	public static ArrayList<aRoute> getARouteDynParam(String selectionArgs) {
		ArrayList<aRoute> currentRouteArray = new ArrayList<aRoute>();

		Cursor mCursor = mDb.query(RouteDatabase.AllRoutes.ALLROUTESS_TABLE_NAME, null, selectionArgs, null, null, null, null);

		int routeCount = mCursor.getCount();

		if ((routeCount > 0) && (mCursor.moveToFirst())) {
			for (int i = 0; i < routeCount; i++) {
				aRoute cRoute = new aRoute(null);
				cRoute.setXyString(mCursor.getString(mCursor.getColumnIndex(AllRoutes.ALLROUTES_XYString)));
				cRoute.setRouteId(mCursor.getString(mCursor.getColumnIndex(AllRoutes.ALLROUTES_ID)));
				cRoute.setRouteKey((mCursor.getString(mCursor.getColumnIndex(AllRoutes.ALLROUTES_KEY))));
				cRoute.setPlaceName(mCursor.getString(mCursor.getColumnIndex(AllRoutes.ALLROUTES_PLACE)));
				cRoute.setDistance(mCursor.getString(mCursor.getColumnIndex(AllRoutes.ALLROUTES_distance)));
				cRoute.setPointCount(mCursor.getString(mCursor.getColumnIndex(AllRoutes.ALLROUTES_pointCount)));
				cRoute.setElapsedTime(mCursor.getString(mCursor.getColumnIndex(AllRoutes.ALLROUTES_ElapsedTime)));
				cRoute.setTopSpeed(mCursor.getString(mCursor.getColumnIndex(AllRoutes.ALLROUTES_topSpeed)));
				cRoute.setRouteId(mCursor.getString(mCursor.getColumnIndex(AllRoutes.ALLROUTES_ID)));
				if (mCursor.getString(mCursor.getColumnIndex(AllRoutes.ALLROUTES_currentTrackBool)).equals("true")) {
					cRoute.setCurrentlyTracking(true);
				} else {
					cRoute.setCurrentlyTracking(false);
				}
				currentRouteArray.add(cRoute);
				mCursor.moveToNext();
			}
		}
		mCursor.close();
		return currentRouteArray;
	}

	public static String getARouteDynParamReturnXyString(String searchParamter, String paramterValue) {
		String xyString = "";

		Cursor mCursor = mDb.query(RouteDatabase.AllRoutes.ALLROUTESS_TABLE_NAME, null, searchParamter + " = " + "'" + paramterValue + "'", null, null, null, null);

		int routeCount = mCursor.getCount();

		if ((routeCount > 0) && (mCursor.moveToFirst())) {
			for (int i = 0; i < routeCount; i++) {
				xyString += mCursor.getString(mCursor.getColumnIndex(AllRoutes.ALLROUTES_XYString));
				mCursor.moveToNext();
			}
		}
		mCursor.close();
		return xyString;
	}

	public static aRoute getAllElementsOfRouteByKey(String routeKey) {

		Cursor cursor = mDb.query(RouteDatabase.AllRoutes.ALLROUTESS_TABLE_NAME, null, AllRoutes.ALLROUTES_KEY + " = ?", new String[] { routeKey }, null, null, null);

		return parseRouteCursor(cursor).get(0);
	}

	public aRoute getAllElementsOfRouteByDateSegKey(String segKey) {
		Cursor cursor = mDb.query(RouteDatabase.AllRoutes.ALLROUTESS_TABLE_NAME, null, AllRoutes.ALLROUTES_SegmentKey + " = ?", new String[] { segKey }, null, null, null);

		return parseRouteCursor(cursor).get(cursor.getCount() - 1);
	}

	public static String getARouteReturnId(String routeKey) {
		String sqlArg = "";

		String routeId = "";
		Cursor mCursor = mDb.query(RouteDatabase.AllRoutes.ALLROUTESS_TABLE_NAME, new String[] { AllRoutes.ALLROUTES_KEY, AllRoutes.ALLROUTES_ID, AllRoutes.ALLROUTES_XYString },
				AllRoutes.ALLROUTES_KEY + " = " + "'" + routeKey + "'", null, null, null, null);

		int routeCount = mCursor.getCount();

		if ((routeCount > 0) && (mCursor.moveToFirst())) {

			// code for binding all of the coordinates to a listview

			for (int i = 0; i < routeCount; i++) {
				routeId = mCursor.getString(mCursor.getColumnIndex(AllRoutes.ALLROUTES_ID));

				mCursor.moveToNext();
			}
			mCursor.close();
		}
		return routeId;
	}

	/**
	 * Get Places from Phone Database -- Override with a blank value if you want
	 * all images;
	 */

	public static ArrayList<Image> getImageaFromPhoneDb(String placeName) {

		String placeNameClause;

		if (!placeName.equals("")) {
			placeNameClause = "image_place_name = " + "'" + placeName + "'";
		} else {
			placeNameClause = null;
		}

		ArrayList<Image> imgAry = new ArrayList<Image>();
		Cursor mCursor = mDb.query(RouteDatabase.Images.IMAGE_TABLE_NAME, new String[] { Images.IMAGE_PATH, Images.IMAGE_PLACE_NAME, Images.IMAGE_ID, Images.IMAGE_X,
				Images.IMAGE_Y, Images.IMAGE_CREATEDATE, Images.IMAGE_EARTH_GALLERY }, placeNameClause, null, null, null, null);

		int imageCount = mCursor.getCount();

		if ((imageCount > 0) && (mCursor.moveToLast())) {
			// code for binding all of the coordinates to a listview
			for (int i = 0; i < imageCount; i++) {

				Image newImage = new Image();
				newImage.setImgType(OFFLINE_IMG);
				newImage.setImageId(mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_ID)));
				newImage.setImgCreateDate(mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_CREATEDATE)));
				String imgPath = mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_PATH));
				String imgThumbPath = imgPath.replace("Photos/", "Photos/Thumbs/");
				newImage.setImgThumbNail(imgThumbPath);
				newImage.setImgPath(imgPath);
				newImage.setImgX(mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_X)));
				newImage.setImgY(mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_Y)));
				newImage.setImgEarthGal(false);
				newImage.setImgPlace(mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_PLACE_NAME)));

				File f = new File(newImage.getimgPath());
				if (f.exists()) {
					imgAry.add(newImage);
				}
				mCursor.moveToPrevious();
			}
			;
		}
		mCursor.close();

		return imgAry;
	}

	public Image getaImageaFromPhoneDbById(String imageId) {
		Image fetchedImage = null;
		Cursor mCursor = mDb.query(RouteDatabase.Images.IMAGE_TABLE_NAME, new String[] { Images.IMAGE_PATH, Images.IMAGE_PLACE_NAME, Images.IMAGE_ID, Images.IMAGE_X,
				Images.IMAGE_Y, Images.IMAGE_CREATEDATE, Images.IMAGE_EARTH_GALLERY }, "image_id = " + "'" + imageId + "'", null, null, null, null);

		int imageCount = mCursor.getCount();

		if ((imageCount > 0) && (mCursor.moveToFirst())) {
			// code for binding all of the coordinates to a listview
			for (int i = 0; i < imageCount; i++) {
				String[] img = new String[6];
				img[0] = mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_ID));
				img[1] = mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_PATH));
				img[2] = mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_PLACE_NAME));
				img[3] = mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_CREATEDATE));
				img[4] = mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_X));
				img[5] = mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_Y));

				Image newImage = new Image();
				newImage.setImageId(mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_ID)));
				newImage.setImgCreateDate(mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_CREATEDATE)));
				newImage.setImgPath(mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_PATH)));
				newImage.setImgX(mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_X)));
				newImage.setImgY(mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_Y)));
				newImage.setImgEarthGal(false);
				newImage.setImgPlace(mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_PLACE_NAME)));

				fetchedImage = newImage;
				mCursor.moveToNext();
			}
			;

		}
		mCursor.close();
		return fetchedImage;
	}

	public static Image getaImageaFromPhoneDbImgPath(String imgPath) {
		Image fetchedImage = null;
		Cursor mCursor = mDb.query(RouteDatabase.Images.IMAGE_TABLE_NAME, new String[] { Images.IMAGE_PATH, Images.IMAGE_PLACE_NAME, Images.IMAGE_ID, Images.IMAGE_X,
				Images.IMAGE_Y, Images.IMAGE_CREATEDATE, Images.IMAGE_EARTH_GALLERY }, "image_path = " + "'" + imgPath + "'", null, null, null, null);

		int imageCount = mCursor.getCount();

		if ((imageCount > 0) && (mCursor.moveToFirst())) {
			// code for binding all of the coordinates to a listview
			for (int i = 0; i < imageCount; i++) {

				Image newImage = new Image();
				newImage.setImageId(mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_ID)));
				newImage.setImgCreateDate(mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_CREATEDATE)));
				newImage.setImgPath(mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_PATH)));
				newImage.setImgX(mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_X)));
				newImage.setImgY(mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_Y)));
				newImage.setImgEarthGal(Boolean.valueOf(mCursor.getString(mCursor.getColumnIndex(Images.IMAGE_EARTH_GALLERY))));
				fetchedImage = newImage;

				mCursor.moveToNext();
			}
			;

		}
		mCursor.close();
		return fetchedImage;
	}

	protected static void insertNewImage(Image image) {

		mDb.beginTransaction();

		try {

			ContentValues imageRecord = new ContentValues();
			imageRecord.put("image_path", image.getimgPath());
			imageRecord.put("image_place_name", image.getimgPlace());
			imageRecord.put("image_createdate", image.getimgCreateDate());
			imageRecord.put("image_x", image.getimgX());
			imageRecord.put("image_y", image.getimgY());
			imageRecord.put("image_id", image.getimageId());

			mDb.insert(RouteDatabase.Images.IMAGE_TABLE_NAME, null, imageRecord);

			mDb.setTransactionSuccessful();

		} finally {
			mDb.endTransaction();
		}
	}

	public static void initiateRouteOnlineDatabaseSync() {
		insertAllRoutesOnlineDb(getRoutesWithFullXyString());
	}

	public static void insertAllRoutesOnlineDb(ArrayList<aRoute> rootAry) {

		// (String userName, String placeName, String createDate, String
		// xyStringSvc)
		for (int i = 0; i < rootAry.size(); i++) {
			String routeId = "";

			aRoute aRouteOb = rootAry.get(i);
			System.out.println("Globals.getpersonid: " + Globals.getpersonId() + DTD.getPersonId());
			routeId = insertRouteOnline(DTD.getPersonId(), "", aRouteOb.getPlaceName(), aRouteOb.getCreateDate(), aRouteOb.getXyString(), aRouteOb, "", "0",
					aRouteOb.getDistance(), aRouteOb.getRouteName(), aRouteOb.getTopSpeed());

			// update the phone route id with the corresponding online route id
			// that is returned.
			try {
				int isRouteIdNumeric = Integer.valueOf(routeId);
				if (routeId != null) {
					if (!routeId.equals("")) {
						boolean responseBool = updateRouteId(aRouteOb.getSegmentKey(), routeId);
						if (responseBool) {
							updateUploadBool(aRouteOb.getSegmentKey(), "true");
						}
					}
				}
			} catch (Exception e) {

			}
		}
	}

	public static String getPlaceMarkFromOnlineDb(String placeMarkId) {
		String placemarkString = null;
		SoapObject request = new SoapObject(NAMESPACE, APLACEMARK_METHOD_NAME);
		request.addProperty("pmid", placeMarkId);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

		envelope.dotNet = true;
		SoapObject result = null;
		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 3000);
		envelope.setOutputSoapObject(request);

		try {

			androidHttpTransport.call(APLACEMARK_ACTION, envelope);

			placemarkString = envelope.getResponse().toString();

		} catch (SoapFault e1) {

		} catch (IOException e) {

		} catch (XmlPullParserException e) {
		}
		return placemarkString;
	}

	public static Bitmap LoadImage(String URL, BitmapFactory.Options options) {
		Bitmap bitmap = null;
		InputStream in = null;
		try {
			in = OpenHttpConnection(URL);
			bitmap = BitmapFactory.decodeStream(in, null, options);
			in.close();
		} catch (IOException e1) {

		}
		return bitmap;
	}

	private static InputStream OpenHttpConnection(String strURL) throws IOException {
		InputStream inputStream = null;
		URL url = new URL(strURL);
		URLConnection conn = url.openConnection();
		try {
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				inputStream = httpConn.getInputStream();
			}
		} catch (Exception ex) {
		}
		return inputStream;
	}

	public JSONObject createJsonObFromXyString(String userName, String placeName, String xyString) throws JSONException {
		JSONObject jArrayFacebookData = new JSONObject();
		JSONObject jObjectType = new JSONObject();
		// x + delimiter + y + delimiter + datetime + delimiter + altitude +
		// delimiter + speed + delimiter;
		String[] xyArray = xyString.split(",\\s*");

		jArrayFacebookData.put("route", jObjectType);

		for (int i = 0; i < xyArray.length; i++) {
			// 2nd array for user information
			JSONObject jObjectData = new JSONObject();
			String x = xyArray[i];
			String y = xyArray[i + 1];
			String createDate = xyArray[i + 2];
			String altitude = xyArray[i + 3];
			String speed = xyArray[i + 4];

			// Create Json Object using Facebook Data

			jObjectData.put("x", x);
			jObjectData.put("y", y);
			jObjectData.put("place", placeName);
			jObjectData.put("createDate", createDate);
			jObjectData.put("speed", speed);
			jObjectData.put("altitude", altitude);

			jArrayFacebookData.put("location", jObjectData);
		}
		jArrayFacebookData.put("route", jObjectType);
		return jArrayFacebookData;
	}

	public static void sendEmail(String from, String to, String body, String url, String subject) {

		String response = null;

		SoapObject request = new SoapObject(NAMESPACE, Email_Method_Name);
		request.addProperty("from", from);
		request.addProperty("to", to);
		request.addProperty("body", body);
		request.addProperty("subject", subject);
		request.addProperty("url", url);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

		envelope.dotNet = true;
		SoapObject result = null;

		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 3000);
		envelope.setOutputSoapObject(request);

		try {

			androidHttpTransport.call(Email_Action, envelope);

			// response = envelope.getResponse().toString();

		} catch (SoapFault e1) {
			// TODO Auto-generated catch block

		} catch (IOException e) {
			// TODO Auto-generated catch block

		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block

		}
	}

	public static String accountsMethod(String personId, String friendId, String methodType) {
		String responseVar = "";
		String[] currentFriends;
		SoapObject request = new SoapObject(NAMESPACE, Account_Method_Name);
		// String user = loadPreferences("email");

		request.addProperty("personId", personId);
		request.addProperty("friendId", friendId);
		request.addProperty("methodType", methodType);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 3000);

		try {

			androidHttpTransport.call(Account_Action, envelope);
			responseVar = envelope.getResponse().toString();

		} catch (Exception e) {
			e.printStackTrace();

		}
		return responseVar;
	}

	public void setRouteStatsFromXystring(String xyString) {
		double totalSpeed = 0;
		double totalDistance = 0;
		double topSpeed = 0;
		double previousX = 0;
		double previousY = 0;

		String tempData[] = xyString.split(",");
		int recordCount = tempData.length / 5;

		if (recordCount > 10) {
			for (int i = 0; i < tempData.length; i++) {

				try {

					if (topSpeed < Double.valueOf(tempData[i + 4])) {
						topSpeed = Double.valueOf(tempData[i + 4]);
						Globals.settopSpeedCoords(tempData[i] + "," + tempData[i + 1]);
					}

					Double x = Double.valueOf(tempData[i]);
					Double y = Double.valueOf(tempData[i + 1]);

					if (i >= 5) {
						double distanceToAdd = findCoordinateDistance(previousX, previousY, x, y);

						previousX = x;
						previousY = y;
						totalDistance += distanceToAdd;
					} else {
						previousX = x;
						previousY = y;
					}

					totalSpeed += Double.valueOf(tempData[i + 4]);

				} catch (NumberFormatException e) {

				} catch (ArrayIndexOutOfBoundsException e) {

					insertErrorLog("tester", "Error Resulting from Bad Data: xyString: " + xyString + " Message: " + e.getMessage());
				}

				i += 4;
			}

			Route.setrecordCount(String.valueOf(recordCount));
			Route.setRoutaverageSpeed(String.valueOf(twoDForm.format(totalSpeed / recordCount)) + " " + GPShare.UNIT_OF_MEASURE_TXT_SPEED);
			Route.setRouttopSpeed(String.valueOf(twoDForm.format(topSpeed)) + " mph");
			Route.setRoutdistance(String.valueOf(convertMetersPrefUom(totalDistance, GPShare.getUOM()) + " " + GPShare.UNIT_OF_MEASURE_TXT_DIST));
		}
	}

	protected Double findCoordinateDistance(Double startLat, Double startLong, Double endLat, Double endLong) {
		Double distance = null;

		String startingLoc = null;
		Location start = new Location(startingLoc);
		String endingLoc = null;
		Location end = new Location(endingLoc);

		start.setLatitude(startLat);
		start.setLongitude(startLong);

		end.setLatitude(endLat);
		end.setLongitude(endLong);
		distance = Double.valueOf(start.distanceTo(end));

		return distance;
	}

	public static double convertMetersPrefUom(double meters, String uom) {
		if (uom.equals("") || !uom.equals("0")) {
			return Double.valueOf(twoDForm.format((meters / 1000)));
		} else {
			double miles = 0;
			miles = (meters * 3.2808) / 5280;
			return Double.valueOf(twoDForm.format((miles)));
		} // to Feet

	}

	public static double convertMetersToFeet(double meters) {
		// function converts Feet to Meters.

		double feet = 0;
		feet = (meters * 3.2808); // official conversion rate of Meters to Feet
		return Double.valueOf(twoDForm.format((feet)));
	}

	public static String totalTime(String beginTime, String endTime) {
		String totalTime = "";
		boolean begindateTimeAM = true;
		int beginDateIndex = beginTime.lastIndexOf("AM");
		if (beginDateIndex == -1) {
			begindateTimeAM = false;
		}

		String[] beginDateTime = beginTime.replace("_", "-").replace("AM", " AM").split("SS");
		String beginDate = beginDateTime[0];
		String begintime = beginDateTime[1];

		String[] endDateTime = endTime.replace("_", ":").replace("PM", " PM").split("SS");
		String enddate = endDateTime[0];
		String endtime = endDateTime[1];

		return totalTime;
	}

	public static String saveLog(String UserName) {
		String logString = null;
		Process mLogcatProc = null;
		BufferedReader reader = null;
		try {
			mLogcatProc = Runtime.getRuntime().exec(new String[] { "logcat", "-d", "AndroidRuntime:E [Your Log Tag Here]:V *:S" });
			reader = new BufferedReader(new InputStreamReader(mLogcatProc.getInputStream()));
			String line;
			final StringBuilder log = new StringBuilder();
			log.append("Error Log: for USER NAME: " + UserName + " PHONE: " + Build.MODEL + " DATE: " + DateNow.getCurrentDateTime() + "\n");
			String separator = System.getProperty("line.separator");
			while ((line = reader.readLine()) != null) {
				log.append(line);
				log.append(separator);
			}
			// do whatever you want with the log. I'd recommend using Intents to
			// create an email
			logString = log.toString();
		} catch (IOException e) {
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {

				}
		}
		return logString;
	}

	public String concatenateXyStrings(ArrayList<aRoute> aRoute) {
		String xyString = null;
		for (int i = 0; i < aRoute.size(); i++) {
			aRoute route = aRoute.get(i);
			xyString += route.getXyString();
		}
		return xyString;

	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}

	public static void syncWithOnlinePlaces() {
		// TODO Auto-generated method stub
		ArrayList<aPlace> places = fetchPlacesOnline(DTD.getPersonId());
		for (int i = 0; i < places.size(); i++) {
			aPlace p = places.get(i);
			insertNewPlace(p);
		}
	}

	public static void syncWithOnlineRouteData() {

		String routeIdsString = getRouteIdString();
		ArrayList<aRoute> routes = getRoutesByPersonId(DTD.getPersonId(), routeIdsString);
		if (routes != null) {
			if (routes.size() != 0) {
				for (int i = 0; i < routes.size(); i++) {
					aRoute cRoute = routes.get(i);
					insertNewPlace(new aPlace(new String[] { "", cRoute.getPlaceName(), "" }));
					createNewRouteWithOb(cRoute);

					// DBTransactions.insertNewPlace(p);
				}
				syncWithOnlineRouteData();
			}
		}
	}

	public static void insertNewPlace(aPlace placeOb) {

		ContentValues routeRecordToAdd = new ContentValues();
		routeRecordToAdd.put(Places.PLACES_ID, placeOb.getPlaceId());
		routeRecordToAdd.put("places_name", placeOb.getPlaceName());
		routeRecordToAdd.put("places_current_place", true);
		mDb.insert(Places.PLACES_TABLE_NAME, null, routeRecordToAdd);
	}

	private static ArrayList<aRoute> getRoutesByPersonId(String loadPreferences, String routeIdsString) {

		return null;
	}

	public static String getRouteIdString() {
		ArrayList<aRoute> routeStringAry = getAllRoutesFromPhoneDb();
		String returnaRouteIdString = "";
		for (aRoute rl : routeStringAry) {
			if (rl.getRouteId() != null) {
				returnaRouteIdString += rl.getRouteId() + ",";
			}
		}
		return returnaRouteIdString;
	}

	public static void createNewRouteWithOb(aRoute newRoute) {

		mDb.beginTransaction();

		try {

			ContentValues allroutesRecordToAdd = new ContentValues();
			allroutesRecordToAdd.put("allroutes_id", newRoute.getRouteId());
			allroutesRecordToAdd.put("allroutes_key", newRoute.getRouteKey());
			allroutesRecordToAdd.put("allroutes_place", newRoute.getPlaceName());
			allroutesRecordToAdd.put("allroutes_xy_string", newRoute.getXyString());
			allroutesRecordToAdd.put("allroutes_createDate", GLibTrans.getDateTime());
			allroutesRecordToAdd.put(AllRoutes.ALLROUTES_distance, newRoute.getDistance());

			allroutesRecordToAdd.put("allroutes_uploaded", "true");
			allroutesRecordToAdd.put("allroutes_taggerUsername", "");
			allroutesRecordToAdd.put("allroutes_currentTrackBool", "false");
			allroutesRecordToAdd.put(AllRoutes.ALLROUTES_startDateTime, newRoute.getCreateDate());
			allroutesRecordToAdd.put(AllRoutes.ALLROUTES_SegmentKey, newRoute.getCreateDate());
			mDb.insert(AllRoutes.ALLROUTESS_TABLE_NAME, null, allroutesRecordToAdd);

			// update coordinate counter with current coordinate count with out
			// actually querying the database.
			// updateCoordCounter(coordinateCount);

			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
		}
	}

	public static aUser getProfileStatsByPersonId(String string) {

		return null;
	}
}
