package com.example.go4lunch.viewmodel;

import android.content.Intent;
import android.location.Location;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.example.go4lunch.repository.ConnectedActivityRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ConnectedActivityViewModel {
    private final AuthenticationRepository mAuthenticationRepository;
    private MutableLiveData<FirebaseUser> userData;
    private MutableLiveData<Boolean> isUserSignedIn;
    private MutableLiveData<User> currentUserMutableLiveData;

    private MutableLiveData<List<User>> workmatesMutableLiveData;

    private MutableLiveData<List<Restaurant>> restaurantsMutableLiveData;
    private ConnectedActivityRepository mConnectedActivityRepository;

    public ConnectedActivityViewModel(AuthenticationRepository authenticationRepository){
        mAuthenticationRepository = authenticationRepository;
        userData = authenticationRepository.getFirebaseUserMutableLiveData();
        isUserSignedIn = authenticationRepository.getIsUserSignedIn();
        currentUserMutableLiveData = authenticationRepository.getCurrentUserMutableLiveData();
        workmatesMutableLiveData = authenticationRepository.getWorkmatesMutableLiveData();

        mConnectedActivityRepository = new ConnectedActivityRepository();
        restaurantsMutableLiveData = mConnectedActivityRepository.getRestaurantsMutableLiveData();

    }
    public void setupGoogleSignInOptions(){
        mAuthenticationRepository.setupGoogleSignInOptions();
    }
    public boolean isUserAlreadySignIn() {
        return mAuthenticationRepository.isUserAlreadySignIn();
    }
    public void signIn() {
        mAuthenticationRepository.signIn();
    }
    public void signOut() {
        mAuthenticationRepository.signOut();
    }
    public FirebaseUser getProfileInfo(){
        return mAuthenticationRepository.getProfileInfo();
    }
    public void handleSignInResult(Intent data){
        mAuthenticationRepository.handleSignInResult(data);
    }
    public int getGOOGLE_SIGN_IN(){
        return mAuthenticationRepository.getGOOGLE_SIGN_IN();
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
        return mAuthenticationRepository.getAllWorkmates();
    }
    public void setGooglePlacesData(Location currentLocation){
        mConnectedActivityRepository.setGooglePlacesData(currentLocation);
    }

    public MutableLiveData<List<Restaurant>> getRestaurantsMutableLiveData() {
        return restaurantsMutableLiveData;
    }

    public void setRestaurantsDistance(Location currentLocation){
        mConnectedActivityRepository.setRestaurantsDistance(currentLocation);
    }
    public void updateUserRestaurantChoice(String newChoiceId, String newChoiceName){
        mAuthenticationRepository.updateUserRestaurantChoice(newChoiceId, newChoiceName);
    }
    public void setFilteredWorkmates(String restaurantId){
        mAuthenticationRepository.retrieveFilteredWorkmates(restaurantId);
    }
}
