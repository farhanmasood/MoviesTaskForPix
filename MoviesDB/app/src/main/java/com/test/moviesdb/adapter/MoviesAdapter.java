package com.test.moviesdb.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.test.moviesdb.R;
import com.test.moviesdb.activity.MovieDetails;
import com.test.moviesdb.model.Movie;
import com.test.moviesdb.utils.Constant;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Farhan on 1/22/2018.
 */

// Adapter to implement functions for Recyclerview Adapter
public class MoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Movie> movies;
    private static Context mContext;

    public MoviesAdapter() {
        movies = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(R.layout.movie_item, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies == null ? 0 : movies.size();
    }

    /**
     * Helpers functions for adapter
     */

    public void add(Movie movie) {
        movies.add(movie);
        notifyItemInserted(movies.size() - 1);
    }

    public void addAll(List<Movie> mcList) {
        for (Movie mc : mcList) {
            add(mc);
        }
    }

    public void remove(Movie movie) {
        int position = movies.indexOf(movie);
        if (position > -1) {
            movies.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }
    public boolean isEmpty() {
        return getItemCount() == 0;
    }
    public Movie getItem(int position) {
        return movies.get(position);
    }

    //Viewholder class to handle the view in the list and to map the movie information to that view
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView movieTitle;
        private TextView voteAvergae;
        private ImageView moviePoster;
        private RelativeLayout itemLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            movieTitle = itemView.findViewById(R.id.movie_title);
            voteAvergae = itemView.findViewById((R.id.vote_average));
            moviePoster = itemView.findViewById(R.id.movie_poster);
            itemLayout = itemView.findViewById(R.id.item_layout);
        }

        public void bind(final Movie movie) {
            movieTitle.setText(movie.getMovieTitle());
            voteAvergae.setText(""+movie.getVoteAverage());
            if(movie.getMoviePoster()!=null)
            {
                String image_path= Constant.IMAGE_PATH_W92+movie.getMoviePoster();
                Picasso.with(mContext).load(image_path).into(moviePoster);
            }

            //handling on click event for a specific movie in the list
            itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, MovieDetails.class);
                    //passing current movie object as serialized object
                    intent.putExtra(Constant.MOVIE_OBJECT,movie);
                    mContext.startActivity(intent);
                    Activity activity=(Activity)mContext;
                    activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }
            });
        }
    }
}

