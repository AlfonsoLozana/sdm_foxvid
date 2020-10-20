package com.uniovi.foxvid.modelo;


import java.sql.Timestamp;

public class Post {

    private String title;
    private String text;
    private User user;
    private String date;
    private String localization;

    public Post() {
    }

    public Post(String title, String text, User user, String  date, String localization) {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocalization() {
        return localization;
    }

    public void setLocalization(String localization) {
        this.localization = localization;
    }
}
