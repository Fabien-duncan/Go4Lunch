package com.example.go4lunch.model;

import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
    public boolean isToday() {
        if (choiceTimeStamp == null) return false;
        else {
            Calendar now = Calendar.getInstance();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());

            String endOfLunch;
            String startOfLunch;
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            if (calendar.get(Calendar.HOUR_OF_DAY) <= 14) {
                endOfLunch = dateFormatter.format(new Date()) + "T14:00";
                calendar.setTimeInMillis(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(1));
                startOfLunch = dateFormatter.format(calendar.getTime()) + "T14:00";
            } else {
                startOfLunch = dateFormatter.format(new Date()) + "T14:00";
                calendar.setTimeInMillis(calendar.getTimeInMillis() + TimeUnit.DAYS.toMillis(1));
                endOfLunch = dateFormatter.format(calendar.getTime()) + "T14:00";
            }

            return choiceTimeStamp.compareTo(startOfLunch) >= 0 && choiceTimeStamp.compareTo(endOfLunch) < 0;
        }
    }

}
