package com.example.trapic_test.Model;

public class User {
    public String user_id = null;
    public String user_firstname = null;
    public String user_lastname = null;
    public String user_passWord = null;
    public String user_eMail = null;

    public User(){

    }

    public User(String id, String firstname, String lastname, String passWord, String eMail){
       this.user_id = id;
       this.user_firstname = firstname;
       this.user_lastname = lastname;
       this.user_passWord = passWord;
       this.user_eMail = eMail;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_firstname() {
        return user_firstname;
    }

    public void setUser_firstname(String user_firstname) {
        this.user_firstname = user_firstname;
    }

    public String getUser_lastname() {
        return user_lastname;
    }

    public void setUser_lastname(String user_lastname) {
        this.user_lastname = user_lastname;
    }

    public String getUser_passWord() {
        return user_passWord;
    }

    public void setUser_passWord(String user_passWord) {
        this.user_passWord = user_passWord;
    }

    public String getUser_eMail() {
        return user_eMail;
    }

    public void setUser_eMail(String user_eMail) {
        this.user_eMail = user_eMail;
    }
}
