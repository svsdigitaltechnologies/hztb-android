package com.svs.hztb.Bean;

public class ValidateOTPResponse {

	private String mobileNumber;
	private Boolean isValidateOTPSuccesful = false;


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