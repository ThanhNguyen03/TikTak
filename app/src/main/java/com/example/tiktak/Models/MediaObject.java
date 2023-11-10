package com.example.tiktak.Models;

import com.google.firebase.firestore.PropertyName;

public class MediaObject {

    @PropertyName("title")
    private String title;
    @PropertyName("description")
    private String description;
    @PropertyName("date")
    private String date;
    @PropertyName("user_id")
    private String user_id;
    @PropertyName("post_categories")
    private String post_categories;
    @PropertyName("post_id")
    private String post_id;
    @PropertyName("view")
    private String view;
    @PropertyName("user_name")
    private String user_name;
    @PropertyName("media_url")
    private String media_url;
    @PropertyName("thumbnail")
    private String thumbnail;

    public MediaObject(){}
    public MediaObject(String title, String description, String date, String userId, String postCategories, String postId, String view, String userName, String mediaUrl, String thumbnail){
        this.title = title;
        this.description = description;
        this.date = date;
        this.user_id = userId;
        this.post_categories = postCategories;
        this.post_id = postId;
        this.view = view;
        this.user_name = userName;
        this.media_url = mediaUrl;
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }


    public String getUser_id() {
        return user_id;
    }

    public String getPost_categories() {
        return post_categories;
    }

    public String getPost_id() {
        return post_id;
    }

    public String getView() {
        return view;
    }


    public String getUser_name() {
        return user_name;
    }


    public String getMedia_url() {
        return media_url;
    }


    public String getThumbnail() {
        return this.thumbnail;
    }

}
