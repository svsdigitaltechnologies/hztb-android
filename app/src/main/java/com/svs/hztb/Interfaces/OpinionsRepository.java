package com.svs.hztb.Interfaces;

import com.svs.hztb.Bean.GroupDetail;
import com.svs.hztb.Bean.OpinionCountData;
import com.svs.hztb.Bean.OpinionData;
import com.svs.hztb.Bean.OpinionResponseInfo;
import com.svs.hztb.Bean.OpinionResponseInput;
import com.svs.hztb.Bean.OpinionResponseOutput;
import com.svs.hztb.Bean.RefreshInput;
import com.svs.hztb.Bean.RefreshOutput;
import com.svs.hztb.Bean.RequestOpinionInput;
import com.svs.hztb.Bean.RequestOpinionOutput;
import com.svs.hztb.Bean.ResponseGivenPendingInfo;
import com.svs.hztb.Bean.UserID;
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


    @POST("/refresh/opinionResponses")
    Observable<Response<OpinionResponseInfo>> requestToGetOpinionsDetail(@Body RefreshInput refreshInput);



    @POST("/refresh/givenPendingInfo")
    Observable<Response<ResponseGivenPendingInfo>> requestToOpinionGivenDetail(@Body RefreshInput refreshInput);


    @POST("/refresh/allResponsesCounts")
    Observable<Response<List<OpinionCountData>>> requestGivenOpinion(@Body RefreshInput refreshInput);


    @POST("/opinion/opinionResponse")
    Observable<Response<OpinionResponseOutput>> sendOpinionOfProduct(@Body OpinionResponseInput refreshInput);


    @POST("/refresh/opinions")
    Observable<Response<List<OpinionData>>> requestToGetOpinions(@Body RefreshInput refreshInput);

    @POST("/group/listGroups")
    Observable<Response<List<GroupDetail>>> requestToGetOpinions(@Body UserID userID);

    @POST("/user/registeredUsers")
    Observable<Response<List<GroupDetail>>> requestToGetRegisteredContacts(@Body UserID userID);

    @POST("/group/createGroup")
    Observable<Response<RequestOpinionOutput>> requestForNewGroup(@Body GroupDetail groupDetail);

}
