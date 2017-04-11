package com.pmcoder.duermebeb.models;

public class ElementoPlaylist {
    private String artist;
    private String name;
    private String urlsong;
    private String like = "false";
    private String urlimg;


    public ElementoPlaylist(String artist, String name, String urlsong, String like, String urlimg) {
        this.artist = artist;
        this.name = name;
        this.urlsong = urlsong;
        this.like = like;
        this.urlimg = urlimg;
    }

    public ElementoPlaylist(String artist, String name, String urlsong, String urlimg) {
        this.artist = artist;
        this.name = name;
        this.urlsong = urlsong;
        this.urlimg = urlimg;
    }

    public String getArtist() {
        return artist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlsong() {
        return urlsong;
    }

    public String getUrlimg() {
        return urlimg;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public boolean isLikeState() {
        return like.equals("true");
    }

}
