package com.test.moviesdb.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Farhan on 1/22/2018.
 */

public class MoviesResponseList {
    @SerializedName("page")
    private int pageNumber;

    @SerializedName("total_results")
    private int totalResults;

    @SerializedName("total_pages")
    private int totalPages;
}
