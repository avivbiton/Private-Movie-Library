package com.biton.aviv.movieproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class Database extends SQLiteOpenHelper {
    public Database() {
        super(MyApp.getContext(), "MoviesDatabase", null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE Movies\n" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "name TEXT NOT NULL,\n" +
                "description TEXT NOT NULL,\n" +
                "imageURL TEXT NOT NULL,\n" +
                "watched BOOL NOT NULL,\n" +
                "rating FLOAT NOT NULL)\n" +
                "\n" +
                "        ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVer, int newVer) {
        ClearDatabase();
    }


    public void ClearDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE Movies");
        onCreate(db);
    }


    public void AddMovie(Movie m) {

        String query = String.format("INSERT INTO Movies(name, description, imageURL, watched, rating)\n" +
                "VALUES('%s', '%s', '%s', %d, %f)", m.getName(), m.getDescription(), m.getImageURL(), (m.isWatched()) ? 1 : 0, m.getRating());

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        Cursor cursor = db.rawQuery("SELECT last_insert_rowid()", null);
        cursor.moveToNext();
        m.setID(cursor.getInt((0)));
        cursor.close();
        db.close();

    }

    public void UpdateMovie(Movie movie) {
        String query = "UPDATE Movies\n" +
                "SET name='" + movie.getName() + "',\n" +
                "description='" + movie.getDescription() + "',\n" +
                "imageURL='" + movie.getImageURL() + "',\n" +
                "watched=" + (movie.isWatched() ? "1" : "0") + ",\n" +
                "rating=" + movie.getRating() + "\n" +
                "WHERE _id=" + movie.getID();

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        db.close();
    }


    public void DeleteMovie(Movie m) {
        String query = "DELETE FROM Movies WHERE _id=" + m.getID();
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public ArrayList<Movie> getAllMovies() {
        ArrayList<Movie> movies = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Movies", null);


        int nameIndex = cursor.getColumnIndex("name");
        int descriptionIndex = cursor.getColumnIndex("description");
        int imageURLIndex = cursor.getColumnIndex("imageURL");
        int watchedIndex = cursor.getColumnIndex("watched");
        int ratingIndex = cursor.getColumnIndex("rating");

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(nameIndex);
            String description = cursor.getString(descriptionIndex);
            String imageURL = cursor.getString(imageURLIndex);
            boolean watched = cursor.getInt(watchedIndex) == 1 ? true : false;
            short rating = (short) cursor.getInt(ratingIndex);

            Movie m = new Movie(id, name, description, imageURL, watched, rating);
            movies.add(m);
        }

        cursor.close();
        db.close();

        return movies;

    }


}
