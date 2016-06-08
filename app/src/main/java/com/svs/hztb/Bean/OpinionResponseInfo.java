package com.svs.hztb.Bean;

import java.util.ArrayList;
import java.util.List;

public class OpinionResponseInfo {
	private List<OpinionResponseData> opinionResponseDataList = new ArrayList<OpinionResponseData>();
	private List<Integer> unResponsiveUsers = new ArrayList<Integer>();
	public List<OpinionResponseData> getOpinionResponseDataList() {
		return opinionResponseDataList;
	}
	public void setOpinionResponseDataList(List<OpinionResponseData> opinionResponseDataList) {
		this.opinionResponseDataList = opinionResponseDataList;
	}
	public List<Integer> getUnResponsiveUsers() {
		return unResponsiveUsers;
	}
	public void setUnResponsiveUsers(List<Integer> unResponsiveUsers) {
		this.unResponsiveUsers = unResponsiveUsers;
	}
 
}