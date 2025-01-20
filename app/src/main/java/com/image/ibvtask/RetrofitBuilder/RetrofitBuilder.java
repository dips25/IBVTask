package com.image.ibvtask.RetrofitBuilder;

import com.image.ibvtask.Api;
import com.image.ibvtask.ApiConstants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBuilder {

    private static Retrofit retrofit;
    private static RetrofitBuilder retrofitBuilder;

    public static RetrofitBuilder getInstance() {

        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            retrofitBuilder = new RetrofitBuilder();


        }

        return retrofitBuilder;

    }

    public Api getApi() {

        return retrofit.create(Api.class);
    }
}
