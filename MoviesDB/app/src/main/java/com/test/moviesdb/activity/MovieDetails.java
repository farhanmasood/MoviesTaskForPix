package com.test.moviesdb.activity;

import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.test.moviesdb.R;
import com.test.moviesdb.model.Movie;
import com.test.moviesdb.utils.Constant;

/*
 * Movie details activity to show data of the selected movie
 */
public class MovieDetails extends BaseActivity {

    //TAG for debugging and logging purpose
    private static final String TAG = MovieDetails.class.getSimpleName();

    //Movie to be displayed on this screen
    Movie mMovie;

    //Variables to be mapped on views
    TextView movieTitle;
    TextView movieVoteCount;
    TextView movieVoteAverage;
    TextView movieOverview;
    ImageView moviePoster;

    @Override
    protected int setActivityLayout() {
        //To add the back navigation to home screen from top bar (action bar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return R.layout.activity_movie_details;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void initViews() {
        movieTitle=findViewById(R.id.movie_title_text);
        moviePoster = findViewById(R.id.movie_poster_big);
        movieVoteAverage = findViewById(R.id.vote_average_text);
        movieVoteCount = findViewById(R.id.vote_count_text);
        movieOverview = findViewById(R.id.movie_overview_text);
    }

    @Override
    protected void initValues() {
        //Receiving the data sent by previous activity by using intent
        Intent intent=getIntent();
        //storing the movie object from received intent to mMovie object
        mMovie=(Movie)intent.getSerializableExtra(Constant.MOVIE_OBJECT);
        Log.i(TAG,mMovie.getMovieTitle());
    }

    @Override
    protected void initValuesInViews() {
        //Initializing all values into views
        movieTitle.setText(mMovie.getMovieTitle());
        movieVoteAverage.setText(mMovie.getVoteAverage()+"");
        movieVoteCount.setText("("+mMovie.getVoteCount()+")");
        movieOverview.setText(mMovie.getOverview());
        if(mMovie.getMoviePoster()!=null)
        {
            //Displaying hight quality image with w500 to imageview
            String image_path= Constant.IMAGE_PATH_W500+mMovie.getMoviePoster();
            Picasso.with(this).load(image_path).placeholder(R.drawable.default_image).into(moviePoster);
        }

    }

    @Override
    protected void setListenersOnViews() {

    }
}
