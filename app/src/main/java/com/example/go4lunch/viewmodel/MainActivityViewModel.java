package com.example.go4lunch.viewmodel;

import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivityViewModel extends ViewModel {
    private final AuthenticationRepository mAuthenticationRepository;
    private MutableLiveData<FirebaseUser> userData;
    private MutableLiveData<Boolean> isUserSignedIn;
    private MutableLiveData<User> currentUserMutableLiveData;

    private MutableLiveData<List<User>> allUsersMutableLiveData;

    public MainActivityViewModel(AuthenticationRepository authenticationRepository){
        mAuthenticationRepository = authenticationRepository;
        userData = authenticationRepository.getFirebaseUserMutableLiveData();
        isUserSignedIn = authenticationRepository.getIsUserSignedIn();
        currentUserMutableLiveData = authenticationRepository.getCurrentUserMutableLiveData();
        allUsersMutableLiveData = authenticationRepository.getAllUsersMutableLiveData();
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

    public MutableLiveData<List<User>> getAllUsersMutableLiveData() {
        return allUsersMutableLiveData;
    }
    /*public User getCurrentUser(){
        return mAuthenticationRepository.getCurrentUser();
    }*/
}
