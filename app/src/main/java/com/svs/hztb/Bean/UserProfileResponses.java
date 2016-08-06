package com.svs.hztb.Bean;

import java.util.ArrayList;
import java.util.List;

public class UserProfileResponses {

	private List<UserProfileResponse> userProfileResponses;

	public List<UserProfileResponse> getUserProfileResponses() {
		if(userProfileResponses == null) {
			userProfileResponses = new ArrayList<UserProfileResponse>();
		}
		return userProfileResponses;
	}

	public void setUserProfileResponses(List<UserProfileResponse> userProfileResponses) {
		this.userProfileResponses = userProfileResponses;
	}
	
	public void addUserProfileResponse(UserProfileResponse userProfileResponse) {
		if(userProfileResponses == null) {
			userProfileResponses = new ArrayList<UserProfileResponse>();
		}
		userProfileResponses.add(userProfileResponse);
	}

}
