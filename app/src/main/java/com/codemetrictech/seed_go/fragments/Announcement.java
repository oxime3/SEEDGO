package com.codemetrictech.seed_go.fragments;

import java.util.Date;

public class Announcement {

    String post_title, post_date, id, link;
    Integer seen;


    public Announcement(String post_title, String post_date, String id, String link) {
        this.post_title = post_title;
        this.post_date = post_date;
        this.id = id;
        this.link = link;
    }


    public Announcement(String id){
        this.id = id;
    }

    public Announcement(){}

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Integer getSeen() {
        return seen;
    }

    public void setSeen(Integer seen) {
        this.seen = seen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getPost_date() {
        return post_date;
    }

    public void setPost_date(String post_author) {
        this.post_date = post_date;
    }

}
