package com.omdbexample.service;


import com.omdbexample.utils.JacksonMapper;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Luis Fernando Briguelli da Silva on 13/03/2016.
 */
public class RetrofitClient
{
    private static final String BASE_URL = "http://www.omdbapi.com/";

    private ApiService apiService;
    Retrofit retrofitAdapter;
    private static RetrofitClient instance;

    private RetrofitClient() {
        retrofitAdapter = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create(JacksonMapper.getInstance().getObjectMapper()))
            .client(new OkHttpClient())
            .build();
        apiService = retrofitAdapter.create(ApiService.class);
    }

    public static RetrofitClient getInstance() {
        if (instance == null)
            instance = new RetrofitClient();
        return instance;
    }

    public ApiService getApiService()
    {
        return apiService;
    }
}
