package com.example.go4lunch.viewmodel;

import android.location.Location;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.example.go4lunch.repository.ConnectedActivityRepository;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDateTime;
import java.util.List;

public class ConnectedActivityViewModel extends ViewModel {
    private final AuthenticationRepository mAuthenticationRepository;
    private final MutableLiveData<FirebaseUser> userData;
    private final MutableLiveData<Boolean> isUserSignedIn;
    private final MutableLiveData<User> currentUserMutableLiveData;

    private final MutableLiveData<List<User>> workmatesMutableLiveData;

    private final MutableLiveData<List<Restaurant>> restaurantsMutableLiveData;
    private final ConnectedActivityRepository mConnectedActivityRepository;


    public ConnectedActivityViewModel(AuthenticationRepository authenticationRepository, ConnectedActivityRepository connectedActivityRepository){
        mAuthenticationRepository = authenticationRepository;
        mConnectedActivityRepository = connectedActivityRepository;

        userData = authenticationRepository.getFirebaseUserMutableLiveData();
        isUserSignedIn = authenticationRepository.getIsUserSignedIn();
        currentUserMutableLiveData = authenticationRepository.getCurrentUserMutableLiveData();
        workmatesMutableLiveData = authenticationRepository.getWorkmatesMutableLiveData();
        restaurantsMutableLiveData = mConnectedActivityRepository.getRestaurantsMutableLiveData();
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
    @RequiresApi(api = Build.VERSION_CODES.O)
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
