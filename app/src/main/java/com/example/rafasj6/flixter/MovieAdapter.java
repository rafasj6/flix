package com.example.rafasj6.flixter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rafasj6.flixter.models.Config;
import com.example.rafasj6.flixter.models.Movie;

import org.parceler.Parcels;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by rafasj6 on 6/22/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    ArrayList<Movie> movies;
    Config config;
    Context context;


    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View movieView = inflater.inflate(R.layout.item_movie, parent, false);

        return new ViewHolder(movieView);


    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie = movies.get(position);

        holder.tvTitle.setText(movie.getTitle());
        holder.tvOverview.setText(movie.getOverview());

        String imageUrl;

        boolean isPortrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        if (isPortrait){
            imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());

        }

        else{
            imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
        }

        int placeholderID = isPortrait ? R.drawable.flicks_movie_placeholder : R.drawable.flicks_backdrop_placeholder;
        ImageView imageView = isPortrait ? holder.ivPosterImage : holder.ivBackdropImage;

        Glide.with(context)
                .load(imageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 30, 0))
                .placeholder(R.drawable.flicks_movie_placeholder)
                .error(R.drawable.flicks_movie_placeholder)
                .into(imageView);

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView ivPosterImage;
        TextView tvTitle;
        TextView tvOverview;
        ImageView ivBackdropImage;

        @Override
        public void onClick(View view) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the movie at the position, this won't work if the class is static
                Movie movie = movies.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                // serialize the movie using parceler, use its short name as a key
                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
                // show the activity
                context.startActivity(intent);
            }
        }

        public ViewHolder(View itemView){
            super(itemView);
            ivPosterImage = (ImageView) itemView.findViewById(R.id.ivPosterImage);
            ivBackdropImage = (ImageView) itemView.findViewById(R.id.ivBackdropImage);
            tvOverview = (TextView) itemView.findViewById(R.id.tvOverview);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);

            itemView.setOnClickListener(this);
        }
    }
}
