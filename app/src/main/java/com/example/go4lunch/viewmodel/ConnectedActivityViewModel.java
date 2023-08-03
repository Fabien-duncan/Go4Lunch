package com.example.go4lunch.viewmodel;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.example.go4lunch.repository.ConnectedActivityRepository;
import com.example.go4lunch.util.ReminderBroadcast;
import com.example.go4lunch.views.ConnectedActivity;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

public class ConnectedActivityViewModel extends ViewModel {
    private final AuthenticationRepository mAuthenticationRepository;
    private MutableLiveData<FirebaseUser> userData;
    private MutableLiveData<Boolean> isUserSignedIn;
    private MutableLiveData<User> currentUserMutableLiveData;

    private MutableLiveData<List<User>> workmatesMutableLiveData;

    private MutableLiveData<List<Restaurant>> restaurantsMutableLiveData;
    private ConnectedActivityRepository mConnectedActivityRepository;
    private MutableLiveData<Restaurant> currentRestaurantChoice;


    public ConnectedActivityViewModel(AuthenticationRepository authenticationRepository, Context context){
        mAuthenticationRepository = authenticationRepository;
        mConnectedActivityRepository = new ConnectedActivityRepository(context);

        userData = authenticationRepository.getFirebaseUserMutableLiveData();
        isUserSignedIn = authenticationRepository.getIsUserSignedIn();
        currentUserMutableLiveData = authenticationRepository.getCurrentUserMutableLiveData();
        workmatesMutableLiveData = authenticationRepository.getWorkmatesMutableLiveData();
        restaurantsMutableLiveData = mConnectedActivityRepository.getRestaurantsMutableLiveData();
        currentRestaurantChoice = mConnectedActivityRepository.getCurrentRestaurantChoice();
    }


    public void setupGoogleSignInOptions(){
        mAuthenticationRepository.setupGoogleSignInOptions();
    }
    public void signOut() {
        mAuthenticationRepository.signOut();
    }

    public MutableLiveData<FirebaseUser> getUserData() {
        return userData;
    }

    public MutableLiveData<Boolean> getIsUserSignedIn() {
        return isUserSignedIn;
    }

    public MutableLiveData<User> getCurrentUserMutableLiveData() {
        return currentUserMutableLiveData;
    }
    public void setCurrentWorkmates(){
        mAuthenticationRepository.retrieveAllWorkmates();
    }
    public MutableLiveData<List<User>> getWorkmatesMutableLiveData() {
        return mAuthenticationRepository.getWorkmatesMutableLiveData();
        //return workmatesMutableLiveData;
    }
    public MutableLiveData<List<User>> getAllWorkmates(){
        return workmatesMutableLiveData;
    }
    public void setGooglePlacesData(){
        mConnectedActivityRepository.setGooglePlacesData();
    }

    public MutableLiveData<List<Restaurant>> getRestaurantsMutableLiveData() {
        return restaurantsMutableLiveData;
    }

    public void updateUserRestaurantChoice(String newChoiceId, String newChoiceName, LocalDateTime choiceTimeStamp){
        mAuthenticationRepository.updateUserRestaurantChoice(newChoiceId, newChoiceName, choiceTimeStamp);
    }
    public void updateUserRestaurantFavorite(String restaurantID, String type){
        mAuthenticationRepository.updateUserRestaurantFavorite(restaurantID, type);
    }
    public void updateAttending(List<User> workmates){
        mConnectedActivityRepository.updateAttending(workmates);
    }
    public void resetNearbyRestaurants(){
        mConnectedActivityRepository.resetNearbyRestaurants();
    }
    public void autocomplete(String text){
        mConnectedActivityRepository.autocomplete(text);
    }

    public void setCurrentLocation(Location currentLocation) {
        mConnectedActivityRepository.setCurrentLocation(currentLocation);
    }

}
