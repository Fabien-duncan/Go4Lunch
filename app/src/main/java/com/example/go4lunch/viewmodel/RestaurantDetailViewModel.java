package com.example.go4lunch.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.AuthenticationRepository;

import java.util.List;

public class RestaurantDetailViewModel extends ViewModel{
    private final AuthenticationRepository mAuthenticationRepository;

    private MutableLiveData<List<User>> attendingWorkmatesMutableLiveData;

    public RestaurantDetailViewModel(AuthenticationRepository authenticationRepository, String restaurantId){
        mAuthenticationRepository = authenticationRepository;
        attendingWorkmatesMutableLiveData = authenticationRepository.getWorkmatesMutableLiveData();
        mAuthenticationRepository.retrieveFilteredWorkmates(restaurantId);
    }

    public MutableLiveData<List<User>> getAllWorkmates(){
        return attendingWorkmatesMutableLiveData;
    }
}
