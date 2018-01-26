package com.test.moviesdb.api;

import com.test.moviesdb.model.MoviesResponseList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Farhan on 1/22/2018.
 */

/*
 * API Interface for API calls using Retrofit
 */
public interface ApiInterface {

    @GET("/3/search/movie")
    Call<MoviesResponseList> getRecipes(@Query("api_key") String key, @Query("query") String searchString, @Query("page") int pageNumber);

}
