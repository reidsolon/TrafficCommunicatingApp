package com.example.trapic_test.Model;

public class Comment {

    public Comment(){

    }
    private String publisher_id;
    private String post_id;
    private String cmt_msg;
    private String comment_user_id;
    private String comment_user_fullname;

    public Comment(String id, String id2, String msg, String id3, String fullname){
        this.publisher_id = id;
        this.post_id = id2;
        this.cmt_msg = msg;
        this.comment_user_id = id3;
        this.comment_user_fullname = fullname;
    }

    public String getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(String publisher_id) {
        this.publisher_id = publisher_id;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getCmt_msg() {
        return cmt_msg;
    }

    public void setCmt_msg(String cmt_msg) {
        this.cmt_msg = cmt_msg;
    }

    public String getComment_user_id() {
        return comment_user_id;
    }

    public String getComment_user_fullname() {
        return comment_user_fullname;
    }
}
