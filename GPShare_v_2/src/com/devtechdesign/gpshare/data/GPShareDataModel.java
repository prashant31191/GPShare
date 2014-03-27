package com.devtechdesign.gpshare.data;

import java.util.ArrayList;
import java.util.LinkedList;

import com.devtechdesign.gpshare.layouts.vwJournal;
import com.dtd.dbeagen.db.elements.aJournals;
import com.dtd.dbeagen.db.elements.aMyPlaces;
import com.dtd.dbeagen.db.elements.aPlaceMark;
import com.dtd.dbeagen.db.elements.aTrip;

public class GPShareDataModel {

	private static GPShareDataModel instance = new GPShareDataModel();
	public ArrayList<aMyPlaces> places = new ArrayList<aMyPlaces>();
	public ArrayList<aJournals> journals = new ArrayList<aJournals>();
	public ArrayList<aTrip> trips;
	public ArrayList<vwJournal> vwJournals;
	public LinkedList<aPlaceMark> placeMarks = new  LinkedList<aPlaceMark>(); 
	 

	private GPShareDataModel() {

	}

	public static GPShareDataModel getInstance() {
		return instance;
	}
}
