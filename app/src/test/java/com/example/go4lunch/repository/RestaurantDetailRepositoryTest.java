package com.example.go4lunch.repository;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.net.Uri;


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
import org.junit.Test;
import org.junit.runner.Request;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class RestaurantDetailRepositoryTest {
    private Context mContext;
    private Restaurant restaurantDetails;
    private Restaurant currentRestaurant;
    private RestaurantDetailRepository mRestaurantDetailRepository;
    private  PlacesClient placesClient;

    @Before
    public void setUp() throws Exception {
        mContext = Mockito.mock(Context.class);
        MockitoAnnotations.openMocks(this);
        placesClient = Mockito.mock(PlacesClient.class);

        setUpRepositoryMethods();
        mRestaurantDetailRepository = new RestaurantDetailRepository(mContext, placesClient);
        generateRestaurantDetails();
        generateCurrentRestaurant();
    }

    @Test
    public void getCurrentRestaurantMutableLiveData() {
           Restaurant tempRestaurant = mRestaurantDetailRepository.getCurrentRestaurantMutableLiveData().getValue();
           assertNull(tempRestaurant);
    }

    /*@Test
    public void setDetail() {
        Place mockPlace = Mockito.mock(Place.class);

        Task<FetchPlaceResponse> mockTask = Mockito.mock(Task.class);
        FetchPlaceResponse mockFetch = Mockito.mock(FetchPlaceResponse.class);

        when(placesClient.fetchPlace(any(FetchPlaceRequest.class))).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockFetch);
        when(mockFetch.getPlace()).thenReturn(mockPlace);

        // Call the method
        mRestaurantDetailRepository.setDetail(currentRestaurant);

        // Verify interactions
        verify(mockTask).addOnCompleteListener(any());
        //verify(mAuth).getCurrentUser();


    }*/
    /*@Test
    public void testSetDetailWithNonNullName() throws InterruptedException, ExecutionException {
        // Create a mock Place object
        Place mockPlace = Mockito.mock(Place.class);
        //when(mockPlace.getWebsiteUri()).thenReturn(Uri.parse("https://example.com"));
        when(mockPlace.getPhoneNumber()).thenReturn("+123456789");

        // Create a mock FetchPlaceResponse
        FetchPlaceResponse mockResponse = Mockito.mock(FetchPlaceResponse.class);
        when(mockResponse.getPlace()).thenReturn(mockPlace);

        // Create a mock FetchPlaceRequest
        FetchPlaceRequest mockRequest = Mockito.mock(FetchPlaceRequest.class);

        // Create a TaskCompletionSource for the fetchPlace Task
        TaskCompletionSource<FetchPlaceResponse> fetchPlaceSource = new TaskCompletionSource<>();
        Task<FetchPlaceResponse> fetchPlaceTask = fetchPlaceSource.getTask();
        when(placesClient.fetchPlace(mockRequest)).thenReturn(fetchPlaceTask);

        // Create a mock Restaurant
        Restaurant mockRestaurant = new Restaurant();
        mockRestaurant.setName("Test Restaurant");
        mockRestaurant.setId("testRestaurantId");

        // Call the method
        mRestaurantDetailRepository.setDetail(mockRestaurant);

        // Complete the fetchPlace Task
        fetchPlaceSource.setResult(mockResponse);

        // Use Tasks.await to wait for the task to complete
        Tasks.await(fetchPlaceTask);

        // Verify interactions and assertions
        verify(placesClient).fetchPlace(mockRequest); // Verify fetchPlace was called
        //verify(mockCurrentRestaurantMutableLiveData).postValue(mockRestaurant);
        assertEquals(mockRestaurant.getWebsite(), Uri.parse("https://example.com"));
        assertEquals(mockRestaurant.getPhoneNumber(), "+123456789");
    }*/


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
                0,
                "https://media.istockphoto.com/id/1446478827/fr/photo/un-chef-cuisine-dans-la-cuisine-de-son-restaurant.jpg?s=1024x1024&w=is&k=20&c=_KRkTJnju8zm8pTSs-aOq9J4mdtzlPc31AucwKR54CY=",
                "french",
                "23:00",
                4.5,
                120,
                "+33 6 58 32 57 01"
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