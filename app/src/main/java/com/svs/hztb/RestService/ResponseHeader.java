package com.svs.hztb.RestService;

import java.util.ArrayList;
import java.util.List;

public class ResponseHeader extends Header {

	/**
	 *
	 */
	private static final long serialVersionUID = 8390760684424288400L;

	private String status;
	
	private List<ErrorStatus> errors;
	
	public ResponseHeader(String requestId, String status) {
		super(requestId);
		this.status = status;
	}
	
	public ResponseHeader(String requestId, String status, List<ErrorStatus> errors) {
		this(requestId, status);
		this.errors = errors;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<ErrorStatus> getErrors() {
		if (errors == null) {
			errors = new ArrayList<>();
		}
		return errors;
	}

	public void setErrors(List<ErrorStatus> errors) {
		this.errors = errors;
	}
	
}