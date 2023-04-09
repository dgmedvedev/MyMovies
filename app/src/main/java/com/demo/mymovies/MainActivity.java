package com.demo.mymovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.mymovies.data.Movie;
import com.demo.mymovies.utils.JSONUtils;
import com.demo.mymovies.utils.NetworkUtils;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPosters;
    private MovieAdapter movieAdapter;
    private Switch switchSort;
    private TextView textViewPopularity;
    private TextView textViewTopRated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, 2));
        movieAdapter = new MovieAdapter();
        recyclerViewPosters.setAdapter(movieAdapter);

        textViewPopularity = findViewById(R.id.textViewPopularity);
        textViewTopRated = findViewById(R.id.textViewTopRated);

        switchSort = findViewById(R.id.switchSort);
        switchSort.setChecked(true);
        switchSort.setOnCheckedChangeListener((compoundButton, isChecked) -> setMethodOfSort(isChecked));
        switchSort.setChecked(false);

        movieAdapter.setOnPosterClickListener((position -> {
            Toast.makeText(this, "Позиция " + position, Toast.LENGTH_SHORT).show();
        }));

        movieAdapter.setOnReachEndListener(() -> {
            Toast.makeText(this, "Конец списка", Toast.LENGTH_SHORT).show();
        });

        textViewPopularity.setOnClickListener((view) -> {
            setMethodOfSort(false);
            switchSort.setChecked(false);
        });
        textViewTopRated.setOnClickListener((view) -> {
            setMethodOfSort(true);
            switchSort.setChecked(true);
        });
    }

    private void setMethodOfSort(boolean isTopRated) {
        int methodOfSort;
        if (isTopRated) {
            textViewTopRated.setTextColor(getResources().getColor(R.color.colorAccent));
            textViewPopularity.setTextColor(getResources().getColor(R.color.white));
            methodOfSort = NetworkUtils.TOP_RATED;
        } else {
            methodOfSort = NetworkUtils.POPULARITY;
            textViewPopularity.setTextColor(getResources().getColor(R.color.colorAccent));
            textViewTopRated.setTextColor(getResources().getColor(R.color.white));
        }
        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(methodOfSort, 1);
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
        movieAdapter.setMovies(movies);
    }
}