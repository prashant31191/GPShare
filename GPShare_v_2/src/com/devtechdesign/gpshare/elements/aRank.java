package com.devtechdesign.gpshare.elements; 

public class aRank {
	//(String userName, String placeName, String createDate, String xyStringSvc)
    
    
	private String [] properties;
	private String personId;
	private String firstLastName;
	private String createDate = "";
	private String distance;
	private String picUrl; 
	
	public aRank(String []myProperties){
		properties = myProperties;
		initialize(properties);
	}
	
	private void initialize(String []myProperties) {
		String []temp = myProperties;
		for(int a = 0; a < myProperties.length; a++){
			
				//System.out.println(" Property Index: " + a + " Property: " + myProperties[a]); 
		}
		
		setPersonId(temp[0]);
		setDistance(temp[1]);
		setFirstLastName(temp[2]);
		setPicUrl(temp[3]); 
	}
    
	public String getCreateDate(){
		return createDate;
	}
	
	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public String getFirstLastName() {
		return firstLastName;
	}

	public void setFirstLastName(String firstLastName) {
		this.firstLastName = firstLastName;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}
}
