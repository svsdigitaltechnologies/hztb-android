package com.svs.hztb.RestService;

import com.svs.hztb.Bean.RegisterRequest;
import com.svs.hztb.Bean.RegisterResponse;
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


    public Observable<Response<ValidateOTPResponse>> validate(String mobileNumber,String otpCode,String imei,String deviceID) {
        ValidateOTPRequest validateOTPRequest = new ValidateOTPRequest();
        validateOTPRequest.setMobileNumber(mobileNumber);
        validateOTPRequest.setOtpCode(otpCode);
        validateOTPRequest.setImei(imei);
        validateOTPRequest.setDeviceRegId(deviceID);
        return dataService.validateOTPResponse(validateOTPRequest);
    }


    public Observable<Response<UserProfileResponse>> updateUserProfile(String mobileNumber,String name,String emailID) {
        UserProfileRequest userProfileRequest = new UserProfileRequest();
        userProfileRequest.setMobileNumber(mobileNumber);
        userProfileRequest.setName(name);
        userProfileRequest.setEmailAddress(emailID);
        return dataService.updateUserProfile(userProfileRequest);
    }

}