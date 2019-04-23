package com.example.trapic_test.Model;

public class Notification {

    private String notif_id;
    private String notif_user_id;

    private String type;
    private String post_id;
    private String user_id;
    private String pub_id;


    public Notification(){

    }

    public Notification(String notif_id) {
        this.notif_id = notif_id;

    }

    public Notification(String type, String user_id, String post_id, String pub_id){
        this.type = type;
        this.post_id = post_id;
        this.user_id = user_id;
        this.pub_id = pub_id;
    }

    public String getPub_id() {
        return pub_id;
    }

    public String getType() {
        return type;
    }

    public String getPost_id() {
        return post_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getNotif_id() {
        return notif_id;
    }

    public void setNotif_id(String notif_id) {
        this.notif_id = notif_id;
    }


    public String getNotif_user_id() {
        return notif_user_id;
    }

    public void setNotif_user_id(String notif_user_id) {
        this.notif_user_id = notif_user_id;
    }
}
