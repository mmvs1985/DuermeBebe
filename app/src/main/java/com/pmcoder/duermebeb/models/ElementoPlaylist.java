package com.pmcoder.duermebeb.models;

public class ElementoPlaylist {
    private String artist;
    private String name;
    private String urlsong;
    private String soundcloud;
    private String youtube;
    private String web;
    private String like = "false";
    private String urlimg;


    public ElementoPlaylist(String artist, String name, String urlsong, String like, String urlimg, String soundcloud, String youtube, String web) {
        this.artist = artist;
        this.name = name;
        this.urlsong = urlsong;
        this.soundcloud = soundcloud;
        this.youtube = youtube;
        this.web = web;
        this.like = like;
        this.urlimg = urlimg;
    }

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

    public ElementoPlaylist(String artist, String name, String urlsong, String urlimg, String soundcloud, String youtube, String web) {
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

    public String getSoundcloud() {
        return soundcloud;
    }

    public void setSoundcloud(String soundcloud) {
        this.soundcloud = soundcloud;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public boolean isLikeState() {
        return like.equals("true");
    }

}
