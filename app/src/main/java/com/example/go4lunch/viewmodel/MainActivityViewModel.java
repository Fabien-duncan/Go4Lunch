package com.example.go4lunch.viewmodel;

import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.google.firebase.auth.FirebaseUser;


public class MainActivityViewModel extends ViewModel {
    private final AuthenticationRepository mAuthenticationRepository;
    private final MutableLiveData<FirebaseUser> userData;
    private final MutableLiveData<String> authMessageMutableLiveData;

    public MainActivityViewModel(AuthenticationRepository authenticationRepository){
        mAuthenticationRepository = authenticationRepository;
        userData = authenticationRepository.getFirebaseUserMutableLiveData();
        authMessageMutableLiveData = mAuthenticationRepository.getAuthMessageMutableLiveData();
    }
    public void firebaseCreateUser(String email, String password, String displayName){
        mAuthenticationRepository.firebaseCreateUser(email,password,displayName);
    }
    public void signInWithEmail(String email, String password){
        mAuthenticationRepository.firebaseAuthWithEmailAndPassword(email,password);
    }
    public int getGOOGLE_SIGN_IN(){
        return mAuthenticationRepository.getGOOGLE_SIGN_IN();
    }

    public MutableLiveData<FirebaseUser> getUserData() {
        return userData;
    }
    public Intent signIn() {
        return mAuthenticationRepository.signIn();
    }

    public void firebaseAuthWithGoogle(String idToken) {
        mAuthenticationRepository.firebaseAuthWithGoogle(idToken);
    }
    public MutableLiveData<String> getAuthMessageMutableLiveData() {
        return authMessageMutableLiveData;
    }
}
