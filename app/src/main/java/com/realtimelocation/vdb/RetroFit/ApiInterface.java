package com.realtimelocation.vdb.RetroFit;


import com.realtimelocation.vdb.JakesEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiInterface {


    @GET
    Call<List<JakesEntity>> getJakesData(@Url String url);



}