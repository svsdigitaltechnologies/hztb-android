package com.svs.hztb.Bean;

public class OpinionResponseInput {
	private int userId;
	private int opinionReqId;
	private String responseCode;
	private String responseTxt;
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getOpinionReqId() {
		return opinionReqId;
	}
	public void setOpinionReqId(int opinionReqId) {
		this.opinionReqId = opinionReqId;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseTxt() {
		return responseTxt;
	}
	public void setResponseTxt(String responseTxt) {
		this.responseTxt = responseTxt;
	}
	
	

}