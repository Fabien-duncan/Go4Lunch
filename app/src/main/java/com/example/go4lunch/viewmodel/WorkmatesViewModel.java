package com.example.go4lunch.viewmodel;

import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class WorkmatesViewModel extends ViewModel {
    private final AuthenticationRepository mAuthenticationRepository;

    private MutableLiveData<List<User>> workmatesMutableLiveData;

    public WorkmatesViewModel(AuthenticationRepository authenticationRepository){
        mAuthenticationRepository = authenticationRepository;
        workmatesMutableLiveData = authenticationRepository.getWorkmatesMutableLiveData();
    }
    public void setCurrentWorkmates(){
        mAuthenticationRepository.retrieveAllWorkmates();
    }
    public MutableLiveData<List<User>> getAllWorkmates(){
        return workmatesMutableLiveData;
    }
}
