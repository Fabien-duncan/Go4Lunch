package com.example.go4lunch.model;

import android.net.Uri;

import com.google.firebase.Timestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String displayName;
    private String firstName;
    private String lastName;
    private String email;
    private String lunchChoiceId;
    private String lunchChoiceName;
    private String choiceTimeStamp;
    private Uri photoUrl;
    private List<String> favoriteRestaurants;


    public User(String displayName,String firstName, String lastName, String email, Uri photoUrl) {
        this.displayName = displayName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.photoUrl = photoUrl;
        favoriteRestaurants = new ArrayList<>();
    }
    public User(String displayName, String email, Uri photoUrl) {
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.lunchChoiceId="";
        this.lunchChoiceName="";
        favoriteRestaurants = new ArrayList<>();
    }
    public User(){

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLunchChoiceId() {
        return lunchChoiceId;
    }

    public void setLunchChoiceId(String lunchChoiceId) {
        this.lunchChoiceId = lunchChoiceId;
    }

    public String getChoiceTimeStamp() {
        return choiceTimeStamp;
    }

    public void setChoiceTimeStamp(String choiceTimeStamp) {
        this.choiceTimeStamp = choiceTimeStamp;
    }
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Uri getPhotoUrl() {
        return photoUrl;
    }

    /*public void setPhotoUrl(Uri photoUrl) {
        this.photoUrl = photoUrl;
    }*/
    public void setPhotoUrl(String photoUrl){
        this.photoUrl = Uri.parse(photoUrl);
    }

    public String getLunchChoiceName() {
        return lunchChoiceName;
    }

    public void setLunchChoiceName(String lunchChoiceName) {
        this.lunchChoiceName = lunchChoiceName;
    }

    public List<String> getFavoriteRestaurants() {
        return favoriteRestaurants;
    }

    public void setFavoriteRestaurants(List<String> favoriteRestaurants) {
        this.favoriteRestaurants = favoriteRestaurants;
    }
    public boolean isFavorite(String restaurantID){
        boolean found = false;
        int i = 0;
        while(!found && i < favoriteRestaurants.size()){
            if(restaurantID.equals(favoriteRestaurants.get(i))) found = true;
            i++;
        }
        return found;
    }
}
