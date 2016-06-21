package com.svs.hztb.RestService;

import com.svs.hztb.Bean.GroupDetail;
import com.svs.hztb.Bean.OpinionData;
import com.svs.hztb.Bean.RefreshInput;
import com.svs.hztb.Bean.RefreshOutput;
import com.svs.hztb.Bean.RequestOpinionInput;
import com.svs.hztb.Bean.RequestOpinionOutput;
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




    public Observable<Response<List<OpinionData>> > getOpinions(RefreshInput opinionsInput) {
        return dataService.requestToGetOpinions(opinionsInput);
    }

    public Observable<Response<List<GroupDetail>> > getGroups(UserID userID) {
        return dataService.requestToGetOpinions(userID);
    }


}
