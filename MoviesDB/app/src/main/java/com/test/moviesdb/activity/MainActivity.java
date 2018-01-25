package com.test.moviesdb.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.test.moviesdb.R;
import com.test.moviesdb.adapter.MoviesAdapter;
import com.test.moviesdb.api.ApiClient;
import com.test.moviesdb.api.ApiInterface;
import com.test.moviesdb.api.CallbacksManager;
import com.test.moviesdb.database.DatabaseHandler;
import com.test.moviesdb.listener.PaginationScrollListener;
import com.test.moviesdb.model.MoviesResponseList;
import com.test.moviesdb.utils.Constant;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Farhan on 1/22/2018.
 */

/**
 * MainActivity class which is the entry point to the project which
 * contains main screen to search movies and display them in a list
 * based on search query
 */
public class MainActivity extends BaseActivity implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener, SearchView.OnFocusChangeListener{
    //TAG to display and track logs
    private static final String TAG = MainActivity.class.getSimpleName();
    //Page start value to load the first 
    private static final int PAGE_START = 1;
    private static final String MOVIE_TEXT = "movie_text";

    private Context mContext;
    MoviesResponseList moviesResponseList=null;
    private CallbacksManager mCallbacksManager = new CallbacksManager();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager layoutManager;
    private MoviesAdapter mMoviesAdapter;
    private ProgressBar mProgressBar;
    private SearchView mSearchView;
    private LinearLayout rootView;
    DatabaseHandler databaseHandler;
    private boolean isLoading = false;
    private List<String> suggestionsList;

    private SimpleCursorAdapter mAdapter;

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
        rootView=(LinearLayout)findViewById(R.id.rootView);
        rootView.requestFocus();
        final String[] from = new String[] {MOVIE_TEXT};
        final int[] to = new int[] {android.R.id.text1};
        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    protected void initValues() {
        mMoviesAdapter = new MoviesAdapter();
        layoutManager = new LinearLayoutManager(this);
        mContext=this;
        databaseHandler = new DatabaseHandler(this);
        suggestionsList=databaseHandler.getLastt10Suggestions();
    }

    @Override
    protected void initValuesInViews() {
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mMoviesAdapter);


        int autoCompleteTextViewID = getResources().getIdentifier("android:id/search_src_text", null, null);
        AutoCompleteTextView searchAutoCompleteTextView = (AutoCompleteTextView) mSearchView.findViewById(autoCompleteTextViewID);
        searchAutoCompleteTextView.setThreshold(0);

        mSearchView.setSuggestionsAdapter(mAdapter);

    }

    @Override
    protected void setListenersOnViews() {

        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnSuggestionListener(this);
        mSearchView.setOnQueryTextFocusChangeListener(this);

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
        mMoviesAdapter.clear();
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
        populateAdapter(newText);
        return false;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        Log.i(TAG,"Suggestion Selected");
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {

        Log.i(TAG,"Search term selected");
        mSearchView.setQuery(suggestionsList.get(position), true);
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus)
        {
            Log.i(TAG,"Got Focus");
            populateAdapter("");
        }
    }

    private void getMovies(int pageNumber, final boolean firstRunCheck) {
        if (firstRunCheck) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        CallbacksManager.CancelableCallback moviesRequest = mCallbacksManager.new CancelableCallback(null) {
            @Override
            protected void response(Response response, View mRecycleView) {
                moviesResponseList = (MoviesResponseList) response.body();

                if(firstRunCheck)
                {
                    if(moviesResponseList.getTotalResults()>0) {
                        databaseHandler.addSuggestion(searchString);
                        suggestionsList = databaseHandler.getLastt10Suggestions();
                    }
                    else
                    {
                        showAlert("No movies found with this search query...");
                    }
                }


                //adding recipes to the list
                mMoviesAdapter.addAll(moviesResponseList.getMovieList());
                mProgressBar.setVisibility(View.GONE);
                isLoading=false;
            }

            @Override
            protected void failure(Response response, Throwable error) {
                showAlert("There seems to be a problem with your internet, please check and try again.");
                mProgressBar.setVisibility(View.GONE);
            }
        };


        //Adding network call
        ApiInterface apiService = ApiClient.getApiService();
        Call<MoviesResponseList> log = apiService.getRecipes(Constant.API_KEY,searchString,pageNumber);
        log.enqueue(moviesRequest);
    }

    // You must implements your logic to get data using OrmLite
    private void populateAdapter(String query) {

        final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, MOVIE_TEXT });
        for (int i=0; i<suggestionsList.size(); i++) {
            if (suggestionsList.get(i).toLowerCase().startsWith(query.toLowerCase())) {
                c.addRow(new Object[]{i, suggestionsList.get(i)});
            }
        }
        mAdapter.changeCursor(c);
    }

    /* To display some alert messages */
    private void showAlert(String s)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(s);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
