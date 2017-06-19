package com.pmcoder.duermebeb.models;

public class ElementoPlaylist {
    private String artist;
    private String name;
    private String urlsong;
    private String soundcloud;
    private String youtube;
    private String web;
    private String like = "false";
    private String icon;


    public ElementoPlaylist(String artist, String name, String urlsong, String like, String icon, String soundcloud, String youtube, String web) {
        this.artist = artist;
        this.name = name;
        this.urlsong = urlsong;
        this.soundcloud = soundcloud;
        this.youtube = youtube;
        this.web = web;
        this.like = like;
        this.icon = icon;
    }

    public ElementoPlaylist(String artist, String name, String urlsong, String like, String icon) {
        this.artist = artist;
        this.name = name;
        this.urlsong = urlsong;
        this.like = like;
        this.icon = icon;
    }

    public ElementoPlaylist(String artist, String name, String urlsong, String icon) {
        this.artist = artist;
        this.name = name;
        this.urlsong = urlsong;
        this.icon = icon;
    }

    public ElementoPlaylist(String soundcloud, String youtube, String web) {
        this.soundcloud = soundcloud;
        this.youtube = youtube;
        this.web = web;
    }

    public  ElementoPlaylist(String name, String icon) {
        this.icon = icon;
        this.name = name;
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

    public String getIcon() {
        return icon;
    }

    public String getSoundcloud() {
        return soundcloud;
    }

    public String getYoutube() {
        return youtube;
    }

    public String getWeb() {
        return web;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public boolean isLikeState() {
        return like.equals("true");
    }

}
