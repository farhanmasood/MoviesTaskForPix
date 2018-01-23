package com.test.moviesdb.activity;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.test.moviesdb.R;
import com.test.moviesdb.adapter.MoviesAdapter;
import com.test.moviesdb.api.ApiClient;
import com.test.moviesdb.api.ApiInterface;
import com.test.moviesdb.api.CallbacksManager;
import com.test.moviesdb.listener.PaginationScrollListener;
import com.test.moviesdb.model.MoviesResponseList;
import com.test.moviesdb.utils.Constant;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Farhan on 1/22/2018.
 */

public class MainActivity extends BaseActivity implements SearchView.OnQueryTextListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PER_PAGE_MOVIES = 30;
    private static final int PAGE_START = 1;

    private Context mContext;
    MoviesResponseList moviesResponseList=null;
    private CallbacksManager mCallbacksManager = new CallbacksManager();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager layoutManager;
    private MoviesAdapter mAdapter;
    private ProgressBar mProgressBar;
    private SearchView mSearchView;

    private boolean isLoading = false;

    private String searchString;

    @Override
    protected int setActivityLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        mProgressBar = findViewById(R.id.progress_bar);
        mRecyclerView = findViewById(R.id.facts_recycler_view);
        mSearchView = findViewById(R.id.search_view);
    }

    @Override
    protected void initValues() {
        mAdapter = new MoviesAdapter();
        layoutManager = new LinearLayoutManager(this);
        mContext=this;
    }

    @Override
    protected void initValuesInViews() {
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mSearchView.setOnQueryTextListener(this);

        //Scroll listener to check if there is a need to load next page
        mRecyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                Log.e("recyclerview", "loadMoreItems");
                isLoading = true;
                mProgressBar.setVisibility(View.VISIBLE);
                if(moviesResponseList!=null)
                    getMovies(moviesResponseList.getPageNumber()+1, false);
            }

            @Override
            public boolean isLastPage() {
                if(moviesResponseList!=null)
                {
                    return moviesResponseList.isLastPage();
                }
                return false;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mAdapter.clear();
        if(query.isEmpty() || (query.trim()).isEmpty())
        {
            return false;
        }
        searchString=query;
        isLoading = false;
        mProgressBar.setVisibility(View.VISIBLE);
        getMovies(PAGE_START, true);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void getMovies(int pageNumber, final boolean firstRunCheck) {
        if (firstRunCheck)
            mProgressBar.setVisibility(View.VISIBLE);
        CallbacksManager.CancelableCallback moviesRequest = mCallbacksManager.new CancelableCallback(null) {
            @Override
            protected void response(Response response, View mRecycleView) {
                moviesResponseList = (MoviesResponseList) response.body();

                //adding recipes to the list
                mAdapter.addAll(moviesResponseList.getMovieList());
                mProgressBar.setVisibility(View.GONE);
                isLoading=false;
            }

            @Override
            protected void failure(Response response, Throwable error) {
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_LONG).show();
            }
        };


        //Adding network call
        ApiInterface apiService = ApiClient.getApiService();
        Call<MoviesResponseList> log = apiService.getRecipes(Constant.API_KEY,searchString,pageNumber);
        log.enqueue(moviesRequest);

    }
}
