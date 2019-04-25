package com.example.trapic_test.Model;

public class Notification {

    private String notif_id;
    private String notif_user_id;

    private String notif_type;
    private String notif_post_id;
    private String notif_userId;
    private String notif_pub_id;
    private String notif_time;


    public Notification(){

    }

    public Notification(String notif_id) {
        this.notif_id = notif_id;

    }

    public Notification(String type, String user_id, String post_id, String pub_id, String time){
       this.notif_type = type;
       this.notif_post_id = post_id;
        this.notif_userId = user_id;
        this.notif_pub_id = pub_id;
        this.notif_time = time;
    }

    public String getNotif_post_id() {
        return notif_post_id;
    }

    public String getNotif_pub_id() {
        return notif_pub_id;
    }

    public String getNotif_time() {
        return notif_time;
    }

    public String getNotif_type() {
        return notif_type;
    }

    public String getNotif_userId() {
        return notif_userId;
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
