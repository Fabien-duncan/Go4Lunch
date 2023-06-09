package com.example.go4lunch.viewmodel;

import android.content.Intent;

import androidx.lifecycle.ViewModel;

import com.example.go4lunch.repository.AuthenticationRepository;
import com.google.firebase.auth.FirebaseUser;

public class MainActivityViewModel extends ViewModel {
    private final AuthenticationRepository mAuthenticationRepository;

    public MainActivityViewModel(AuthenticationRepository authenticationRepository){
        mAuthenticationRepository = authenticationRepository;
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


}
