package com.demo.mymovies.data;

import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "favourite_movies")
public class FavouriteMovie extends Movie {
    public FavouriteMovie(int uniqueId, int id, int voteCount, double voteAverage, String title, String originalTitle, String overview, String posterPath, String bigPosterPath, String backdropPath, String releaseDate) {
        super(uniqueId, id, voteCount, voteAverage, title, originalTitle, overview, posterPath, bigPosterPath, backdropPath, releaseDate);
    }

    @Ignore
    public FavouriteMovie(Movie movie) {
        super(movie.getUniqueId(), movie.getId(), movie.getVoteCount(), movie.getVoteAverage(), movie.getTitle(), movie.getOriginalTitle(),
                movie.getOverview(), movie.getPosterPath(), movie.getBigPosterPath(), movie.getBackdropPath(), movie.getReleaseDate());
    }
}