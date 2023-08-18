package com.example.go4lunch.model;

import android.graphics.Bitmap;
import android.net.Uri;

import com.example.go4lunch.util.FormatString;
/**
 * Represents a restaurant with various attributes such as name, distance, image URL, etc.
 */
public class Restaurant {
    private String name;
    private int distance;
    private String imageUrl;
    private Bitmap imageBitmap;
    private String address;
    private int attendanceNum;
    private String openingHours;
    private double rating;
    private double lat;
    private double lng;
    private String id;
    private String phoneNumber;
    private Uri website;
    /**
     * Default constructor for the Restaurant class.
     */
    public Restaurant(){}
    /**
     * Constructs a Restaurant object with provided attributes.
     *
     * @param id         The unique identifier of the restaurant.
     * @param name       The name of the restaurant.
     * @param address    The address of the restaurant.
     * @param lat        The latitude coordinate of the restaurant's location.
     * @param lng        The longitude coordinate of the restaurant's location.
     * @param rating     The rating of the restaurant.
     * @param distance   The distance to the restaurant.
     */
    public Restaurant(String id, String name, String address, double lat, double lng,double rating, int distance) {
        this.id = id;
        this.name = FormatString.capitalizeEveryWord(name);
        this.address = FormatString.capitalizeEveryWord(address);
        this.lat = lat;
        this.lng = lng;
        this.rating = rating;
        this.distance = distance;
        //temp extra
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
     * Converts the rating out of 5 to a rating out of 3 rounded to the closest integer.
     *
     * @return The converted rating.
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

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }
}
