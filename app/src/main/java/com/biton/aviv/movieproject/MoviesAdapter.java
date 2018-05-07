package com.biton.aviv.movieproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MoviesAdapter extends ArrayAdapter<Movie> {


    AdapterView.OnItemClickListener clickListener;
    OnItemClickedListener listener;
    LayoutInflater inflater;

    public MoviesAdapter(Context context, ArrayList<Movie> movies, OnItemClickedListener listener) {
        super(context, 0, movies);
        inflater = LayoutInflater.from(context);
        this.listener = listener;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.movie_layout, null);

        // TextView textViewID = relativeLayout.findViewById(R.id.textViewID);
        ImageView imageView = linearLayout.findViewById(R.id.imageViewPicture);
        TextView textViewName = linearLayout.findViewById(R.id.textViewName);
        RatingBar ratingBar = linearLayout.findViewById(R.id.ratingBarScore);
        TextView descriptionTextView = linearLayout.findViewById(R.id.textViewDescription);
        Button buttonDelete = linearLayout.findViewById(R.id.buttonDelete);
        Button buttonEdit = linearLayout.findViewById(R.id.buttonUpdate);
        Button buttonShare = linearLayout.findViewById(R.id.buttonShare);
        View watchedView = linearLayout.findViewById(R.id.watched_view);

        Movie movie = getItem(position);
        textViewName.setText("" + movie.getName());
        descriptionTextView.setText("" + movie.getDescription());

        //TODO: save images to phone and get it from the user's phone

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(convertView.getContext().getContentResolver(), Uri.parse(movie.getImageURL()));
            imageView.setImageBitmap(bitmap);
        } catch (Exception ex) {
            new ImageDownloader(imageView, null).execute(movie.getImageURL());
        }
        ratingBar.setRating(movie.getRating());


        // set watched movies to watchedColor and sets unwatched movies to planToWatch color
        watchedView.setBackgroundColor(movie.isWatched() ? MyApp.getContext().getResources().getColor(R.color.watchedColor) : MyApp.getContext().getResources().getColor(R.color.planToWatchColor));


        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnDeleteClicked(position);
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnEditClicked(position);
            }
        });

        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnShareClicked(position);
            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    clickListener.onItemClick(null, view, position, -1);
                }
            }
        });
        return linearLayout;
    }

    // sets click listener that works with custom adapter
    public void setClickListener(AdapterView.OnItemClickListener obj) {
        clickListener = obj;
    }

    public interface OnItemClickedListener {

        void OnEditClicked(int position);

        void OnDeleteClicked(int position);

        void OnShareClicked(int position);

    }
}
