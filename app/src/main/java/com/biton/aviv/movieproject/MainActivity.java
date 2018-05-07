package com.biton.aviv.movieproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.OnItemClickedListener, AdapterView.OnItemSelectedListener, AsyncCallback {


    public final static short REQUEST_CODE_PANEL_MOVIE = 0;

    Database db;

    MoviesAdapter adapter;
    ListView listViewMovies;
    ArrayList<Movie> movies;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE_PANEL_MOVIE || resultCode != RESULT_OK) return;
        Movie movie = (Movie) data.getSerializableExtra("MOVIE_DATA");
        if (movie.getID() != -1) {
            // When the ID is not -1. it means this movie already exist in the database and we are updating an already existing movie
            movies.set(getMoviePosition(movie.getID()), movie);
            db.UpdateMovie(movie);
        } else {
            movies.add(movie);
            db.AddMovie(movie);
        }
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        db = new Database();
        movies = db.getAllMovies();
        adapter = new MoviesAdapter(this, movies, this);
        adapter.setClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OnEditClicked(i);
            }
        });
        listViewMovies = findViewById(R.id.listViewMovies);
        listViewMovies.setAdapter(adapter);

        registerForContextMenu(listViewMovies);

        Spinner spinner = findViewById(R.id.sortby_Spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.sortby_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_view_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.listViewMenu_edit:
                OnEditClicked(info.position);
                return true;
            case R.id.listViewMenu_delete:
                OnDeleteClicked(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    public void OnClearMoviesClicked(MenuItem item) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.drawable.delete_icon)
                .setTitle("Clear Database")
                .setMessage("Do you want to wipe out your entire movie collection?\nThis is can not be undone")
                .setCancelable(false)
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.ClearDatabase();
                        movies.clear();
                        adapter.notifyDataSetChanged();

                    }
                }).create();
        dialog.show();
    }

    public void OnAddNewMovieClicked(MenuItem item) {
        Intent intent = new Intent(this, MoviePanelActivity.class);
        intent.putExtra("PANEL_MODE", MoviePanelActivity.PanelMode.Create);
        startActivityForResult(intent, REQUEST_CODE_PANEL_MOVIE);
    }


    public void OnAddFromInternetClicked(MenuItem item) {
        Intent intent = new Intent(this, SearchFromInternetActivity.class);
        startActivityForResult(intent, REQUEST_CODE_PANEL_MOVIE);
    }

    @Override
    public void OnEditClicked(int position) {
        Movie movie = (Movie) listViewMovies.getItemAtPosition(position);
        Intent intent = new Intent(this, MoviePanelActivity.class);
        intent.putExtra("PANEL_MODE", MoviePanelActivity.PanelMode.Edit);
        intent.putExtra("EDIT_MOVIE", movie);
        startActivityForResult(intent, REQUEST_CODE_PANEL_MOVIE);
    }

    @Override
    public void OnDeleteClicked(final int position) {
        Movie m = (Movie) listViewMovies.getItemAtPosition(position);
        SpannableStringBuilder text = new SpannableStringBuilder("Delete " + m.getName() + " from your collection?");
        text.setSpan(new StyleSpan(Typeface.BOLD), 7, 7 + m.getName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.drawable.delete_icon)
                .setTitle("Delete Movie")
                .setMessage(text)
                .setCancelable(false)
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.DeleteMovie(movies.get(position));
                        movies.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public void OnShareClicked(int position) {

        Movie movie = (Movie) listViewMovies.getItemAtPosition(position);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey, check out this movie.\n" + movie.getName() + "\nRating: " + movie.getRating());
        sendIntent.setType("image/*");
        Uri uri = Uri.parse(movie.getImageURL());
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(sendIntent, "Send to"));

    }


    private int getMoviePosition(int movieid) {
        int count = 0;
        for (Movie m : movies) {
            if (m.getID() == movieid)
                return count;
            count++;
        }
        return -1;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String sort = (String) adapterView.getItemAtPosition(i);
        if (sort.equals(getString(R.string.sort_by_rating))) {
            SortMoviesByRating();
        } else if (sort.equals(getString(R.string.sort_by_date))) {
            SortMoviesById();
        } else if (sort.equals(getString(R.string.sort_by_name))) {
            SortMoviesByName();
        } else if (sort.equals(getString(R.string.sort_by_watched))) {
            SortMoviesByWatched(true);
        } else if (sort.equals(getString(R.string.sort_by_unwatched))) {
            SortMoviesByWatched(false);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private void SortMoviesById() {
        int i = 0, n = movies.size();
        boolean swapNeeded = true;
        while (i < n - 1 && swapNeeded) {
            swapNeeded = false;
            for (int j = 1; j < n - i; j++) {
                if (movies.get(j - 1).getID() < movies.get(j).getID()) {
                    Movie temp = movies.get(j - 1);
                    movies.set(j - 1, movies.get(j));
                    movies.set(j, temp);
                    swapNeeded = true;
                }
            }
            if (!swapNeeded) {
                break;
            }
            i++;
        }
    }

    private void SortMoviesByRating() {
        int i = 0, n = movies.size();
        boolean swapNeeded = true;
        while (i < n - 1 && swapNeeded) {
            swapNeeded = false;
            for (int j = 1; j < n - i; j++) {
                if (movies.get(j - 1).getRating() < movies.get(j).getRating()) {
                    Movie temp = movies.get(j - 1);
                    movies.set(j - 1, movies.get(j));
                    movies.set(j, temp);
                    swapNeeded = true;
                }
            }
            if (!swapNeeded) {
                break;
            }
            i++;
        }

    }
    private void SortMoviesByName() {
        int i = 0, n = movies.size();
        boolean swapNeeded = true;
        while (i < n - 1 && swapNeeded) {
            swapNeeded = false;
            for (int j = 1; j < n - i; j++) {
                if (movies.get(j - 1).getName().compareTo(movies.get(j).getName()) > 0) {
                    Movie temp = movies.get(j - 1);
                    movies.set(j - 1, movies.get(j));
                    movies.set(j, temp);
                    swapNeeded = true;
                }
            }
            if (!swapNeeded) {
                break;
            }
            i++;
        }
    }

    // put watches movies first
    private void SortMoviesByWatched(boolean ascending) {
        int i = 0, n = movies.size();
        boolean swapNeeded = true;
        while (i < n - 1 && swapNeeded) {
            swapNeeded = false;
            for (int j = 1; j < n - i; j++) {
                //  change sorting order by ascending
                if (movies.get(j - 1).isWatched() == !ascending && movies.get(j).isWatched() == ascending) {
                    Movie temp = movies.get(j - 1);
                    movies.set(j - 1, movies.get(j));
                    movies.set(j, temp);
                    swapNeeded = true;
                }
            }
            if (!swapNeeded) {
                break;
            }
            i++;
        }
    }

    @Override
    public void OnFinishExecuting(String result) {

    }

    @Override
    public void OnPreExecute() {

    }

    @Override
    public void OnFailedExecuting() {

    }
}
