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

    //Page start value to get the first page in search query from movies API
    private static final int PAGE_START = 1;

    //Movie text constant to use as column name in SimpleCursor Adapter
    private static final String MOVIE_TEXT = "movie_text";

    //To store the context of this activity
    private Context mContext;

    //MoviesResponseList model object to store the response from API query
    MoviesResponseList moviesResponseList=null;

    //Callbacksmanager object to hanlde all callbacks from API queries
    private CallbacksManager mCallbacksManager = new CallbacksManager();

    //Variables to be used for mapping from xml to java
    private RecyclerView mRecyclerView;
    private LinearLayoutManager layoutManager;
    private ProgressBar mProgressBar;
    private SearchView mSearchView;
    private LinearLayout rootView;
    //*

    //Movies adapted to handle movies list
    private MoviesAdapter mMoviesAdapter;

    //databasehandler object to store successful suggestions
    DatabaseHandler databaseHandler;

    //isLoading is used to check if the API call is completed or still in place
    private boolean isLoading = false;

    //String list to store last 10 suggestions
    private List<String> suggestionsList;

    //Simple cursor adapter to be used for suggestions handling
    private SimpleCursorAdapter mAdapter;

    //To store the main search query
    private String searchString;

    //Setting up mainactivities xml file to the main view
    @Override
    protected int setActivityLayout() {
        return R.layout.activity_main;
    }

    //Binding java variables to xml views
    @Override
    protected void initViews() {
        mProgressBar = findViewById(R.id.progress_bar);
        mRecyclerView = findViewById(R.id.facts_recycler_view);
        mSearchView = findViewById(R.id.search_view);
        rootView=findViewById(R.id.rootView);
        rootView.requestFocus();
    }

    //Initializing local variables
    @Override
    protected void initValues() {
        mContext=this;
        mMoviesAdapter = new MoviesAdapter();
        layoutManager = new LinearLayoutManager(this);
        databaseHandler = new DatabaseHandler(this);
        suggestionsList=databaseHandler.getLastt10Suggestions();
    }

    //Setting up values in variables which are bounded to view
    @Override
    protected void initValuesInViews() {
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mMoviesAdapter);


        setupSuggestionsAdapter(0);

    }

    /*
    * This function's first sets up the minimum threshold value for searchview to show suggestions as a list
    * For now we are setting up threshold to 0 because we want to display suggestions list even if user haven't
    * typed a single character.
    * The second step is to create a cursor adapter for suggestions list which will be displayed with searchview
    * Then finally setting up cursor adapter to searchView
    */
    private void setupSuggestionsAdapter(int threshold)
    {
        //Setting up threshold
        int autoCompleteTextViewID = getResources().getIdentifier("android:id/search_src_text", null, null);
        AutoCompleteTextView searchAutoCompleteTextView = (AutoCompleteTextView) mSearchView.findViewById(autoCompleteTextViewID);
        searchAutoCompleteTextView.setThreshold(threshold);

        //Creating cursor adapter
        final String[] from = new String[] {MOVIE_TEXT};
        final int[] to = new int[] {android.R.id.text1};
        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        //assigning cursor adapter for suggestions to searchview
        mSearchView.setSuggestionsAdapter(mAdapter);
    }

    //If some views required to implement their listeners this functions sets up listeners for them
    @Override
    protected void setListenersOnViews() {

        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnSuggestionListener(this);
        mSearchView.setOnQueryTextFocusChangeListener(this);

        //Scroll listener to check if there is a need to load next page and then to load the next page
        mRecyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {

            //This functions loads more items if
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
