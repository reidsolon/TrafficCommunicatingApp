package com.example.trapic_test.Model;

public class Log {

    public Log(){

    }
//    POST LOG VARIABLE
    String post_id;
    String post_user_id;
    String post_txt_msg;
    String post_date_time;

//    COMMENT LOG VARIABLE
    private String comment_id;
    private String comment_post_id;
    private String comment_user_id;

//    LOGIN LOG VARIABLE
    private String login_user_id;
    private String login_txt_msg;
    private String login_date_time;

//    REGISTER LOG VARIABLE
    private String register_user_id;
    private String register_txt_msg;
    private String register_date_time;

//    REPORT LOG VARIABLE
    private String report_id;
    private String report_post_id;
    private String report_user_id;

//    Login Log Constructor
    public Log(String id, String txt_msg, String date_time){
        this.login_user_id = id;
        this.login_txt_msg = txt_msg;
        this.login_date_time = date_time;
    }
// REGISTER LOG CONSTRUCTOR
    public Log(int i, String id, String txt_msg, String date_time){
        this.register_date_time=date_time;
        this.register_txt_msg = txt_msg;
        this.register_user_id = id;
    }
//    POST LOG CONSTRUCTOR
    public Log(String i, String id, String user_id, String txt_msg, String date_time){
        this.post_id = id;
        this.post_user_id = user_id;
        this.post_txt_msg = txt_msg;
        this.post_date_time = date_time;
    }

    public String getPost_id() {
        return post_id;
    }

    public String getPost_user_id() {
        return post_user_id;
    }

    public String getPost_txt_msg() {
        return post_txt_msg;
    }

    public String getPost_date_time() {
        return post_date_time;
    }

    public String getLogin_user_id() {
        return login_user_id;
    }

    public String getLogin_date_time() {
        return login_date_time;
    }

    public String getLogin_txt_msg() {
        return login_txt_msg;
    }
    //    Register log constructor

}
