package com.devtechdesign.gpshare.elements;

public class Route {

	public static String lat; 
	public static String lon; 
	public static String currentDtm; 
    public static String elevation; 
    public static boolean newGpxBool; 
    public static Route[] routeCoordList; 
    public static String totalTime; 
    public static String topSpeed; 
    public static String averageSpeed; 
    public static String distance; 
    public static String recordCount; 
    public static String totalVert;
    public static String rise; 
    public static String fall; 
	
    public static String getFall(){
		  
		  return fall;
	  } 

	  public static void setfall(String fall){
			 
		  Route.fall = fall; 
	  }
	  
 public static String getrise(){
		  
		  return rise;
	  } 

	  public static void setrise(String rise){
			 
		  Route.rise = rise; 
	  }
	  
    public static String gettotalVert(){
		  
		  return totalVert;
	  } 

	  public static void settotalVert(String totalVert){
			 
		  Route.totalVert = totalVert; 
	  }
	  
    public static String getrecordCount(){
		  
		  return recordCount;
	  } 

	  public static void setrecordCount(String recordCount){
			 
		  Route.recordCount = recordCount; 
	  }
	  
    public static String getdistance(){
		  
		  return distance;
	  } 

	  public static void setRoutdistance(String distance){
			 
		  Route.distance = distance; 
	  }
	  
    public static String getaverageSpeed(){
		  
		  return averageSpeed;
	  } 

	  public static void setRoutaverageSpeed(String averageSpeed){
			 
		  Route.averageSpeed = averageSpeed; 
	  }
	  
	  
    public static String gettopSpeed(){
		  
		  return topSpeed;
	  } 

	  public static void setRouttopSpeed(String topSpeed){
			 
		  Route.topSpeed = topSpeed; 
	  }
	  
    public static Route[] getRouteCoordList(){
		  
		  return routeCoordList;
	  } 

	  public static void setRouteCoordList(Route[] routeCoordList){
			 
		  Route.routeCoordList = routeCoordList; 
	  }
	  
	  
    public static String gettotalTime(){
		  
		  return totalTime;
	  } 

	  public static void setRouttotalTime(String totalTime){
			 
		  Route.totalTime = totalTime; 
	  }
	  
		  public String getLon(){
			  
			  return lon;
		  } 
	
		  public void setLon(String lon){
				 
			  Route.lon = lon; 
		  }
		  
		  public String getLat(){
			  
			  return lat;
		  } 
	
		  public void setLat(String lat){
				 
			  Route.lat = lat; 
		  }
	
		  public String getCurrentDtm(){
	 
			  return currentDtm;
		  } 
	
		  public void setCurrentDtm(String currentDtm){
				 
			  Route.currentDtm = currentDtm; 
		  }
		  
		  public static String getEleveation(){
				 
			  return elevation;
		  } 
	
		  public void setElevation(String elevation){
				 
			  Route.elevation = elevation; 
		  }
		  
		  public boolean getNewGpxBool(){
				 
			  return newGpxBool;
		  } 
	
		  public void setNewGpxBool(boolean newGpxBool){
				 
			  Route.newGpxBool = newGpxBool; 
		  }
		  
}
