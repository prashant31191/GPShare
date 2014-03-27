package com.devtechdesign.gpshare.elements;

import android.graphics.Bitmap;

public class Image implements Comparable {

	private int		id;
	private String	imageId;
	private String	imgPath;
	private String	imgPlace;
	private String	imgCreateDate;
	private String	imgX;
	private String	imgY;
	private Boolean	imgEarthGal;
	private String	imgThumbNail;
	private Boolean	selectedForUpload;
	private String	LargeUrl;
	private int		imgType;

	private Bitmap	bitmap	= null;

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public String getImgPlace() {
		return imgPlace;
	}

	public void setImgPlace(String imgPlace) {
		this.imgPlace = imgPlace;
	}

	public String getImgCreateDate() {
		return imgCreateDate;
	}

	public void setImgCreateDate(String imgCreateDate) {
		this.imgCreateDate = imgCreateDate;
	}

	public String getImgX() {
		return imgX;
	}

	public void setImgX(String imgX) {
		this.imgX = imgX;
	}

	public String getImgY() {
		return imgY;
	}

	public void setImgY(String imgY) {
		this.imgY = imgY;
	}

	public String getimageId() {
		return imageId;
	}

	public String getimgPath() {
		return imgPath;
	}

	public String getimgPlace() {
		return imgPlace;
	}

	public String getimgCreateDate() {
		return imgCreateDate;
	}

	public String getimgX() {
		return imgX;
	}

	public String getimgY() {
		return imgY;
	}

	public Boolean getImgEarthGal() {
		return imgEarthGal;
	}

	public void setImgEarthGal(Boolean imgEarthGal) {
		this.imgEarthGal = imgEarthGal;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int compareTo(Image image) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public Boolean getSelectedForUpload() {
		return selectedForUpload;
	}

	public void setSelectedForUpload(Boolean selectedForUpload) {
		this.selectedForUpload = selectedForUpload;
	}

	public String getLargeUrl() {
		return LargeUrl;
	}

	public void setLargeUrl(String largeUrl) {
		LargeUrl = largeUrl;
	}

	public int getImgType() {
		return imgType;
	}

	public void setImgType(int imgType) {
		this.imgType = imgType;
	}

	public String getImgThumbNail() {
		return imgThumbNail;
	}

	public void setImgThumbNail(String imgThumbNail) {
		this.imgThumbNail = imgThumbNail;
	}

	@Override
	public int compareTo(Object another) {
		// TODO Auto-generated method stub
		return 0;
	}
}
