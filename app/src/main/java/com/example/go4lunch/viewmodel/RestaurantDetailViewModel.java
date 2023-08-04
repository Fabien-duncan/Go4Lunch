package com.example.go4lunch.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.example.go4lunch.repository.RestaurantDetailRepository;

import java.util.List;

public class RestaurantDetailViewModel extends ViewModel{
    private final AuthenticationRepository mAuthenticationRepository;

    private final MutableLiveData<List<User>> attendingWorkmatesMutableLiveData;
    private final RestaurantDetailRepository mRestaurantDetailRepository;
    private final MutableLiveData<Restaurant> currentRestaurantMutableLiveDate;

    public RestaurantDetailViewModel(AuthenticationRepository authenticationRepository, RestaurantDetailRepository restaurantDetailRepository){
        mAuthenticationRepository = authenticationRepository;
        attendingWorkmatesMutableLiveData = authenticationRepository.getWorkmatesMutableLiveData();
        mRestaurantDetailRepository = restaurantDetailRepository;

        currentRestaurantMutableLiveDate = mRestaurantDetailRepository.getCurrentRestaurantMutableLiveData();
    }

    public void setDetail(Restaurant currentRestaurant){
        mRestaurantDetailRepository.setDetail(currentRestaurant);
    }

    public MutableLiveData<List<User>> getAllWorkmates(){
        return attendingWorkmatesMutableLiveData;
    }
    public void retrieveFilteredWorkmates(String restaurantId){
        mAuthenticationRepository.retrieveFilteredWorkmates(restaurantId);
    }

    public MutableLiveData<Restaurant> getCurrentRestaurantMutableLiveDate() {
        return currentRestaurantMutableLiveDate;
    }
}
