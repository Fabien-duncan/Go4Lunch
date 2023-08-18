package com.example.go4lunch.repository;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.net.Uri;


import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.dataSource.GooglePlacesDetailsApi;
import com.example.go4lunch.model.Restaurant;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Request;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class RestaurantDetailRepositoryTest {

    private Restaurant restaurantDetails;
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
    public void setUp() throws Exception {
        setUpRepositoryMethods();
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
        restaurantDetails = new Restaurant(
                "01",
                "Zinc",
                "16 ch du four",
                45,26,
                4.5,
                120
        );
    }
    private void setUpRepositoryMethods(){
       /* Task<FetchPlaceResponse> task = Tasks.forResult(null);
        FetchPlaceResponse fetchPlaceResponse = Mockito.mock(FetchPlaceResponse.class);
        Place mockPlace = Mockito.mock(Place.class);

        when(fetchPlaceResponse.getPlace()).thenReturn(mockPlace);
        when(mockPlace.getWebsiteUri()).thenReturn(null);
        //when(task.getResult()).thenReturn(fetchPlaceResponse);
        //when(task.isSuccessful()).thenReturn(true);
        when(placesClient.fetchPlace(any(FetchPlaceRequest.class))).thenReturn(task);*/

    }
}