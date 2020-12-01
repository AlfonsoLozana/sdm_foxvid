package com.uniovi.foxvid.modelo;

public class News {

    private String title;
    private String summary;
    private String image;
    private String urlNews;

    public News(String title, String summary, String image, String urlNews){
        this.title=title;
        this.summary=summary;
        this.image=image;
        this.urlNews=urlNews;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrlNews() {
        return urlNews;
    }

    public void setUrlNews(String urlNews) {
        this.urlNews = urlNews;
    }

    @Override
    public String toString() {
        return "News{" +
                "title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", image='" + image + '\'' +
                ", urlNews='" + urlNews + '\'' +
                '}';
    }
}
