package com.example.go4lunch.model;

import java.time.LocalDateTime;

public class User {
    private String displayName;
    private String firstName;
    private String lastName;
    private String email;
    private double lunchChoiceId;
    private LocalDateTime choiceTimeStamp;


    public User(String displayName,String firstName, String lastName, String email) {
        this.displayName = displayName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    public User(String displayName, String email) {
        this.displayName = displayName;
        this.email = email;
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

    public double getLunchChoiceId() {
        return lunchChoiceId;
    }

    public void setLunchChoiceId(double lunchChoiceId) {
        this.lunchChoiceId = lunchChoiceId;
    }

    public LocalDateTime getChoiceTimeStamp() {
        return choiceTimeStamp;
    }

    public void setChoiceTimeStamp(LocalDateTime choiceTimeStamp) {
        this.choiceTimeStamp = choiceTimeStamp;
    }
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
