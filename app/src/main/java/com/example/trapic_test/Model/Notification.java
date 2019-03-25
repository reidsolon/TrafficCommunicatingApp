package com.example.trapic_test.Model;

public class Notification {

    private String notif_id;
    private String notif_user_id;

    public Notification(){

    }

    public Notification(String notif_id) {
        this.notif_id = notif_id;

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
