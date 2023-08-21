package com.example.go4lunch.repository;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.data_source.GooglePlacesDetailsApi;
import com.example.go4lunch.model.Restaurant;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class RestaurantDetailRepositoryTest {

    private Restaurant currentRestaurant;
    private RestaurantDetailRepository mRestaurantDetailRepository;
    @Mock
    private  PlacesClient placesClient;
    @Mock
    private GooglePlacesDetailsApi mGooglePlacesDetailsApi;
    @Mock
    private MutableLiveData<Restaurant> mRestaurantMutableLiveData;

    @Rule //initMocks
    public MockitoRule rule = MockitoJUnit.rule();

    @Before
    public void setUp() {
        when(mGooglePlacesDetailsApi.getRestaurantDetailMutableLiveData()).thenReturn(mRestaurantMutableLiveData);

        generateRestaurantDetails();
        generateCurrentRestaurant();

        when(mRestaurantMutableLiveData.getValue()).thenReturn(currentRestaurant);

        mRestaurantDetailRepository = new RestaurantDetailRepository(placesClient,mGooglePlacesDetailsApi);


    }

    @Test
    public void getCurrentRestaurantMutableLiveData() {
           Restaurant tempRestaurant = mRestaurantDetailRepository.getCurrentRestaurantMutableLiveData().getValue();
           assertNotNull(tempRestaurant);
           assertEquals("Zinc", tempRestaurant.getName());
    }

    @Test
    public void setSmallDetail() {
        Restaurant testRestaurant = new Restaurant();
        testRestaurant.setName("testRestaurant");
        testRestaurant.setId("123");

        mRestaurantDetailRepository.setDetail(testRestaurant);

        verify(mGooglePlacesDetailsApi).setSmallDetail(any(Restaurant.class), any(PlacesClient.class), anyString());
    }
    @Test
    public void setAllDetail() {
        Restaurant testRestaurant = new Restaurant();
        testRestaurant.setId("123");

        mRestaurantDetailRepository.setDetail(testRestaurant);

        verify(mGooglePlacesDetailsApi).setAllDetails(any(Restaurant.class), any(PlacesClient.class), anyString());
    }


    private void generateCurrentRestaurant(){
        currentRestaurant = new Restaurant();
        currentRestaurant.setName("Zinc");
        currentRestaurant.setId("01");
    }
    private void generateRestaurantDetails(){
        Restaurant restaurantDetails = new Restaurant(
                "01",
                "Zinc",
                "16 ch du four",
                45, 26,
                4.5,
                120
        );
    }
}