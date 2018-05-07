package com.biton.aviv.movieproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;


public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

    ImageView image;
    AsyncCallback callback;
    Bitmap bitmap;

    public ImageDownloader(ImageView image, AsyncCallback callback) {
        this.image = image;
        this.callback = callback;

    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);
            bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
            ((InputStream) url.getContent()).close();
            return bitmap;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        if (callback != null) {
            callback.OnPreExecute();
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null && image != null) {
            image.setImageBitmap(bitmap);
        } else if (callback != null) {
            callback.OnFailedExecuting();
            return;
        }
        if (callback != null) {
            callback.OnFinishExecuting("imageDownloader");
        }
    }

    public String saveImage(String image_name) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Image-" + image_name + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Bitmap is null or failed to save.");
        }
        return file.toURI().toString();
    }

}
