package com.biton.aviv.movieproject;


import java.io.Serializable;

public class Movie implements Serializable {

    private int ID;
    private String name;
    private String description;
    private String imageURL;
    private boolean watched;
    private float rating;


    public Movie(int ID, String name, String description, String imageURL, boolean watched, float rating) {
        this.ID = ID;
        this.name = name;
        this.description = description;
        this.imageURL = imageURL;
        this.watched = watched;
        this.rating = rating;
    }


    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public boolean isWatched() {
        return watched;
    }

    public float getRating() {
        return rating;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public void setRating(float rating) {

        this.rating = rating;
    }
}

