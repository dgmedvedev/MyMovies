package com.demo.mymovies.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainViewModel extends AndroidViewModel {
    private static MovieDatabase database;
    private LiveData<List<Movie>> movies;
    private LiveData<List<FavouriteMovie>> favouriteMovies;
    private Movie tempMovie;

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = MovieDatabase.getInstance(getApplication());
        movies = database.movieDao().getAllMovies();
        favouriteMovies = database.movieDao().getAllFavouriteMovies();
    }

    public Movie getMovieById(int id) {
        Thread getMovieTask = new Thread(() -> {
            tempMovie = database.movieDao().getMovieById(id);
        });
        getMovieTask.start();
        try {
            getMovieTask.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return tempMovie;
    }

    public void insertMovie(Movie movie) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            database.movieDao().insertMovie(movie);
        });
    }

    public void deleteMovie(Movie movie) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            database.movieDao().deleteMovie(movie);
        });
    }

    public void insertFavouriteMovie(FavouriteMovie movie) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            database.movieDao().insertFavouriteMovie(movie);
        });
    }

    public void deleteFavouriteMovie(FavouriteMovie movie) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            database.movieDao().deleteFavouriteMovie(movie);
        });
    }

    public void deleteAllMovies() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            database.movieDao().deleteAllMovies();
        });
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public LiveData<List<FavouriteMovie>> getFavouriteMovies() {
        return favouriteMovies;
    }
}