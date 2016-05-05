package com.svs.hztb.RestService;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static final String WEB_SERVICE_BASE_URL = "http://hztb-dev.us-east-1.elasticbeanstalk.com";
    private static Retrofit retrofit;
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(WEB_SERVICE_BASE_URL).
            addConverterFactory(GsonConverterFactory.create()).
            addCallAdapterFactory(RxJavaCallAdapterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("REQUEST_ID", "1")
                        .header("Accept", "application/json")
                        .header("Accept-Language", "en-US")

                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });
                retrofit = builder.
                client(httpClient.build()).
                build();
        return retrofit.create(serviceClass);
    }

    public static List<ErrorStatus> parseErrorBody(Response response) {
        List<ErrorStatus> listErrorStatuses = null;
        try {
            HztbResponse hztbResponse = (HztbResponse) retrofit.responseBodyConverter(HztbResponse.class, HztbResponse.class.getAnnotations()).
                    convert(response.errorBody());
             listErrorStatuses = hztbResponse.getHeader().getErrors();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return listErrorStatuses;
    }
}