package com.limwoon.musicwriter.data;

import java.io.Serializable;

/**
 * Created by ejdej on 2016-08-24.
 */
public class SheetData implements Serializable {
    private long id;
    private String title;
    private String author;
    private String note;
    private long likes;
    private long comments;
    private String uploadTime;
    private String uploadUserStrID;

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public long getComments() {
        return comments;
    }

    public void setComments(long comments) {
        this.comments = comments;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getUploadUserStrID() {
        return uploadUserStrID;
    }

    public void setUploadUserStrID(String uploadUserStrID) {
        this.uploadUserStrID = uploadUserStrID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
