package com.biton.aviv.movieproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchFromInternetActivity extends AppCompatActivity implements AsyncCallback {


    EditText searchEditText;
    ListView moviesListView;
    ArrayList<MovieResult> foundResult;
    ProgressDialog dialog;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode != MainActivity.REQUEST_CODE_PANEL_MOVIE || resultCode != RESULT_OK)
            return;

        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_from_internet);
        searchEditText = findViewById(R.id.search_EditText);
        moviesListView = findViewById(R.id.found_movies_ListView);
        final SearchFromInternetActivity context = this;
        moviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, MoviePanelActivity.class);
                MovieResult result = foundResult.get(i);
                Movie movie = new Movie(-1, result.getTitle(), result.getOverview(), "http://image.tmdb.org/t/p/w342/" + result.getPoster_path(), false, 1);
                intent.putExtra("EDIT_MOVIE", movie);
                intent.putExtra("PANEL_MODE", MoviePanelActivity.PanelMode.Edit);

                startActivityForResult(intent, MainActivity.REQUEST_CODE_PANEL_MOVIE);

            }
        });

    }


    public void OnSearchButtonClicked(View view) {

        HttpRequest request = new HttpRequest(HttpRequest.GET, this);
        String keyword = searchEditText.getText().toString().replace(" ", "%20");
        String query = "https://api.themoviedb.org/3/search/movie?api_key=fd54accf66ec234093ef938b10492561&query" +
                "=" + keyword + "&page=1";
        request.execute(query);

    }

    @Override
    public void OnFinishExecuting(String result) {
        try {

            JSONObject jsonObject = new JSONObject(result);
            JSONArray array = jsonObject.getJSONArray("results");
            foundResult = new ArrayList<>();
            ArrayList<String> adapterList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject currentJson = array.getJSONObject(i);
                MovieResult newResult = new MovieResult();
                newResult.setId(currentJson.getInt("id"));
                newResult.setTitle(currentJson.getString("title"));
                newResult.setOverview(currentJson.getString("overview"));
                newResult.setPoster_path(currentJson.getString("poster_path"));
                foundResult.add(newResult);
                adapterList.add(newResult.getTitle());
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, adapterList);
            moviesListView.setAdapter(arrayAdapter);
            Toast.makeText(this, "Found " + foundResult.size() + " results", Toast.LENGTH_SHORT).show();

        } catch (Exception ex) {
            Toast.makeText(this, "Failed to search for movies, please try again.", Toast.LENGTH_SHORT).show();
            Log.d("OnFinishExecuting", "Failed to parse JSON Object");

        } finally {
            dialog.dismiss();
        }
    }

    @Override
    public void OnPreExecute() {
        dialog = new ProgressDialog(this);
        dialog.setIcon(R.drawable.search_icon);
        dialog.setTitle("Searching");
        dialog.setMessage("Please wait a moment...");
        dialog.setCancelable(false);
        dialog.create();
        dialog.show();
    }

    @Override
    public void OnFailedExecuting() {
        Toast.makeText(this, "Failed to search for movies, please try again", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }
}
