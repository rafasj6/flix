package com.example.rafasj6.flixter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.rafasj6.flixter.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;

public class MovieListActivity extends AppCompatActivity {


    public final static String API_BASE_URL = "http://api.themoviedb.org/3";

    public final static String API_KEY_PARAM = "api_key";



    //tag for logging from this activity

    public final static String TAG = "MovieListActivity";

    AsyncHttpClient client;

    String imageBaseUrl;
    //poster size to fetch images
    String posterSize;

    ArrayList<Movie> movies;

    RecyclerView rvMovies;

    MovieAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        client = new AsyncHttpClient();

        movies = new ArrayList<>();

        adapter = new MovieAdapter(movies);

        rvMovies = (RecyclerView) findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);

        //get config

        getConfiguration();

    }

    private void getNowPlaying(){
        String url = API_BASE_URL + "/movie/now_playing";
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));

        client.get(url, params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //load results
                try {
                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++){
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);
                        //notify that adapter
                        adapter.notifyItemInserted(movies.size()-1);
                    }
                    log.i(TAG, String.format("LOADED %s movies", results.length()));
                } catch (JSONException e) {
                    logError("couldnt parse", e, true);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get now playing", throwable, true);
            }
        });

    }

    private void getConfiguration(){


        String url = API_BASE_URL + "/configuration";
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));

        client.get(url, params, new JsonHttpResponseHandler(){


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject images = response.getJSONObject("images");
                    imageBaseUrl = images.getString("secure_base_url");
                    JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");
                    posterSize = posterSizeOptions.optString(3,"w342");
                    log.i(TAG, String.format("LOADED image url: &s and poster size: %s", imageBaseUrl, posterSize));

                } catch (JSONException e) {
                    logError("Failed parsing config", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("failed getting congig", throwable, true);

            }

        });

        getNowPlaying();

    }

    private void logError(String message, Throwable error, boolean alertUser){

        //log it
        log.e(TAG,message,error);
        //aler the user
        if (alertUser){
            //show a Toast
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG ).show();
        }
    }
}
