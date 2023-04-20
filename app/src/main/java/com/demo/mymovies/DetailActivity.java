package com.demo.mymovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.mymovies.adapters.ReviewAdapter;
import com.demo.mymovies.adapters.TrailerAdapter;
import com.demo.mymovies.data.FavouriteMovie;
import com.demo.mymovies.data.MainViewModel;
import com.demo.mymovies.data.Movie;
import com.demo.mymovies.data.Review;
import com.demo.mymovies.data.Trailer;
import com.demo.mymovies.utils.JSONUtils;
import com.demo.mymovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageViewBigPoster;
    private ImageView imageViewAddToFavourite;

    private RecyclerView recyclerViewTrailers;
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;

    private TextView textViewTitle;
    private TextView textViewOriginalTitle;
    private TextView textViewRating;
    private TextView textViewReleaseDate;
    private TextView textViewOverview;

    private Toast toastMessage;

    private int id;
    Movie movie;
    FavouriteMovie favouriteMovie;

    private MainViewModel viewModel;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.itemFavourite:
                Intent intentToFavourite = new Intent(this, FavouriteActivity.class);
                startActivity(intentToFavourite);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        imageViewAddToFavourite = findViewById(R.id.imageViewAddToFavourite);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);
        JSONObject jsonObjectTrailers;
        JSONObject jsonObjectReviews;
        ArrayList<Trailer> trailers = new ArrayList<>();
        ArrayList<Review> reviews = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1);
        } else {
            finish();
        }
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        movie = viewModel.getMovieById(id);
        setFavourite();

        if (movie != null) {
            downloadContent(movie);
            jsonObjectTrailers = NetworkUtils.getJSONForVideos(movie.getId());
            jsonObjectReviews = NetworkUtils.getJSONForReviews(movie.getId());
            trailers = JSONUtils.getTrailersFromJSON(jsonObjectTrailers);
            reviews = JSONUtils.getReviewsFromJSON(jsonObjectReviews);
        }
        if (favouriteMovie != null) {
            downloadContent(favouriteMovie);
            jsonObjectTrailers = NetworkUtils.getJSONForVideos(favouriteMovie.getId());
            jsonObjectReviews = NetworkUtils.getJSONForReviews(favouriteMovie.getId());
            trailers = JSONUtils.getTrailersFromJSON(jsonObjectTrailers);
            reviews = JSONUtils.getReviewsFromJSON(jsonObjectReviews);
        }

        imageViewAddToFavourite.setOnClickListener((view -> {
            if (toastMessage != null) {
                toastMessage.cancel();
            }
            if (favouriteMovie == null) {
                viewModel.insertFavouriteMovie(new FavouriteMovie(movie));
                toastMessage = Toast.makeText(this, R.string.add_to_favourite, Toast.LENGTH_SHORT);
                toastMessage.show();
            } else {
                viewModel.deleteFavouriteMovie(favouriteMovie);
                toastMessage = Toast.makeText(this, R.string.remove_from_favourite, Toast.LENGTH_SHORT);
                toastMessage.show();
            }
            try {
                // Усыпляю поток, т.к. метод setFavourite() очень быстро отрабатывает,
                // что приводит к ошибке. Позже разобраться
                Thread.sleep(100);
                setFavourite();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

        recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        trailerAdapter = new TrailerAdapter();
        reviewAdapter = new ReviewAdapter();
        trailerAdapter.setOnTrailerClickListener(url -> {
            Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intentToTrailer);
        });
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setAdapter(trailerAdapter);
        recyclerViewReviews.setAdapter(reviewAdapter);
        trailerAdapter.setTrailers(trailers);
        reviewAdapter.setReviews(reviews);
    }

    private void setFavourite() {
        favouriteMovie = viewModel.getFavouriteMovieById(id);
        if (favouriteMovie == null) {
            imageViewAddToFavourite.setImageResource(R.drawable.favourite_add_to);
        } else {
            imageViewAddToFavourite.setImageResource(R.drawable.favourite_remove);
        }
    }

    private void downloadContent(Movie movie) {
        Picasso.get().load(movie.getBigPosterPath()).into(imageViewBigPoster);
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewOverview.setText(movie.getOverview());
        textViewReleaseDate.setText(movie.getReleaseDate());
        textViewRating.setText(Double.toString(movie.getVoteAverage()));
    }
}