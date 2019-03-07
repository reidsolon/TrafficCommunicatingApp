package com.example.trapic_test.Model;

public class User {
    public String id = null;
    public String firstname = null;
    public String lastname = null;
    public String passWord = null;
    public String eMail = null;

    public User(){

    }

    public User(String id, String firstname, String lastname, String passWord, String eMail){
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.passWord = passWord;
        this.eMail = eMail;
    }

    public String getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }



    public String getPassWord() {
        return passWord;
    }

    public String geteMail() {
        return eMail;
    }
}
