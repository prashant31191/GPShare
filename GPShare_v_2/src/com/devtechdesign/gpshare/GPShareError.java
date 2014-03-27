package com.devtechdesign.gpshare;

/**
 * Encapsulation of a GPShare Error: a web request request that could not be
 * fulfilled.
 * 
 * @author matthew roberts
 */
public class GPShareError extends Throwable {

	private static final long serialVersionUID = 1L;

	private int mErrorCode = 0;
	private String mErrorType;

	public GPShareError(String message) {
		super(message);
	}

	public GPShareError(String message, String type, int code) {
		super(message);
		mErrorType = type;
		mErrorCode = code;
	}

	public int getErrorCode() {
		return mErrorCode;
	}

	public String getErrorType() {
		return mErrorType;
	}
}
