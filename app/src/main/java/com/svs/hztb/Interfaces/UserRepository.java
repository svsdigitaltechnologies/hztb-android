package com.svs.hztb.Interfaces;


import com.svs.hztb.Bean.RegisterRequest;
import com.svs.hztb.Bean.RegisterResponse;
import com.svs.hztb.Bean.RequestOpinionInput;
import com.svs.hztb.Bean.RequestOpinionOutput;
import com.svs.hztb.Bean.UserProfileRequest;
import com.svs.hztb.Bean.UserProfileResponse;
import com.svs.hztb.Bean.ValidateOTPRequest;
import com.svs.hztb.Bean.ValidateOTPResponse;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface UserRepository {


    @POST("/user/register")
    Observable<Response<RegisterResponse>> register(@Body RegisterRequest registerRequest);


    @POST("/user/validateOTP")
    Observable<Response<ValidateOTPResponse>> validateOTPResponse(@Body ValidateOTPRequest validateOTPRequest);


    @POST("/opinion/requestOpinion")
    Observable<Response<RequestOpinionOutput>> requestOpinionForNewProduct(@Body RequestOpinionInput requestOpinionInput);


    @POST("/user/updateUserProfile")
    Observable<Response<UserProfileResponse>> updateUserProfile(@Body UserProfileRequest userProfileRequest);
}