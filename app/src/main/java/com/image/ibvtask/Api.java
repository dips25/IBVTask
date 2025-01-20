package com.image.ibvtask;

import com.image.ibvtask.Models.LoginResponse;
import com.image.ibvtask.Models.PricesData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface Api {

    @POST(ApiConstants.LOGIN)
    Call<LoginResponse> login(@Body Map<String,String> body);

    @GET(ApiConstants.GET_DATA_URL)
    Call<ArrayList<PricesData>> getPricesData(@HeaderMap Map<String,String> headerMap);
}
