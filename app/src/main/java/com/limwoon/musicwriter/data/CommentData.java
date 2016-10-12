package com.limwoon.musicwriter.data;

/**
 * Created by ejdej on 2016-10-10.
 */

public class CommentData {
    long commentID;
    long userID;
    long sheetID;
    String uploadTime;
    String comment;
    String userStrID;

    public String getUserStrID() {
        return userStrID;
    }

    public void setUserStrID(String userStrID) {
        this.userStrID = userStrID;
    }

    public long getCommentID() {
        return commentID;
    }

    public void setCommentID(long commentID) {
        this.commentID = commentID;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public long getSheetID() {
        return sheetID;
    }

    public void setSheetID(long sheetID) {
        this.sheetID = sheetID;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
