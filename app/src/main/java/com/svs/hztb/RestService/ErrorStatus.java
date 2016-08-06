package com.svs.hztb.RestService;

import java.io.Serializable;

public class ErrorStatus implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -9127867295214624680L;
	
	//private static final String STATUS = "status";
	
	private final String status;
	
	private final String message;
	
	public ErrorStatus(String statusCode, String message) {
		this.message = message;
		this.status = statusCode;
	}

	public String getStatus() {
		return status;
	}
	
	public String getMessage() {
		return message;
	}
}