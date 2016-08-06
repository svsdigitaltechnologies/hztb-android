package com.svs.hztb.Bean;

import java.util.ArrayList;
import java.util.List;


public class GroupOutput extends BasicOutput {
	private Status status;
	private ErrorOutput errorOutput;
	private List<GroupDetail> groupDetailList = new ArrayList<>();

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public ErrorOutput getErrorOutput() {
		return errorOutput;
	}

	public void setErrorOutput(ErrorOutput errorOutput) {
		this.errorOutput = errorOutput;
	}

	public List<GroupDetail> getGroupDetailList() {
		return groupDetailList;
	}

	public void setGroupDetailList(List<GroupDetail> groupDetailList) {

		this.groupDetailList = groupDetailList;
	}
}