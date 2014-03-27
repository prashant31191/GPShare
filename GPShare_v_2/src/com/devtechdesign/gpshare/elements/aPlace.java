package com.devtechdesign.gpshare.elements;

public class aPlace {

	private String[] properties;
	private String placeName;
	private String placeId;
	private String placeRouteCount;

	public aPlace(String[] myProperties) {
		properties = myProperties;
		initialize(properties);
	}

	private void initialize(String[] myProperties) {
		String[] temp = myProperties;

		placeId = temp[0];
		placeName = temp[1];
		placeRouteCount = temp[2];

	}

	public String getPlaceName() {
		return placeName;
	}

	public String getPlaceId() {
		return placeId;
	}

	public String getplaceRouteCount() {
		return placeRouteCount;
	}
}
