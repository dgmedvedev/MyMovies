package com.demo.mymovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.demo.mymovies.data.Movie;
import com.demo.mymovies.utils.JSONUtils;
import com.demo.mymovies.utils.NetworkUtils;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPosters;
    private MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, 2));
        movieAdapter = new MovieAdapter();

        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(NetworkUtils.POPULARITY, 1);
        if (jsonObject == null) {
            Toast.makeText(this, "Проверьте интернет соединение", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "JSON загружен", Toast.LENGTH_SHORT).show();
        }
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
        if (movies.isEmpty()) {
            Toast.makeText(this, "Произошла ошибка", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "ArrayList создан", Toast.LENGTH_SHORT).show();
        }
        movieAdapter.setMovies(movies);
        recyclerViewPosters.setAdapter(movieAdapter);
    }
}