package com.devtechdesign.gpshare.data.web;

import java.util.ArrayList;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import com.devtechdesign.gpshare.utility.SoapInterface;
import com.dtd.dbeagen.db.elements.aPlaceMark;

public class WebTrans implements SoapInterface {

	public static ArrayList<aPlaceMark> getImageBackUps() {
		ArrayList<aPlaceMark> ImageBackUps = new ArrayList<aPlaceMark>();

		SoapObject request = new SoapObject(NAMESPACE, getImageBackUpsMethodName);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url, 4000);

		try {

			androidHttpTransport.call(getImageBackUpsAction, envelope);

			SoapObject b = (SoapObject) envelope.getResponse();

			int propertyCount = b.getPropertyCount();

			for (int i = 0; i < propertyCount; i++) {

				SoapObject input = (SoapObject) b.getProperty(i);

				String ImageId = input.getProperty("ImageId").toString();
				String ImgPlace = input.getProperty("ImgPlace").toString();
				String ImgPath = input.getProperty("ImgPath").toString();
				String ImgCreateDate = input.getProperty("ImgCreateDate").toString();
				String ImgX = input.getProperty("ImgX").toString();
				String ImgY = input.getProperty("ImgY").toString();

				aPlaceMark newImage = new aPlaceMark();
				newImage.setPlaceId(ImageId);
				newImage.setCreateDate(ImgCreateDate);
				newImage.setLargeUrl(ImgPath);
				String thumbUrl = ImgPath.replace("/Photos/", "/Photos/Thumbs/");
				newImage.setpicUrl(thumbUrl);
				newImage.setx(ImgX);
				newImage.sety(ImgY);
				newImage.setPlaceName(ImgPlace);
				ImageBackUps.add(newImage);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ImageBackUps;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {

	}
}
