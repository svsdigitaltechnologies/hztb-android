package com.svs.hztb.Bean;

import java.util.Date;
import java.util.List;


public class RequestOpinionInput {
	private int requesterUserId;
	private Product product;
	private int channelId;
	private String storeGeoCode;
	private String productUrl;

	private byte[] selfiePic;
	//Added for sample, delete this
	private Date date;
	
	//private int requestedGroupId;
	private String groupName;
	private List<Integer> requestedUserIds;
	private List<Integer> requestedGroupIds; 

	public int getRequesterUserId() {
		return requesterUserId;
	}

	public void setRequesterUserId(int requesterUserId) {
		this.requesterUserId = requesterUserId;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public String getStoreGeoCode() {
		return storeGeoCode;
	}

	public void setStoreGeoCode(String storeGeoCode) {
		this.storeGeoCode = storeGeoCode;
	}

	public byte[] getSelfiePic() {
		return selfiePic;
	}

	public void setSelfiePic(byte[] selfiePic) {
		this.selfiePic = selfiePic;
	}

	public List<Integer> getRequestedUserIds() {
		return requestedUserIds;
	}

	public void setRequestedUserIds(List<Integer> requestedUserIds) {
		this.requestedUserIds = requestedUserIds;
	}

	public String getProductUrl() {
		return productUrl;
	}

	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}

	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<Integer> getRequestedGroupIds() {
		return requestedGroupIds;
	}

	public void setRequestedGroupIds(List<Integer> requestedGroupIds) {
		this.requestedGroupIds = requestedGroupIds;
	}


	
	

}