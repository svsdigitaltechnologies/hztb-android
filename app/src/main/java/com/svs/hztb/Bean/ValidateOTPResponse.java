package com.svs.hztb.Bean;

public class ValidateOTPResponse {

	private String mobileNumber;
	private Boolean isValidateOTPSuccesful = false;
	private String userId;


	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Boolean getIsValidateOTPSuccesful() {
		return isValidateOTPSuccesful;
	}

	public void setIsValidateOTPSuccesful(Boolean isValidateOTPSuccesful) {
		this.isValidateOTPSuccesful = isValidateOTPSuccesful;
	}

}