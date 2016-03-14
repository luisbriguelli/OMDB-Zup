package com.omdbexample.service;

import com.omdbexample.model.Movie;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Luis Fernando Briguelli da Silva on 13/03/2016.
 */
public interface ApiService {

    @GET("?plot=full&r=json")
    Call<Movie> getMovie(@Query("t") String title);

    @GET("{url}")
    Call<ResponseBody> getPoster(@Path("url") String url);

}
