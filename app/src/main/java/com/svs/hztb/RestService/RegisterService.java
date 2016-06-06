package com.svs.hztb.RestService;

import com.svs.hztb.Bean.RegisterRequest;
import com.svs.hztb.Bean.RegisterResponse;
import com.svs.hztb.Bean.RequestOpinionInput;
import com.svs.hztb.Bean.RequestOpinionOutput;
import com.svs.hztb.Bean.UserProfileRequest;
import com.svs.hztb.Bean.UserProfileResponse;
import com.svs.hztb.Bean.ValidateOTPRequest;
import com.svs.hztb.Bean.ValidateOTPResponse;
import com.svs.hztb.Interfaces.UserRepository;

import retrofit2.Response;
import rx.Observable;

public class RegisterService {



    private UserRepository dataService;

    public RegisterService() {
        dataService = ServiceGenerator.createService(UserRepository.class);
    }

    public Observable<Response<RegisterResponse>> register(String phone) {

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setMobileNumber(phone);
        return dataService.register(registerRequest);
    }


    public Observable<Response<ValidateOTPResponse>> validate(String mobileNumber,String otpCode,String imei,String localDeviceID,String deviceID) {
        ValidateOTPRequest validateOTPRequest = new ValidateOTPRequest();
        validateOTPRequest.setMobileNumber(mobileNumber);
        validateOTPRequest.setOtpCode(otpCode);
        validateOTPRequest.setImei(imei);
        validateOTPRequest.setDeviceRegId(deviceID);
        validateOTPRequest.setLocalDeviceID(localDeviceID);
        return dataService.validateOTPResponse(validateOTPRequest);
    }

    public Observable<Response<RequestOpinionOutput>> requestOpinionForNewProduct(RequestOpinionInput requestOpinionInput) {
        return dataService.requestOpinionForNewProduct(requestOpinionInput);
    }


    public Observable<Response<UserProfileResponse>> updateUserProfile(String mobileNumber,String name,String emailID,byte[] picArray) {
        UserProfileRequest userProfileRequest = new UserProfileRequest();
        userProfileRequest.setMobileNumber(mobileNumber);
        userProfileRequest.setName(name);
        userProfileRequest.setEmailAddress(emailID);
        userProfileRequest.setProfilePic(picArray);
        return dataService.updateUserProfile(userProfileRequest);
    }

}