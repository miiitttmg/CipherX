package com.example.cipherxchatapp.Models;

public class Users {
    String uid;
    String name;
    String email;
    String imageURi;
    String status;
    String statusOnOff;

    public Users() {
    }

    public Users(String uid, String name, String email, String imageURi,String status,String statusOnOff) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.imageURi = imageURi;
        this.status=status;
        this.statusOnOff=statusOnOff;
    }

    public String getStatusOnOff() {
        return statusOnOff;
    }

    public void setStatusOnOff(String statusOnOff) {
        this.statusOnOff = statusOnOff;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageURi() {
        return imageURi;
    }

    public void setImageURi(String imageURi) {
        this.imageURi = imageURi;
    }
}
