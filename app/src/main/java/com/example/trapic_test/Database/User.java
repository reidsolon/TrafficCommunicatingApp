package com.example.trapic_test.Database;

public class User {
    public String id;
    public String firstname;
    public String lastname;
    public String passWord;
    public String eMail;

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
