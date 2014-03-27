package com.devtechdesign.gpshare.lists;

public interface IListManager {

	/**
	 * Set the text of the manager. Places, Trips, Activities are all managers
	 * Example Text: R.string.place_manager = Place Manager -- Use Place manager
	 * to select, add, update and delete places.
	 * 
	 * @param txtPlaceManager
	 */
	public void setTxtLabelViewForManager(int resourceId);
}
