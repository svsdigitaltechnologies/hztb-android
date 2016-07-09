package com.svs.hztb.RestService;

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
import com.svs.hztb.Interfaces.OpinionsRepository;
import com.svs.hztb.Interfaces.UserRepository;

import java.util.List;

import retrofit2.Response;
import rx.Observable;

/**
 * Created by VenuNalla on 6/9/16.
 */
public class OpinionService {
    private OpinionsRepository dataService;

    public OpinionService() {
        dataService = ServiceGenerator.createService(OpinionsRepository.class);
    }


    public Observable<Response<RequestOpinionOutput>> requestOpinionForNewProduct(RequestOpinionInput requestOpinionInput) {
        return dataService.requestOpinionForNewProduct(requestOpinionInput);
    }

    public Observable<Response<OpinionResponseInfo> > getOpinionsDetail(RefreshInput opinionsInput) {
        return dataService.requestToGetOpinionsDetail(opinionsInput);
    }

    public Observable<Response<List<OpinionCountData>>> opinionGiven(RefreshInput opinionsInput) {
        return dataService.requestGivenOpinion(opinionsInput);
    }

    public Observable<Response<ResponseGivenPendingInfo> > opinionsGivenDetail(RefreshInput opinionsInput) {
        return dataService.requestToOpinionGivenDetail(opinionsInput);
    }


    public Observable<Response<List<OpinionData>> > getOpinions(RefreshInput opinionsInput) {
        return dataService.requestToGetOpinions(opinionsInput);
    }

    public Observable<Response<OpinionResponseOutput>> sendOpinionInput(OpinionResponseInput opinionsInput) {
        return dataService.sendOpinionOfProduct(opinionsInput);
    }


    public Observable<Response<List<GroupDetail>> > getGroups(UserID userID) {
        return dataService.requestToGetOpinions(userID);
    }

}
