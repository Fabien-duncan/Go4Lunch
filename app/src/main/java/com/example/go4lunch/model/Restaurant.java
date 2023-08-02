package com.example.go4lunch.model;

import android.net.Uri;

import com.example.go4lunch.util.FormatString;

public class Restaurant {
    private String name;
    private int distance;
    private String imageUrl;
    private String type;
    private String address;
    private int attendanceNum;
    private String openingHours;
    private double rating;
    private double lat;
    private double lng;
    private String id;
    private String phoneNumber;
    private Uri website;

    public Restaurant(){}
    public Restaurant(String id, String name, String address, double lat, double lng, int attendanceNum,String imageUrl, String type, String openingHours, double rating, int distance, String number) {
        this.id = id;
        this.name = FormatString.capitalizeEveryWord(name);
        this.imageUrl = imageUrl;
        this.type = type;
        this.address = FormatString.capitalizeEveryWord(address);
        this.attendanceNum = attendanceNum;
        this.openingHours = openingHours;
        this.rating = rating;
        this.distance = distance;
        this.phoneNumber = number;
        this.lat = lat;
        this.lng = lng;
    }
    public Restaurant(String id, String name, String address, double lat, double lng,double rating, int distance) {
        this.id = id;
        this.name = FormatString.capitalizeEveryWord(name);
        this.address = FormatString.capitalizeEveryWord(address);
        this.lat = lat;
        this.lng = lng;
        this.rating = rating;
        this.distance = distance;
        //temp extra
        this.type = "french";
        this.attendanceNum = -1;
        this.openingHours = "23:00";
        this.phoneNumber = "+33 6 58 32 57 01";
        //default image for when there is no image
        this.imageUrl="https://media.istockphoto.com/id/1446375027/fr/photo/r%C3%A9union-daffaires-dans-le-restaurant.jpg?s=1024x1024&w=is&k=20&c=fypbQnbN5F2zEI81FWLSHSA3EH5cpQXyHZDiSaWEQBY=";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = FormatString.capitalizeEveryWord(name);
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = FormatString.capitalizeEveryWord(address);
    }

    public int getAttendanceNum() {
        return attendanceNum;
    }

    public void setAttendanceNum(int attendanceNum) {
        this.attendanceNum = attendanceNum;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    /**
     * turns a rating out of 5 to out of 3 rounded to the closest integer
     * @return
     */
    public int getRating() {
        return (int)Math.round((rating/5.0) * 3);
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Uri getWebsite() {
        return website;
    }

    public void setWebsite(Uri website) {
        this.website = website;
    }
}
