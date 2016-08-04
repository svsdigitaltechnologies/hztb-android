package com.svs.hztb.Bean;

public class GivenPendingData {
	private int opinionId;
	private String responseText;
	private String responseType;
	private Product product;
	private String selfieUrl;

	public String getSelfieUrl() {
		return selfieUrl;
	}

	public void setSelfieUrl(String selfieUrl) {
		this.selfieUrl = selfieUrl;
	}
	public int getOpinionId() {
		return opinionId;
	}
	public void setOpinionId(int opinionId) {
		this.opinionId = opinionId;
	}
	public String getResponseText() {
		return responseText;
	}
	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}
	public String getResponseType() {
		return responseType;
	}
	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	
}