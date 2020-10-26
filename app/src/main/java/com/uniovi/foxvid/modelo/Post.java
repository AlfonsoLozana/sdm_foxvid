package com.uniovi.foxvid.modelo;


import com.google.firebase.Timestamp;

public class Post {

    private String title;
    private String text;
    private User user;
    private Timestamp date;
    private Coordinate localization;

    public Post() {
    }

    public Post(String title, String text, User user, Timestamp  date, Coordinate localization) {
        this.title = title;
        this.text = text;
        this.user = user;
        this.date = date;
        this.localization = localization;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Coordinate getLocalization() {
        return localization;
    }

    public void setLocalization(Coordinate localization) {
        this.localization = localization;
    }
}
