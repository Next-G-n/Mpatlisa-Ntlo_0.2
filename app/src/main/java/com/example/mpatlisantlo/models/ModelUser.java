package com.example.mpatlisantlo.models;

public class ModelUser {

    //use same as in the firebase database
    String User_Name;
    String Phone_Number;
    String Location;
    String User_Image;
    String uid,search,Email,User_Preferred_area,User_Preferred_Location,User_Preferred_Price,OgUsername;

    public ModelUser() {
    }

    public ModelUser(String user_Name, String phone_Number, String location, String user_Image, String uid, String search, String email, String user_Preferred_area, String user_Preferred_Location, String user_Preferred_Price, String ogUsername) {
        User_Name = user_Name;
        Phone_Number = phone_Number;
        Location = location;
        User_Image = user_Image;
        this.uid = uid;
        this.search = search;
        Email = email;
        User_Preferred_area = user_Preferred_area;
        User_Preferred_Location = user_Preferred_Location;
        User_Preferred_Price = user_Preferred_Price;
        OgUsername = ogUsername;
    }

    public String getUser_Name() {
        return User_Name;
    }

    public void setUser_Name(String user_Name) {
        User_Name = user_Name;
    }

    public String getPhone_Number() {
        return Phone_Number;
    }

    public void setPhone_Number(String phone_Number) {
        Phone_Number = phone_Number;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getUser_Image() {
        return User_Image;
    }

    public void setUser_Image(String user_Image) {
        User_Image = user_Image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUser_Preferred_area() {
        return User_Preferred_area;
    }

    public void setUser_Preferred_area(String user_Preferred_area) {
        User_Preferred_area = user_Preferred_area;
    }

    public String getUser_Preferred_Location() {
        return User_Preferred_Location;
    }

    public void setUser_Preferred_Location(String user_Preferred_Location) {
        User_Preferred_Location = user_Preferred_Location;
    }

    public String getUser_Preferred_Price() {
        return User_Preferred_Price;
    }

    public void setUser_Preferred_Price(String user_Preferred_Price) {
        User_Preferred_Price = user_Preferred_Price;
    }

    public String getOgUsername() {
        return OgUsername;
    }

    public void setOgUsername(String ogUsername) {
        OgUsername = ogUsername;
    }
}
