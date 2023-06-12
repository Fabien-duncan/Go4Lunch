package com.example.go4lunch.model;

public class Restaurant {
    private String name;
    private int distance;
    private String imageUrl;
    private String type;
    private String address;
    private int attendanceNum;
    private String openingHours;
    private double rating;

    public Restaurant(String name, String imageUrl, String type, String address, int attendanceNum, String openingHours, double rating) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.type = type;
        this.address = address;
        this.attendanceNum = attendanceNum;
        this.openingHours = openingHours;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        this.address = address;
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
}