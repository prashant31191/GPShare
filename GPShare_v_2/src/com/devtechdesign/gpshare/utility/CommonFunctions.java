package com.devtechdesign.gpshare.utility;

public class CommonFunctions {
	
	public static String[] getXyValuesFromCoords(String currentCoords)
	{
		String[] coords = currentCoords.toString().split(",\\s*"); 
		return coords;
	}

}
