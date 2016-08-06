package com.svs.hztb.Bean;

import java.util.List;

public class RefreshOutput extends BasicOutput {

	private ErrorOutput errorOutput;
	private List<OpinionData> opinionDataList;
	private OpinionResponseInfo opinionResponseInfo = new OpinionResponseInfo();

	
	public ErrorOutput getErrorOutput() {
		return errorOutput;
	}

	public void setErrorOutput(ErrorOutput errorOutput) {
		this.errorOutput = errorOutput;
	}

	public List<OpinionData> getOpinionDataList() {
		return opinionDataList;
	}

	public void setOpinionDataList(List<OpinionData> opinionDataList) {
		this.opinionDataList = opinionDataList;
	}


	public OpinionResponseInfo getOpinionResponseInfo() {
		return opinionResponseInfo;
	}

	public void setOpinionResponseInfo(OpinionResponseInfo opinionResponseInfo) {
		this.opinionResponseInfo = opinionResponseInfo;
	}


	
	
	

}