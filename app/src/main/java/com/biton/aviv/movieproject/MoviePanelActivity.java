package com.biton.aviv.movieproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;


public class MoviePanelActivity extends AppCompatActivity implements AsyncCallback {


    public enum PanelMode {
        Create,
        Edit
    }

    EditText subjectEditText;
    EditText descriptionEditText;
    EditText imageURLEditText;

    Button showImageButton;
    Button confirmButton;
    Button cancelButton;

    CheckBox watchedCheckbox;
    RatingBar starRatingBar;
    ImageView image;

    PanelMode mode;
    Movie editMovie = null;

    ProgressDialog imageProgress;

    ImageDownloader downloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_panel);
        subjectEditText = findViewById(R.id.panel_Subject_EditText);
        descriptionEditText = findViewById(R.id.panel_description_EditText);
        imageURLEditText = findViewById(R.id.panel_imageURL_EditText);

        showImageButton = findViewById(R.id.panel_show_Button);
        confirmButton = findViewById(R.id.panel_confirm_Button);
        cancelButton = findViewById(R.id.panel_cancel_Button);

        watchedCheckbox = findViewById(R.id.panel_watched_Checkbox);
        starRatingBar = findViewById(R.id.panel_star_RatingBar);

        image = findViewById(R.id.panel_image_view);

        Intent intent = getIntent();
        mode = (PanelMode) intent.getSerializableExtra("PANEL_MODE");
        if (mode == PanelMode.Edit) {
            editMovie = (Movie) intent.getSerializableExtra("EDIT_MOVIE");

            subjectEditText.setText(editMovie.getName());
            descriptionEditText.setText(editMovie.getDescription());
            imageURLEditText.setText(editMovie.getImageURL());
            watchedCheckbox.setChecked(editMovie.isWatched());
            starRatingBar.setRating(editMovie.getRating());
            OnImageShowButtonClicked(null);
        }

    }

    public void OnImageShowButtonClicked(View view) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(MyApp.getContext().getContentResolver(), Uri.parse(editMovie.getImageURL()));
            image.setImageBitmap(bitmap);
        } catch (Exception ex) {
            // if failed it means this is not a uri link
            downloader = new ImageDownloader(image, this);
            imageProgress = new ProgressDialog(this);
            imageProgress.setTitle("LOADING IMAGE");
            imageProgress.setCancelable(false);
            imageProgress.setIcon(R.drawable.search_icon);
            imageProgress.setMessage("Loading image, please wait...");
            imageProgress.create();
            downloader.execute(imageURLEditText.getText().toString());
        }
    }

    public void OnConfirmButtonClicked(View view) {
        if (MyApp.verifyStoragePermissions(this) == false) {
            return;
        }

        String title = subjectEditText.getText().toString();
        if (title.isEmpty()) {
            Toast.makeText(this, "Title is required.", Toast.LENGTH_SHORT).show();
            return;
        }
        String description = descriptionEditText.getText().toString();
        if (description.length() > 200) {
            // too long descriptions crashes the app
            description = description.substring(0, 199);
        }
        String imageURL = imageURLEditText.getText().toString();
        Boolean watched = watchedCheckbox.isChecked();
        float rating = starRatingBar.getRating();

        if (editMovie == null) {
            editMovie = new Movie(-1, title, description, imageURL, watched, rating);
        } else {
            editMovie.setName(title);
            editMovie.setDescription(description);
            editMovie.setImageURL(imageURL);
            editMovie.setWatched(watched);
            editMovie.setRating(rating);
        }
        if (downloader != null) {
            try {
                String path = downloader.saveImage(editMovie.getName().replace(" ", ""));
                editMovie.setImageURL(path);
            } catch (Exception ex) {

            }
        }
        Intent intent = new Intent();
        intent.putExtra("MOVIE_DATA", editMovie);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void OnCancelButtonClicked(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void OnFinishExecuting(String result) {
        imageProgress.dismiss();
    }


    @Override
    public void OnPreExecute() {
        imageProgress.show();
    }

    @Override
    public void OnFailedExecuting() {
        Toast.makeText(this, "Failed to load image.", Toast.LENGTH_SHORT).show();
        imageProgress.dismiss();
    }


}
