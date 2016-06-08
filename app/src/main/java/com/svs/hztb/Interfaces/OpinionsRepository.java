package com.svs.hztb.Interfaces;

import com.svs.hztb.Bean.OpinionData;
import com.svs.hztb.Bean.RefreshInput;
import com.svs.hztb.Bean.RefreshOutput;
import com.svs.hztb.Bean.RequestOpinionInput;
import com.svs.hztb.Bean.RequestOpinionOutput;
import com.svs.hztb.Bean.UserProfileRequest;
import com.svs.hztb.Bean.UserProfileResponse;

import java.util.List;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by VenuNalla on 6/9/16.
 */
public interface OpinionsRepository {



    @POST("/opinion/requestOpinion")
    Observable<Response<RequestOpinionOutput>> requestOpinionForNewProduct(@Body RequestOpinionInput requestOpinionInput);


    @POST("/refresh/opinions")
    Observable<Response<List<OpinionData>>> requestToGetOpinions(@Body RefreshInput refreshInput);
}
