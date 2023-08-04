package com.example.nasadailyimage;

import java.sql.Blob;

public class Archive {
    private String name;
    private String date;
    private String hdurl;
    private byte[] photo;

    public  Archive(){}

    public Archive(String name, String date, String hdurl, byte[] photo) {
        this.name = name;
        this.date = date;
        this.hdurl = hdurl;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHdurl() {
        return hdurl;
    }

    public void setHdurl(String hdurl) {
        this.hdurl = hdurl;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}
