package com.svs.hztb.Bean;

import java.util.ArrayList;
import java.util.List;

public class ResponseGivenPendingInfo {
	private List<GivenPendingData> givenData = new ArrayList<GivenPendingData>();
	private List<GivenPendingData> pendingData = new ArrayList<GivenPendingData>();
	public List<GivenPendingData> getGivenData() {
		return givenData;
	}
	public void setGivenData(List<GivenPendingData> givenData) {
		this.givenData = givenData;
	}
	public List<GivenPendingData> getPendingData() {
		return pendingData;
	}
	public void setPendingData(List<GivenPendingData> pendingData) {
		this.pendingData = pendingData;
	}
	
	
}