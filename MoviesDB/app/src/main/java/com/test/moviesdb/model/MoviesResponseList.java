package com.test.moviesdb.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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

    @SerializedName("results")
    private List<Movie> movieList;

    public MoviesResponseList() {
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<Movie> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
    }

    public boolean isLastPage()
    {
        return pageNumber==totalPages;
    }

    @Override
    public String toString() {
        return "MoviesResponseList{" +
                "pageNumber=" + pageNumber +
                ", totalResults=" + totalResults +
                ", totalPages=" + totalPages + '}';
    }
}
