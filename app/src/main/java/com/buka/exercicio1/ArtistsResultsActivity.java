package com.buka.exercicio1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.buka.exercicio1.api.LastFmApi;
import com.buka.exercicio1.models.Artist;
import com.buka.exercicio1.models.LastFmApiResponse;
import com.buka.exercicio1.models.SearchResults;

import java.io.IOException;
import java.util.ArrayList;

public class ArtistsResultsActivity extends AppCompatActivity {

    public static final String EXTRA_SEARCH_TEXT = "extra_search_text";
    private static final String API_KEY = "d38510e4d32d8fe7d8bb7e060f460be5";

    private ProgressBar loadingProgressBar;
    private LastFmApi lastFmApi;
    private ArtistsResultsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists_results);

        RecyclerView searchResultsRecyclerView = findViewById(R.id.recyclerview_searchresults);
        loadingProgressBar = findViewById(R.id.progressbar_loading);

        adapter = new ArtistsResultsAdapter();
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResultsRecyclerView.setAdapter(adapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://ws.audioscrobbler.com/2.0/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        lastFmApi = retrofit.create(LastFmApi.class);

        Intent intent = getIntent();
        String searchTerms = intent.getStringExtra(EXTRA_SEARCH_TEXT);


        // TODO: Passo #5: Instancie e execute o AsyncTask criado, passando como parâmetro os termos
        // de pesquisa

        ArtistsRequestTask artistsRequestTask = new ArtistsRequestTask();
        artistsRequestTask.execute(searchTerms);
    }

    // TODO: Passo #1: Crie uma classe que herde de AsyncTask, e que tome uma String como parâmetro e retorne uma ArrayList<Artist>
    class ArtistsRequestTask extends AsyncTask<String, Void, ArrayList<Artist>> {

        // TODO: Passo #2: Implemente o método onPreExecute(), sendo necessário exibir o ProgressBar neste
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingProgressBar.setVisibility(View.VISIBLE);
        }

        // TODO: Passo #3: Implementar o método doInBackground(), use o método searchArtist() do objecto lastFmApi
        @Override
        protected ArrayList<Artist> doInBackground(String... strings) {

            try {
                Response<LastFmApiResponse> response = lastFmApi.searchArtist(strings[0], API_KEY).execute();

                if (response.isSuccessful()) {
                    LastFmApiResponse searchResults = response.body();
                    ArrayList<Artist> artists = searchResults.getSearchResults().getArtistsFound();
                    return artists;
                } else {
                    Log.e(ArtistsRequestTask.class.getSimpleName(), response.message());
                    return null;
                }
            } catch (IOException exception) {
                Log.e(ArtistsRequestTask.class.getSimpleName(), exception.getMessage());
                return null;
            }
        }

        // TODO: Passo #4: Implementar o método onPostExecute(), o ProgressBar será ocultado novamente, e a lista de artistas será mostrada com o adapter
        @Override
        protected void onPostExecute(ArrayList<Artist> artists) {
            super.onPostExecute(artists);

            loadingProgressBar.setVisibility(View.INVISIBLE);

            if (artists != null) {
                adapter.setArtists(artists);
            } else {
                Toast.makeText(ArtistsResultsActivity.this, "Ocorreu algum erro", Toast.LENGTH_LONG).show();
            }
        }
    }


}
