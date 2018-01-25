package com.test.moviesdb.activity;

import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.test.moviesdb.R;
import com.test.moviesdb.model.Movie;
import com.test.moviesdb.utils.Constant;

public class MovieDetails extends BaseActivity {

    private static final String TAG = MovieDetails.class.getSimpleName();
    Movie mMovie;
    TextView movieTitle;
    TextView movieVoteCount;
    TextView movieVoteAverage;
    TextView movieOverview;
    ImageView moviePoster;

    @Override
    protected int setActivityLayout() {
        return R.layout.activity_movie_details;
    }

    @Override
    protected void initViews() {
        movieTitle=(TextView) findViewById(R.id.movie_title_text);
        moviePoster=(ImageView) findViewById(R.id.movie_poster_big);
        movieVoteAverage=(TextView) findViewById(R.id.vote_average_text);
        movieVoteCount=(TextView) findViewById(R.id.vote_count_text);
        movieOverview=(TextView) findViewById(R.id.movie_overview_text);
    }

    @Override
    protected void initValues() {
        Intent intent=getIntent();
        mMovie=(Movie)intent.getSerializableExtra(Constant.MOVIE_OBJECT);
        Log.i(TAG,mMovie.getMovieTitle());
    }

    @Override
    protected void initValuesInViews() {
        movieTitle.setText(mMovie.getMovieTitle());
        movieVoteAverage.setText(mMovie.getVoteAverage()+"");
        movieVoteCount.setText(mMovie.getVoteCount()+"");
        movieOverview.setText(mMovie.getOverview());
        if(mMovie.getMoviePoster()!=null)
        {
            String image_path= Constant.IMAGE_PATH_W500+mMovie.getMoviePoster();
            Picasso.with(this).load(image_path).placeholder(R.drawable.default_image).into(moviePoster);
        }

    }

    @Override
    protected void setListenersOnViews() {

    }
}
