package com.example.go4lunch.viewmodel;

import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.google.firebase.auth.FirebaseUser;


public class MainActivityViewModel extends ViewModel {
    private final AuthenticationRepository mAuthenticationRepository;
    private final MutableLiveData<FirebaseUser> userData;

    public MainActivityViewModel(AuthenticationRepository authenticationRepository){
        mAuthenticationRepository = authenticationRepository;
        userData = authenticationRepository.getFirebaseUserMutableLiveData();
    }
    public void firebaseCreateUser(String email, String password, String displayName){
        mAuthenticationRepository.firebaseCreateUser(email,password,displayName);
    }
    public void signInWithEmail(String email, String password){
        mAuthenticationRepository.firebaseAuthWithEmailAndPassword(email,password);
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
    public void signIn() {
        mAuthenticationRepository.signIn();
    }
    public void setupGoogleSignInOptions(){
        mAuthenticationRepository.setupGoogleSignInOptions();
    }
}
