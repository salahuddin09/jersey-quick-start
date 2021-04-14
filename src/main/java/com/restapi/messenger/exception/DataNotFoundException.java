package com.restapi.messenger.exception;


// https://stackoverflow.com/questions/33386225/jersey-exceptionmapper-not-being-invoked

public class DataNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6328286661536343936L;

	
	public DataNotFoundException(String message) {
		super(message);
	}
	
}