package com.example.go4lunch.viewmodel;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;

import android.content.Context;
import android.content.Intent;
import android.location.Location;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.example.go4lunch.repository.ConnectedActivityRepository;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConnectedActivityViewModelTest {

    private AuthenticationRepository mAuthenticationRepository;
    private ConnectedActivityRepository mConnectedActivityRepository;
    private FirebaseUser mFirebaseUser;
    private ConnectedActivityViewModel mConnectedActivityViewModel;
    private User newUser;
    private boolean isUserSignedIn;
    private MutableLiveData<Boolean> isUserSignedInMutable;
    private User currentUser;
    private List<User> workmateList;
    private List<Restaurant> restaurantList;
    private Location currentLocation;
    @Before
    public void setUp() throws Exception {
        mAuthenticationRepository = Mockito.mock(AuthenticationRepository.class);
        mConnectedActivityRepository = Mockito.mock(ConnectedActivityRepository.class);
        mFirebaseUser = Mockito.mock(FirebaseUser.class);
        isUserSignedIn = false;
        workmateList = new ArrayList<>();
        restaurantList = new ArrayList<>();

        currentLocation = Mockito.mock(Location.class);
        Mockito.doReturn(0.0).when(currentLocation).getLongitude();

        isUserSignedInMutable = Mockito.spy(new MutableLiveData<>(false));
        Mockito.doReturn("Fabien").when(mFirebaseUser).getDisplayName();

        MutableLiveData<FirebaseUser> mutableFirebaseUser= Mockito.spy(new MutableLiveData<>(mFirebaseUser));
        Mockito.doReturn(mutableFirebaseUser).when(mAuthenticationRepository).getFirebaseUserMutableLiveData();

        generateUser();
        generateWorkmates();
        generateRestaurants();
        setUpRepositoryMethods();

        mConnectedActivityViewModel = new ConnectedActivityViewModel(mAuthenticationRepository, mConnectedActivityRepository);
    }

    @Test
    public void signOut() {
        isUserSignedIn = true;
        mConnectedActivityViewModel.signOut();
        Mockito.verify(mAuthenticationRepository).signOut();
        assertEquals(false, isUserSignedIn);

    }
    @Test
    public void getUserData() {
        FirebaseUser tempUser =  mConnectedActivityViewModel.getUserData().getValue();
        assertEquals("Fabien", tempUser.getDisplayName());
    }

    @Test
    public void getIsUserSignedIn() {
        boolean isSignedIn = mConnectedActivityViewModel.getIsUserSignedIn().getValue();
        Mockito.verify(mAuthenticationRepository).getIsUserSignedIn();
        assertEquals(false, isSignedIn);
    }

    @Test
    public void getCurrentUserMutableLiveData() {
        User user = mConnectedActivityViewModel.getCurrentUserMutableLiveData().getValue();
        assertEquals("Fabien", user.getDisplayName());
        assertEquals("fab@gmail.com", user.getEmail());

    }

    @Test
    public void setCurrentWorkmates() {
        mConnectedActivityViewModel.setCurrentWorkmates();
        Mockito.verify(mAuthenticationRepository).retrieveAllWorkmates();
    }

    @Test
    public void getAllWorkmates() {
        List<User> workmates = mConnectedActivityViewModel.getAllWorkmates().getValue();
        assertEquals("Fabien Duncan", workmates.get(0).getDisplayName());
        assertEquals("Bob", workmates.get(2).getDisplayName());
    }

    @Test
    public void setGooglePlacesData() {
        mConnectedActivityViewModel.setGooglePlacesData();
        Mockito.verify(mConnectedActivityRepository).setGooglePlacesData();
    }

    @Test
    public void getRestaurantsMutableLiveData() {
        List<Restaurant> restaurants = mConnectedActivityViewModel.getRestaurantsMutableLiveData().getValue();
        Mockito.verify(mConnectedActivityRepository).getRestaurantsMutableLiveData();
        assertEquals("Restaurant1", restaurants.get(1).getName());
    }

    @Test
    public void updateUserRestaurantChoice() {
        assertEquals("123", currentUser.getLunchChoiceId());
        assertEquals("Restaurant1", currentUser.getLunchChoiceName());

        mConnectedActivityViewModel.updateUserRestaurantChoice("04", "Restaurant5", LocalDateTime.now());
        Mockito.verify(mAuthenticationRepository).updateUserRestaurantChoice(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), any(LocalDateTime.class));
        assertEquals("04", currentUser.getLunchChoiceId());
        assertEquals("Restaurant5", currentUser.getLunchChoiceName());
    }

    @Test
    public void updateUserRestaurantFavorite() {
        mConnectedActivityViewModel.updateUserRestaurantFavorite("01", "add");
        Mockito.verify(mAuthenticationRepository).updateUserRestaurantFavorite(anyString(), anyString());
    }

    @Test
    public void updateAttending() {
        mConnectedActivityViewModel.updateAttending(workmateList);
        Mockito.verify(mConnectedActivityRepository).updateAttending(anyList());
    }

    @Test
    public void resetNearbyRestaurants() {
        mConnectedActivityViewModel.resetNearbyRestaurants();
        Mockito.verify(mConnectedActivityRepository).resetNearbyRestaurants();
    }

    @Test
    public void autocomplete() {
        mConnectedActivityViewModel.autocomplete("rest");
        Mockito.verify(mConnectedActivityRepository).autocomplete(anyString());
    }

    @Test
    public void setCurrentLocation() {
        assertEquals(0.0, currentLocation.getLongitude(), 0.01);

        Location newLocation = Mockito.mock(Location.class);
        Mockito.doReturn(5.5).when(newLocation).getLongitude();

        mConnectedActivityViewModel.setCurrentLocation(newLocation);
        Mockito.verify(mConnectedActivityRepository).setCurrentLocation(any(Location.class));
        assertEquals(5.5, currentLocation.getLongitude(), 0.01);
    }

    private void setUpRepositoryMethods(){
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                isUserSignedIn = false;
                return(null);
            }
        }).when(mAuthenticationRepository).signOut();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String newId = (String)invocation.getArguments()[0];
                String newChoiceName = (String)invocation.getArguments()[1];
                LocalDateTime timeStamp = (LocalDateTime)invocation.getArguments()[2];

                currentUser.setChoiceTimeStamp(timeStamp.toString());
                currentUser.setLunchChoiceId(newId);
                currentUser.setLunchChoiceName(newChoiceName);

                return(null);
            }
        }).when(mAuthenticationRepository).updateUserRestaurantChoice(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), any(LocalDateTime.class));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                currentLocation = (Location)invocation.getArguments()[0];
                return(null);
            }
        }).when(mConnectedActivityRepository).setCurrentLocation(any(Location.class));
        Mockito.doReturn(isUserSignedInMutable).when(mAuthenticationRepository).getIsUserSignedIn();

        MutableLiveData<User> currentUserMutable= Mockito.spy(new MutableLiveData<>(currentUser));
        Mockito.doReturn((currentUserMutable)).when(mAuthenticationRepository).getCurrentUserMutableLiveData();

        MutableLiveData<List<User>> mutableWorkmates = Mockito.spy(new MutableLiveData<>(workmateList));
        Mockito.doReturn(mutableWorkmates).when(mAuthenticationRepository).getWorkmatesMutableLiveData();

        MutableLiveData<List<Restaurant>> restaurantsMutable = Mockito.spy(new MutableLiveData<>(restaurantList));
        Mockito.doReturn(restaurantsMutable).when(mConnectedActivityRepository).getRestaurantsMutableLiveData();
    }
    private void generateUser(){
        currentUser = new User();
        currentUser.setEmail("fab@gmail.com");
        currentUser.setDisplayName("Fabien");
        currentUser.setLunchChoiceId("123");
        currentUser.setLunchChoiceName("Restaurant1");
        currentUser.setChoiceTimeStamp("2023-08-04T12:02:55.959097");
    }
    private void generateWorkmates(){
        workmateList.add(new User());
        workmateList.get(0).setDisplayName("Fabien Duncan");
        workmateList.add(new User());
        workmateList.get(1).setDisplayName("Marion Chenus");
        workmateList.add(new User());
        workmateList.get(2).setDisplayName("Bob");
    }
    private void generateRestaurants(){
        for(int i =0; i < 3; i++){
            restaurantList.add(new Restaurant());
            restaurantList.get(i).setName("Restaurant" + i);
        }

    }
}