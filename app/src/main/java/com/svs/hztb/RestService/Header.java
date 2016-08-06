package com.svs.hztb.RestService;

import java.io.Serializable;

public class Header implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 4703133893357972812L;

	private String requestId;

	public Header() {

	}

	public Header(String requestId) {
		this.requestId = requestId;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

}