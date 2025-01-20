package com.image.ibvtask.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.image.ibvtask.Api;
import com.image.ibvtask.Models.LoginResponse;
import com.image.ibvtask.Models.PricesData;
import com.image.ibvtask.RetrofitBuilder.RetrofitBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IBVViewModel extends ViewModel {

    private MutableLiveData<LoginResponse> mutableLoginData = new MutableLiveData<>();

    private MutableLiveData<ArrayList<PricesData>> mutablePricesData = new MutableLiveData<>();

    ArrayList<PricesData> oldList = new ArrayList<>();


    public void getLogin() {



        new Thread(new Runnable() {
            @Override
            public void run() {

                Map<String,String> map = new HashMap<>();
                map.put("username","admin");
                map.put("password","A7ge#hu&dt(wer");

                Api api = RetrofitBuilder.getInstance().getApi();
                api.login(map).enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                        if (response.isSuccessful() && response.code()==200) {

                            Log.d(IBVViewModel.class.getName(), "Success " );

                            LoginResponse loginResponse = response.body();
                            mutableLoginData.postValue(loginResponse);

                        } else {

                            Log.d(IBVViewModel.class.getName(), "Error " );

                            LoginResponse errorResponse = new LoginResponse();
                            errorResponse.setSuccess(false);
                            mutableLoginData.postValue(errorResponse);
                        }

                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable throwable) {

                        LoginResponse errorResponse = new LoginResponse();
                        errorResponse.setSuccess(false);
                        mutableLoginData.postValue(errorResponse);

                        Log.d(IBVViewModel.class.getName(), "onFailure: " + errorResponse.getMessage());

                    }
                });

            }
        }).start();
    }



    public void getData(String token) {

        //mutablePricesData.postValue(oldList);


        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {

                    Map<String, String> map = new HashMap<>();
                    map.put("Authorization", "Bearer " + token);

                    Api api = RetrofitBuilder.getInstance().getApi();
                    api.getPricesData(map).enqueue(new Callback<ArrayList<PricesData>>() {
                        @Override
                        public void onResponse(Call<ArrayList<PricesData>> call, Response<ArrayList<PricesData>> response) {

                            if (response.isSuccessful() && response.code() == 200) {

                                ArrayList<PricesData> pricesData = response.body();

                                oldList.retainAll(pricesData);

                                for (PricesData p : pricesData) {

                                    if (!oldList.contains(p)) {

                                        oldList.add(p);

                                    } else {



                                        int index = oldList.indexOf(p);
                                        PricesData p1 = oldList.get(index);

                                        if (p1.comparePrice(p.getPrice())) {

                                            p1.setPrice(p.getPrice());
                                            p1.setChange(p.getChange());
                                            oldList.set(index,p1);

                                        }


                                    }
                                }
                                mutablePricesData.postValue(oldList);



                            } else {

                                mutablePricesData.postValue(null);
                            }

                        }

                        @Override
                        public void onFailure(Call<ArrayList<PricesData>> call, Throwable throwable) {

                            mutablePricesData.postValue(null);

                        }
                    });

                }





            }
        }).start();
    }

    public LiveData<LoginResponse> getLoginLiveData() {

        return mutableLoginData;
    }

    public LiveData<ArrayList<PricesData>> getPricesLiveData() {

        return mutablePricesData;
    }



}
